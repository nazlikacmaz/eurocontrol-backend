package com.eurocontrol.slots.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eurocontrol.slots.entity.FlightStatus;

public interface FlightStatusRepository extends JpaRepository<FlightStatus, Short> {

}
