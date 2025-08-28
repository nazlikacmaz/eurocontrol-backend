package com.eurocontrol.slots.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eurocontrol.slots.entity.Airport;

public interface AirportRepository extends JpaRepository<Airport, String> {

	Optional<Airport> findByIcao(String originIcao);
}