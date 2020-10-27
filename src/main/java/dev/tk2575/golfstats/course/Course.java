package dev.tk2575.golfstats.course;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.course.tee.TeeHandicap;
import dev.tk2575.golfstats.golfround.CompositeCourse;

import java.util.ArrayList;
import java.util.List;

public interface Course {

	String getName();

	String getLocation();

	List<Tee> getTees();

	static Course of(String courseName) {
		return new SimpleCourse(courseName);
	}

	static Course of(String courseName, List<Tee> tees) {
		return new SimpleCourse(courseName, tees);
	}

	static Course compositeOf(Course course1, Course course2) {
		if (course1.equals(course2)) {
			return course1;
		}
		return new CompositeCourse(course1, course2);
	}

	default List<TeeHandicap> handicapOf(Golfer golfer) {
		return handicapOf(List.of(golfer));
	}

	default List<TeeHandicap> handicapOf(List<Golfer> golfers) {
		//TODO one liner via stream?
		List<TeeHandicap> handicaps = new ArrayList<>();
		getTees().forEach(t -> handicaps.add(t.handicapOf(golfers)));
		return handicaps;
	}

}
