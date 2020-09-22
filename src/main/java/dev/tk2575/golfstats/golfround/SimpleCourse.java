package dev.tk2575.golfstats.golfround;

import lombok.*;

@Getter
public class SimpleCourse implements Course {

	private static final String UNKNOWN = "UNKNOWN";

	private final String name;
	private final String location;

	public SimpleCourse(String name) {
		this.name = name;
		this.location = UNKNOWN;
	}
}
