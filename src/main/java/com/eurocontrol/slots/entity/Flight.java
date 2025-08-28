package com.eurocontrol.slots.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "flights")
public class Flight {
	@Id
	@GeneratedValue
	private UUID id;
	@Column(nullable = false)
	private String callsign;
	private String airline;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origin", referencedColumnName = "icao", nullable = false)
	private Airport origin;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination", referencedColumnName = "icao", nullable = false)
	private Airport destination;
	@Column(nullable = false)
	private OffsetDateTime eobt;
	private OffsetDateTime ctot;
	@Column(name = "delay_minutes", nullable = false)
	private int delayMinutes = 0;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight_status_id", nullable = false)
	private FlightStatus status;
	private Integer priority;
	@Column(name = "user_id")
	private UUID userId;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public Airport getOrigin() {
		return origin;
	}

	public void setOrigin(Airport origin) {
		this.origin = origin;
	}

	public Airport getDestination() {
		return destination;
	}

	public void setDestination(Airport destination) {
		this.destination = destination;
	}

	public OffsetDateTime getEobt() {
		return eobt;
	}

	public void setEobt(OffsetDateTime eobt) {
		this.eobt = eobt;
	}

	public OffsetDateTime getCtot() {
		return ctot;
	}

	public void setCtot(OffsetDateTime ctot) {
		this.ctot = ctot;
	}

	public int getDelayMinutes() {
		return delayMinutes;
	}

	public void setDelayMinutes(int delayMinutes) {
		this.delayMinutes = delayMinutes;
	}

	public FlightStatus getStatus() {
		return status;
	}

	public void setStatus(FlightStatus status) {
		this.status = status;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

}
