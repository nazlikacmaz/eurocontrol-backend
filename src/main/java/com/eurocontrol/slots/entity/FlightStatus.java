package com.eurocontrol.slots.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flight_status")
public class FlightStatus {

	@Id
	@Column(name = "id")
	private Short id; // SMALLINT

	@Column(name = "code", nullable = false, unique = true)
	private String code; // 'PLANNED','ALLOCATED',...

	@Column(name = "label", nullable = false)
	private String label;

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
