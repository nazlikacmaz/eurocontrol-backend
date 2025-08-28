package com.eurocontrol.slots.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eurocontrol.slots.entity.Airport;
import com.eurocontrol.slots.repo.AirportRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

record CreateAirportRequest(@NotBlank String icao, @NotBlank String name, @Positive int capacityPer15) {
}

@RestController
@RequestMapping("/api/airports")
public class AirportController {
	private final AirportRepository repo;

	public AirportController(AirportRepository repo) {
		this.repo = repo;
	}

	@GetMapping
	public List<Airport> list() {
		return repo.findAll(Sort.by("icao").ascending());
	}

	@PostMapping
	public ResponseEntity<Airport> create(@RequestBody CreateAirportRequest req) {
		var a = new Airport();
		a.setIcao(req.icao());
		a.setName(req.name());
		a.setCapacityPer15(req.capacityPer15());
		a = repo.save(a);
		return ResponseEntity.created(URI.create("/api/airports/" + a.getIcao())).body(a);
	}
}
