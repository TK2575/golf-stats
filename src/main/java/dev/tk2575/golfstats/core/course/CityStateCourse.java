package dev.tk2575.golfstats.core.course;

import dev.tk2575.golfstats.core.course.tee.Tee;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static dev.tk2575.Utils.isNullOrBlank;

public class CityStateCourse implements Course {

	@Getter private final String name;
	@Getter private final List<Tee> tees;
	private final String city;
	private final String stateAbbrev;

	public CityStateCourse(String name, String city, String stateAbbrev) {
		this(name, new ArrayList<>(), city, stateAbbrev);
	}

	public CityStateCourse(String name, List<Tee> tees, String city, String stateAbbrev) {
		if (isNullOrBlank(name) || tees == null || isNullOrBlank(city) || isNullOrBlank(stateAbbrev)) {
			throw new IllegalArgumentException("name, tees, city, and stateAbbrev are required arguments");
		}

		this.name = name;
		this.tees = tees;
		this.city = city;
		this.stateAbbrev = stateAbbrev;
	}

	@Override
	public String getLocation() {
		return String.format("%s, %s", this.city, this.stateAbbrev);
	}
}
