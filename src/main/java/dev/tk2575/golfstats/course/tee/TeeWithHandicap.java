package dev.tk2575.golfstats.course.tee;

import dev.tk2575.golfstats.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.RoundingMode.HALF_UP;

@Getter
@ToString
public class TeeWithHandicap implements TeeHandicap {

	private static final BigDecimal BASE_SLOPE = BigDecimal.valueOf(113);

	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;
	private final Integer holeCount;
	private final Map<String,Integer> handicapStrokes;

	public TeeWithHandicap(Tee tee, List<Golfer> golfers) {
		this.name = tee.getName();
		this.rating = tee.getRating();
		this.slope = tee.getSlope();
		this.par = tee.getPar();
		this.holeCount = tee.getHoleCount();
		this.handicapStrokes = computeHandicapStrokes(golfers);
	}

	private Map<String,Integer> computeHandicapStrokes(List<Golfer> golfers) {
		Map<String,Integer> results = new HashMap<>();
		BigDecimal slopeDiff, result;

		for (Golfer golfer : golfers) {
			//Handicap Index x (Slope Rating / 113) + (Course Rating – par)
			slopeDiff = this.slope.divide(BASE_SLOPE, 2, HALF_UP);
			result = golfer.getHandicapIndex().getValue().multiply(slopeDiff).add((this.rating.subtract(BigDecimal.valueOf(this.getPar()))));

			//course handicap formula assumes 18 hole rounds, so divide by two for nine hole rounds
			//no idea about rounds that aren't either 9 or 18 holes
			if (this.holeCount <= 9) result = result.divide(BigDecimal.valueOf(2L), 2, HALF_UP);
			results.put(golfer.getKey(), result.setScale(0, HALF_UP).intValue());
		}
		return results;
	}
}
