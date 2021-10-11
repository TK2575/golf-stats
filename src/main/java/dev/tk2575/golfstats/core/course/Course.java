package dev.tk2575.golfstats.core.course;

import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Course {

	String getName();

	String getLocation();

	List<Tee> getTees();

	Course setTees(@NonNull List<Tee> tees);

	static Course of(String courseName) {
		return new SimpleCourse(courseName);
	}

	static Course of(String name, String city, String stateAbbrev) {
		return new CityStateCourse(name, city, stateAbbrev);
	}

	static Course of(String name, List<Tee> tees, String city, String stateAbbrev) {
		return new CityStateCourse(name, tees, city, stateAbbrev);
	}

	static Course of(String name, Tee tee, String city, String stateAbbrev) {
		return new CityStateCourse(name, List.of(tee), city, stateAbbrev);
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
		return getTees().stream().map(t -> t.handicapOf(golfers)).toList();
	}

    default Optional<Tee> getTee(@NonNull String tee) {
		return getTees().stream().filter(each -> each.getName().toLowerCase().equals(tee.toLowerCase())).findAny();
	}
}
