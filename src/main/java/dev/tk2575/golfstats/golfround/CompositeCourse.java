package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Utils;
import lombok.*;

@Getter
public class CompositeCourse implements Course {
	private String name;
	private String location;

	public CompositeCourse(Course course1, Course course2) {
		this.name = Utils.joinByHyphenIfUnequal(course1.getName(), course2.getName());
		this.location = Utils.joinByHyphenIfUnequal(course1.getLocation(), course2.getLocation());
	}
}
