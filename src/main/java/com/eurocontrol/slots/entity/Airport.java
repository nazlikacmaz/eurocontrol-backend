package com.eurocontrol.slots.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "airports")
public class Airport {
	@Id
	@Column(nullable = false, unique = true)
	private String icao;
	@Column(nullable = false)
	private String name;
	@Column(name = "capacity_per_15", nullable = false)
	private int capacityPer15;


	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacityPer15() {
		return capacityPer15;
	}

	public void setCapacityPer15(int capacityPer15) {
		this.capacityPer15 = capacityPer15;
	}

}
