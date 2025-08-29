package com.eurocontrol.slots.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eurocontrol.slots.dto.AllocationSummary;
import com.eurocontrol.slots.entity.Airport;
import com.eurocontrol.slots.entity.Flight;
import com.eurocontrol.slots.entity.FlightStatus;
import com.eurocontrol.slots.repo.AirportRepository;
import com.eurocontrol.slots.repo.FlightRepository;
import com.eurocontrol.slots.repo.FlightStatusRepository;

@Service
public class SlotService {

	private static final int BAND_MINUTES = 15;

	private final FlightRepository flightRepo;
	private final AirportRepository airportRepo;
	private final FlightStatusRepository flightStatusRepo;

	@Autowired
	public SlotService(FlightRepository flightRepo, AirportRepository airportRepo,
			FlightStatusRepository flightStatusRepo) {
		this.flightRepo = flightRepo;
		this.airportRepo = airportRepo;
		this.flightStatusRepo = flightStatusRepo;
	}

	@Transactional
	public AllocationSummary allocateAllPending() {
		return allocateInternal(null);
	}

	@Transactional
	public AllocationSummary allocateForAirport(String originIcao) {
		return allocateInternal(originIcao);
	}

	@Transactional(readOnly = true)
	public AllocationSummary currentSummary(String originIcao) {
		Map<String, Integer> capByIcao = loadCapacities(originIcao);

		Map<String, Integer> counter = new HashMap<>();
		flightsFiltered(originIcao).stream().filter(f -> isAllocated().test(f) && f.getCtot() != null).forEach(
				f -> counter.merge(key(f.getOrigin().getIcao(), truncateToBand(f.getCtot())), 1, Integer::sum));

		List<Flight> all = flightsFiltered(originIcao);
		long allocatedCount = all.stream().filter(isAllocated()).count();
		double avgDelay = all.stream().filter(isAllocated())
				.mapToInt(f -> Optional.ofNullable(f.getDelayMinutes()).orElse(0)).average().orElse(0.0);

		double util = estimateUtilization(counter, capByIcao);
		return new AllocationSummary(all.size(), (int) allocatedCount, avgDelay, util);
	}

	@Transactional
	protected AllocationSummary allocateInternal(String originIcao) {
		Map<String, Integer> capByIcao = loadCapacities(originIcao);

		Map<String, Integer> counter = loadAllocatedCounter(originIcao);

		List<Flight> planned = plannedFlightsOrdered(originIcao);

		int processed = 0, newlyAllocated = 0;
		long totalDelay = 0;
		FlightStatus allocatedStatus = flightStatusRepo.getById((short) 2);

		for (Flight f : planned) {
			processed++;

			int cap = Optional.ofNullable(f.getOrigin().getCapacityPer15()).orElse(0);
			if (cap <= 0)
				continue;

			OffsetDateTime eobtUtc = f.getEobt().withOffsetSameInstant(ZoneOffset.UTC);
			OffsetDateTime band = truncateToBand(eobtUtc);

			String k = key(f.getOrigin().getIcao(), band);
			while (counter.getOrDefault(k, 0) >= cap) {
				band = band.plusMinutes(BAND_MINUTES);
				k = key(f.getOrigin().getIcao(), band);
			}

			f.setCtot(band);
			int delay = (int) Duration.between(eobtUtc, band).toMinutes();
			f.setDelayMinutes(Math.max(delay, 0));
			f.setStatus(allocatedStatus);
			flightRepo.save(f);

			counter.merge(k, 1, Integer::sum);
			newlyAllocated++;
			totalDelay += Math.max(delay, 0);
		}

		double avgDelay = newlyAllocated > 0 ? (double) totalDelay / newlyAllocated : 0.0;
		double util = estimateUtilization(counter, capByIcao);
		return new AllocationSummary(processed, newlyAllocated, avgDelay, util);
	}

	private Map<String, Integer> loadCapacities(String originIcao) {
		List<Airport> airports = (originIcao == null || originIcao.isBlank()) ? airportRepo.findAll()
				: List.of(airportRepo.findByIcao(originIcao)
						.orElseThrow(() -> new IllegalArgumentException("Unknown ICAO: " + originIcao)));
		return airports.stream()
				.collect(Collectors.toMap(Airport::getIcao, a -> Optional.ofNullable(a.getCapacityPer15()).orElse(0)));
	}

	private Map<String, Integer> loadAllocatedCounter(String originIcao) {
		Map<String, Integer> counter = new HashMap<>();
		flightsFiltered(originIcao).stream().filter(f -> isAllocated().test(f) && f.getCtot() != null).forEach(
				f -> counter.merge(key(f.getOrigin().getIcao(), truncateToBand(f.getCtot())), 1, Integer::sum));
		return counter;
	}

	private List<Flight> flightsFiltered(String originIcao) {
		return (originIcao == null || originIcao.isBlank()) ? flightRepo.findAllWithStatusAndAirports()
				: flightRepo.findAllByOriginIcao(originIcao);
	}

	private List<Flight> plannedFlightsOrdered(String originIcao) {
		return flightsFiltered(originIcao).stream().filter(isPlanned()).sorted(Comparator.comparing(Flight::getEobt)
				.thenComparing((Flight f) -> Optional.ofNullable(f.getPriority()).orElse(0), Comparator.reverseOrder())
				.thenComparing(Flight::getId)).toList();
	}

	private static Predicate<Flight> isPlanned() {
		return f -> f.getStatus() != null && "PLANNED".equals(f.getStatus().getCode());
	}

	private static Predicate<Flight> isAllocated() {
		return f -> f.getStatus() != null && "ALLOCATED".equals(f.getStatus().getCode());
	}

	private static OffsetDateTime truncateToBand(OffsetDateTime t) {
		int bandMinute = (t.getMinute() / BAND_MINUTES) * BAND_MINUTES;
		return t.truncatedTo(ChronoUnit.HOURS).plusMinutes(bandMinute).withSecond(0).withNano(0);
	}

	private static String key(String icao, OffsetDateTime band) {
		return icao + "|" + band.toString();
	}

	/** Basit kapasite kullanım oranı (0–100) */
	private static double estimateUtilization(Map<String, Integer> counter, Map<String, Integer> capByIcao) {
		if (counter.isEmpty())
			return 0.0;
		double sum = 0.0;
		for (Map.Entry<String, Integer> e : counter.entrySet()) {
			String icao = e.getKey().substring(0, e.getKey().indexOf('|'));
			int used = e.getValue();
			int cap = Math.max(1, capByIcao.getOrDefault(icao, 1));
			sum += Math.min(1.0, (double) used / cap);
		}
		return (sum / counter.size()) * 100.0;
	}
}
