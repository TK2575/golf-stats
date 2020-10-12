package dev.tk2575.golfstats.coursehandicap;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.Tee;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
public class TeamCourseHandicap implements CourseHandicap {

	private final List<Golfer> golfers;
	private final Course course;
	private final Tee tee;

	private final Map<String,Integer> handicapStrokesPerGolfer;
	private final Integer handicapStrokes;

	public TeamCourseHandicap(List<Golfer> golfers, Course course, Tee tee) {
		this.golfers = golfers;
		this.course = course;
		this.tee = tee;

		this.handicapStrokesPerGolfer = computeHandicapStrokesPerGolfer();
		this.handicapStrokes = this.handicapStrokesPerGolfer.values().stream().reduce(0, Integer::sum);
	}

	private Map<String, Integer> computeHandicapStrokesPerGolfer() {
		//TODO one-liner with stream?
		Map<String,Integer> results = new HashMap<>();
		this.golfers.forEach(g -> results.put(g.getName(), computeHandicapStrokes(g)));
		return results;
	}

}
