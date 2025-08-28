package com.eurocontrol.slots.dto;

public record AllocationSummary(int totalProcessed, int newlyAllocated, double avgDelayMinutes,
		double capacityUtilizationPct) {
}
