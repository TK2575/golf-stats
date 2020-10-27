package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Utils;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CompositeCourse implements Course {
	private final String name;
	private final String location;
	private final List<Tee> tees;

	public CompositeCourse(Course course1, Course course2) {
		this.name = Utils.joinByHyphenIfUnequal(course1.getName(), course2.getName());
		this.location = Utils.joinByHyphenIfUnequal(course1.getLocation(), course2.getLocation());
		this.tees = new ArrayList<>();
	}
}
