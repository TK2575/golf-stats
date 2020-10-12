package dev.tk2575.golfstats.coursehandicap;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.Tee;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public class SingleGolferCourseHandicap implements CourseHandicap {

	@Getter(AccessLevel.NONE)
	private final Golfer golfer;
	private final Course course;
	private final Tee tee;

	private final Integer handicapStrokes;

	public SingleGolferCourseHandicap(Golfer golfer, Course course, Tee tee) {
		this.golfer = golfer;
		this.course = course;
		this.tee = tee;

		this.handicapStrokes = computeHandicapStrokes(golfer);
	}

	@Override
	public List<Golfer> getGolfers() {
		return List.of(this.golfer);
	}

	@Override
	public Map<String, Integer> getHandicapStrokesPerGolfer() {
		return Map.of(this.golfer.getName(), this.getHandicapStrokes());
	}
}
