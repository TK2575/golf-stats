package dev.tk2575.golfstats.coursehandicap;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.Tee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public interface CourseHandicap {

	List<Golfer> getGolfers();

	Course getCourse();

	Tee getTee();

	Integer getHandicapStrokes();

	Map<String,Integer> getHandicapStrokesPerGolfer();

	default Integer computeHandicapStrokes(Golfer golfer) {
		//Handicap Index x (Slope Rating / 113) + (Course Rating – par)
		BigDecimal index = golfer.getHandicapIndex().getValue();
		BigDecimal slope = getTee().getSlope();
		BigDecimal baseSlope = new BigDecimal("113");
		BigDecimal rating = getTee().getRating();
		BigDecimal par = BigDecimal.valueOf(getTee().getPar());

		BigDecimal slopeDiff = slope.divide(baseSlope, 2, RoundingMode.HALF_UP);
		BigDecimal result = index.multiply(slopeDiff).add((rating.subtract(par)));
		return result.setScale(0, RoundingMode.HALF_UP).intValue();
	}

	default Integer getStablefordQuota() {
		return getTee().getHoleCount() * 2 * getGolfers().size() - getHandicapStrokes();
	}

	static CourseHandicap of(Golfer golfer, Course course, Tee tee) {
		return new SingleGolferCourseHandicap(golfer, course, tee);
	}

	static CourseHandicap teamOf(List<Golfer> golfers, Course course, Tee tee) {
		return new TeamCourseHandicap(golfers, course, tee);
	}
}
