package dev.tk2575.golfstats.details.imports;

import java.util.Comparator;
import java.util.List;

public class Hole19Export {
	private List<Hole19Round> data;

	public List<Hole19Round> getData() {
		return data.stream().sorted(Comparator.comparing(Hole19Round::getStartedAt)).toList();
	}
}
