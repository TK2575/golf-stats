package dev.tk2575.golfstats.course.tee;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.handicapindex.StablefordQuota;

import java.math.BigDecimal;
import java.util.List;

public interface Tee {

	String getName();

	BigDecimal getRating();

	BigDecimal getSlope();

	Integer getPar();

	Integer getHoleCount();

	static Tee of(String teeName, BigDecimal courseRating, BigDecimal slope, Integer par) {
		return new SimpleTee(teeName, courseRating, slope, par);
	}

	static Tee of(String teeName, BigDecimal courseRating, BigDecimal slope, Integer par, Long yards) {
		return new TeeWithYardage(teeName, courseRating, slope, par, yards);
	}

	static Tee compositeOf(Tee tee1, Tee tee2) {
		if (tee1.equals(tee2)) {
			return tee1;
		}
		return new CompositeTee(tee1, tee2);
	}

	default TeeHandicap handicapOf(Golfer golfer) {
		return new TeeWithHandicap(this, List.of(golfer));
	}

	default TeeHandicap handicapOf(List<Golfer> golfers) {
		return new TeeWithHandicap(this, golfers);
	}

	default StablefordQuota stablefordQuota(Golfer golfer) { return new StablefordQuota(List.of(golfer), this); }

	default StablefordQuota stablefordQuota(List<Golfer> golfers) { return new StablefordQuota(golfers, this); }

}
