package dev.tk2575.golfstats.golfround;

import lombok.*;

@Getter
public class CompositeCourse implements Course {
	private String name;
	private String location;

	public CompositeCourse(Course course1, Course course2) {
		this.name = course1.getName();
		if (!this.name.equalsIgnoreCase(course2.getName())) {
			this.name = String.join(" - ", this.name, course2.getName());
		}

		this.location = course1.getLocation();
		if (!this.location.equalsIgnoreCase(course2.getLocation())) {
			this.location = String.join(" - ", this.location, course2.getLocation());
		}
	}
}
