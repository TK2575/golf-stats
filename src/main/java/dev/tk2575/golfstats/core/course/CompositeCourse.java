package dev.tk2575.golfstats.core.course;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.tee.Tee;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@ToString
class CompositeCourse implements Course {
	private final String name;
	private final String location;
	private final Collection<Tee> tees;

	CompositeCourse(Course course1, Course course2) {
		this.name = Utils.joinByHyphenIfUnequal(course1.getName(), course2.getName());
		this.location = Utils.joinByHyphenIfUnequal(course1.getLocation(), course2.getLocation());
		this.tees = new ArrayList<>();
	}
}
