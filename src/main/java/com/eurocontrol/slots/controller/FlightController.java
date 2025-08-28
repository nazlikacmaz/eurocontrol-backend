package com.eurocontrol.slots.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eurocontrol.slots.dto.FlightDto;
import com.eurocontrol.slots.entity.Airport;
import com.eurocontrol.slots.entity.Flight;
import com.eurocontrol.slots.entity.FlightStatus;
import com.eurocontrol.slots.repo.AirportRepository;
import com.eurocontrol.slots.repo.FlightRepository;
import com.eurocontrol.slots.repo.FlightStatusRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

record CreateFlightRequest(@NotBlank String callsign, String airline, @NotBlank String origin,
		@NotBlank String destination, @NotNull OffsetDateTime eobt, Integer priority, UUID userId) {
}

@RestController
@RequestMapping("/api/flights")
public class FlightController {
	private final FlightRepository repo;
	private final FlightStatusRepository flightStatusRepo;
	private final AirportRepository airportRepo;

	public FlightController(FlightRepository repo, FlightStatusRepository flightStatusRepo,
			AirportRepository airportRepo) {
		this.repo = repo;
		this.flightStatusRepo = flightStatusRepo;
		this.airportRepo = airportRepo;
	}

	@GetMapping
	public List<FlightDto> list(@RequestParam(required = false) String origin) {
		List<Flight> flights = (origin == null || origin.isBlank()) ? repo.findAllWithStatusAndAirports()
				: repo.findAllByOriginIcao(origin);
		return flights.stream().map(FlightDto::from).toList();
	}

	@PostMapping
	public Flight create(@RequestBody CreateFlightRequest req) {
		var f = new Flight();
		f.setCallsign(req.callsign());
		f.setAirline(req.airline());
		Airport origin = airportRepo.getById(req.origin());
		Airport destination = airportRepo.getById(req.destination());
		f.setOrigin(origin);
		f.setDestination(destination);
		f.setEobt(req.eobt());
		f.setPriority(req.priority());
		f.setUserId(req.userId());
		FlightStatus planned = flightStatusRepo.getReferenceById((short) 1);
		f.setStatus(planned);
		return repo.save(f);
	}
}
