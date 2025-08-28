package com.eurocontrol.slots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eurocontrol.slots.dto.AllocationSummary;
import com.eurocontrol.slots.service.SlotService;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

	private final SlotService slotService;

	@Autowired
	public SlotController(SlotService slotService) {
		this.slotService = slotService;
	}

	@PostMapping("/allocate")
	public AllocationSummary allocate() {
		return slotService.allocateAllPending();
	}

	@PostMapping("/allocate/{originIcao}")
	public AllocationSummary allocateForAirport(@PathVariable String originIcao) {
		return slotService.allocateForAirport(originIcao);
	}

	@GetMapping("/summary")
	public AllocationSummary summary(@RequestParam(required = false) String origin) {
		return slotService.currentSummary(origin);
	}
}
