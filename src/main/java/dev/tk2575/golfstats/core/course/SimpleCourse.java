package dev.tk2575.golfstats.core.course;

import dev.tk2575.golfstats.core.course.tee.Tee;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
class SimpleCourse implements Course {

	private static final String UNKNOWN = "UNKNOWN";

	private final String name;
	private final String location;
	private final List<Tee> tees;

	SimpleCourse(String name) {
		this.name = name;
		this.location = UNKNOWN;
		this.tees = new ArrayList<>();
	}

	SimpleCourse(String name, List<Tee> tees) {
		this.name = name;
		this.location = UNKNOWN;
		this.tees = tees;
	}

	@Override
	public Course setTees(@NonNull List<Tee> tees) {
		return this.toBuilder().tees(tees).build();
	}
}
