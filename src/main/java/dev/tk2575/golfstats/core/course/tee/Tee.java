package dev.tk2575.golfstats.core.course.tee;

import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.handicapindex.StablefordQuota;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

import static java.math.RoundingMode.HALF_UP;

public interface Tee {

	String getName();

	BigDecimal getRating();

	BigDecimal getSlope();

	Integer getPar();

	Integer getHoleCount();

	default Long getYards() {
		return 0L;
	}

	default Integer getHandicapStrokesForGolfer(Golfer golfer) {
		return getHandicapStrokes().get(golfer.getKey());
	}

	default Map<String, Integer> getHandicapStrokes() {
		return new HashMap<>();
	}

	static Tee of(String teeName, BigDecimal courseRating, BigDecimal slope, Integer par) {
		return new SimpleTee(teeName, courseRating, slope, par);
	}

	static Tee of(@NonNull String teeName, @NonNull BigDecimal courseRating, @NonNull BigDecimal slope) {
		return new TeeWithoutPar(teeName, courseRating, slope);
	}

	static Tee of(String teeName, BigDecimal courseRating, BigDecimal slope, Integer par, Long yards) {
		return new TeeWithYardage(teeName, courseRating, slope, par, yards);
	}

	static Tee of(Golfer golfer, String teeName, BigDecimal courseRating, BigDecimal slope, Integer par) {
		return of(teeName, courseRating, slope, par).handicapOf(golfer);
	}

	static Tee of(Golfer golfer, String teeName, BigDecimal courseRating, BigDecimal slope, Integer par, Long yards) {
		return of(teeName, courseRating, slope, par, yards).handicapOf(golfer);
	}

	static Tee compositeOf(Tee tee1, Tee tee2) {
		if (tee1.equals(tee2)) {
			return tee1;
		}
		return new CompositeTee(tee1, tee2);
	}

	//TODO remove TeeWithHandicap
	default Tee handicapOf(Golfer golfer) {
		return new TeeWithHandicap(this, List.of(golfer));
	}

	default Tee handicapOf(Collection<Golfer> golfers) {
		return new TeeWithHandicap(this, golfers);
	}

	default StablefordQuota stablefordQuota(Golfer golfer) { return new StablefordQuota(List.of(golfer), this); }

	default StablefordQuota stablefordQuota(List<Golfer> golfers) { return new StablefordQuota(golfers, this); }

	default Integer handicapStrokes(BigDecimal incomingIndex) {
		//Handicap Index x (Slope Rating / 113) + (Course Rating – par)
		BigDecimal slopeDiff = getSlope().divide(BigDecimal.valueOf(113), 2, HALF_UP);
		BigDecimal result = incomingIndex.multiply(slopeDiff).add((getRating().subtract(BigDecimal.valueOf(this.getPar()))));

		//course handicap formula assumes 18 hole rounds, so divide by two for nine hole rounds
		if (getHoleCount() <= 9) result = result.divide(BigDecimal.valueOf(2L), 2, HALF_UP);
		return result.setScale(0, HALF_UP).intValue();
	}

}
