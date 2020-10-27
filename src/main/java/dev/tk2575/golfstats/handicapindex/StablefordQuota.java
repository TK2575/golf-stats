package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.course.tee.TeeHandicap;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
@Getter
public class StablefordQuota {

	//TODO move to generic Collection or Set where possible/appropriate
	private final List<Golfer> golfers;
	private final TeeHandicap tee;
	private final Map<String, Integer> quotaPerGolfer;
	private final Integer totalQuota;

	public <T extends Tee> StablefordQuota(List<Golfer> golfers, T tee) {
		this.golfers = golfers;

		if (TeeHandicap.class.isAssignableFrom(tee.getClass())) {
			this.tee = (TeeHandicap) tee;
		}
		else {
			this.tee = tee.handicapOf(golfers);
		}

		this.quotaPerGolfer = computeQuotaPerGolfer();
		this.totalQuota = this.quotaPerGolfer.values().stream().reduce(0, Integer::sum);
	}

	private Map<String, Integer> computeQuotaPerGolfer() {
		Map<String, Integer> results = new HashMap<>();
		//TODO encapsulate potentially varying stableford values?
		Integer scratch = this.tee.getHoleCount() * 2;
		for (Map.Entry<String, Integer> each : this.tee.getHandicapStrokes().entrySet()) {
			results.put(each.getKey(), Math.max(0,scratch - each.getValue()));
		}
		return results;
	}

}
