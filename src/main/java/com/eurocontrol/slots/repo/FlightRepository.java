package com.eurocontrol.slots.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eurocontrol.slots.entity.Flight;
import com.eurocontrol.slots.entity.FlightStatus;

public interface FlightRepository extends JpaRepository<Flight, UUID> {

	// Status code ile filtre (en pratik)
	@Query("""
			  SELECT f FROM Flight f
			  WHERE f.status.code = :code
			  ORDER BY f.eobt ASC, COALESCE(f.priority,0) DESC, f.id ASC
			""")
	List<Flight> findPlannedOrderedByCode(@Param("code") String code);

	@Query("""
			  SELECT f FROM Flight f
			  JOIN FETCH f.status s
			  ORDER BY f.eobt ASC, COALESCE(f.priority,0) DESC, f.id ASC
			""")
	List<Flight> findAllWithStatusFetched();

	List<Flight> findByStatus(FlightStatus status);

	List<Flight> findByStatus_Id(Short statusId);

	List<Flight> findByStatus_Code(String code);

	@Query("""
			  SELECT f FROM Flight f
			  JOIN FETCH f.status s
			  JOIN FETCH f.origin o
			  JOIN FETCH f.destination d
			  WHERE o.icao = :icao
			  ORDER BY f.eobt ASC, COALESCE(f.priority,0) DESC, f.id ASC
			""")
	List<Flight> findAllByOriginIcao(@Param("icao") String icao);

	@Query("""
			  SELECT f FROM Flight f
			  JOIN FETCH f.status s
			  JOIN FETCH f.origin o
			  JOIN FETCH f.destination d
			  ORDER BY f.eobt ASC, COALESCE(f.priority,0) DESC, f.id ASC
			""")
	List<Flight> findAllWithStatusAndAirports();
}
