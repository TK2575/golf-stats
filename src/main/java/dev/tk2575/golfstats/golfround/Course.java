package dev.tk2575.golfstats.golfround;

public interface Course {

	static Course newCourse(String courseName) {
		return new SimpleCourse(courseName);
	}

	static Course compositeCourse(Course course1, Course course2) {
		if (course1.equals(course2)) {
			return course1;
		}
		return new CompositeCourse(course1, course2);
	}

	String getName();

	String getLocation();

}
