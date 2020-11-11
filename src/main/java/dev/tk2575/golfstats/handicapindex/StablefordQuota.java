package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.tee.Tee;
import lombok.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ToString
@Getter
public class StablefordQuota {

	private final Collection<Golfer> golfers;
	private final Tee tee;
	private final Map<String, Integer> quotaPerGolfer;
	private final Integer totalQuota;

	public StablefordQuota(Collection<Golfer> golfers, Tee tee) {
		this.golfers = golfers;
		this.tee = tee.handicapOf(golfers);
		this.quotaPerGolfer = computeQuotaPerGolfer();
		this.totalQuota = this.quotaPerGolfer.values().stream().reduce(0, Integer::sum);
	}

	private Map<String, Integer> computeQuotaPerGolfer() {
		Map<String, Integer> results = new HashMap<>();
		Integer scratch = this.tee.getHoleCount() * 2;
		for (Map.Entry<String, Integer> each : this.tee.getHandicapStrokes().entrySet()) {
			results.put(each.getKey(), Math.max(0,scratch - each.getValue()));
		}
		return results;
	}

}
