package dev.tk2575.golfstats.core.course;

import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.course.tee.Tee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Course {

	String getName();

	String getLocation();

	Collection<Tee> getTees();

	static Course of(String courseName) {
		return new SimpleCourse(courseName);
	}

	static Course of(String name, String city, String stateAbbrev) {
		return new CityStateCourse(name, city, stateAbbrev);
	}

	static Course of(String name, List<Tee> tees, String city, String stateAbbrev) {
		return new CityStateCourse(name, tees, city, stateAbbrev);
	}

	static Course of(String courseName, List<Tee> tees) {
		return new SimpleCourse(courseName, tees);
	}

	static Course of(String courseName, Tee tee) {
		return new SimpleCourse(courseName, List.of(tee));
	}

	static Course compositeOf(Course course1, Course course2) {
		if (course1.equals(course2)) {
			return course1;
		}
		return new CompositeCourse(course1, course2);
	}

	default List<Tee> handicapOf(Golfer golfer) {
		return handicapOf(List.of(golfer));
	}

	default List<Tee> handicapOf(List<Golfer> golfers) {
		//TODO one liner via stream?
		List<Tee> handicaps = new ArrayList<>();
		getTees().forEach(t -> handicaps.add(t.handicapOf(golfers)));
		return handicaps;
	}

}
