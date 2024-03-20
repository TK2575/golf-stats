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
	
	static Tee of(String name, BigDecimal rating, BigDecimal slope, Integer par, Integer holeCount) {
		return new SimpleTee(name, rating, slope, par, holeCount);
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

	static BigDecimal correctCourseRating(@NonNull Integer par, @NonNull BigDecimal rating) {
		if (rating.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("rating must be a positive non-zero value");
		}
		if (par <= 0) {
			throw new IllegalArgumentException("par must be a positive non-zero value");
		}

		final BigDecimal ratingOverPar = rating.divide(new BigDecimal(par), 1, HALF_UP);
		final BigDecimal two = BigDecimal.valueOf(2);

		BigDecimal distanceFromOne = ratingOverPar.subtract(BigDecimal.ONE).abs();
		BigDecimal distanceFromTwo = ratingOverPar.subtract(two).abs();
		BigDecimal distanceFromPointFive = ratingOverPar.subtract(new BigDecimal(".5")).abs();
		BigDecimal min = distanceFromOne.min(distanceFromTwo.min(distanceFromPointFive));

		if (min.compareTo(distanceFromTwo) == 0) {
			return rating.divide(two, 1, HALF_UP);
		}
		if (min.compareTo(distanceFromPointFive) == 0) {
			return rating.multiply(two);
		}
		return rating;
	}

}
