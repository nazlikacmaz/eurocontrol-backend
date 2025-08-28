package com.eurocontrol.slots.dto;

import com.eurocontrol.slots.entity.Flight;

public record FlightDto(
	    String id,
	    String callsign,
	    String airline,
	    String originIcao,
	    String originName,
	    String destinationIcao,
	    String destinationName,
	    String eobt,
	    String ctot,
	    Integer delayMinutes,
	    String status,
	    Integer priority
	) {
	    public static FlightDto from(Flight f) {
	        return new FlightDto(
	            f.getId().toString(),
	            f.getCallsign(),
	            f.getAirline(),
	            f.getOrigin() != null ? f.getOrigin().getIcao() : null,
	            f.getOrigin() != null ? f.getOrigin().getName() : null,
	            f.getDestination() != null ? f.getDestination().getIcao() : null,
	            f.getDestination() != null ? f.getDestination().getName() : null,
	            f.getEobt() != null ? f.getEobt().toString() : null,
	            f.getCtot() != null ? f.getCtot().toString() : null,
	            f.getDelayMinutes(),
	            f.getStatus() != null ? f.getStatus().getCode() : null,
	            f.getPriority()
	        );
	    }
	}
