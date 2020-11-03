package dev.tk2575.golfstats.golferperformance;

import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.golfround.GolfRound;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface GolferPerformance {

	String getGolfer();

	Collection<GolfRound> getGolfRounds();

	HandicapIndex getHandicapIndex();

	HandicapIndex getTrendingHandicap();

	LocalDate asOf();

	BigDecimal getFairwaysInRegulation();

	BigDecimal getGreensInRegulation();

	BigDecimal getPuttsPerHole();

	Long getMinutesPer18Holes();

	default String toStringDefault() {
		List<String> fields = new ArrayList<>();
		fields.add(String.format("golfer=%s", getGolfer()));
		fields.add(String.format("handicapIndex=%s", getHandicapIndex().toString()));
		fields.add(String.format("trendingHandicap=%s", getTrendingHandicap().toString()));
		fields.add(String.format("fairwaysInRegulation=%s", getFairwaysInRegulation()));
		fields.add(String.format("greensInRegulation=%s", getGreensInRegulation()));
		fields.add(String.format("puttsPerHole=%s", getPuttsPerHole()));
		fields.add(String.format("minutesPer18Holes=%s", getMinutesPer18Holes()));
		fields.add(String.format("asOf=%s", asOf()));

		return this.getClass()
		           .getSimpleName() + "(" + String.join(", ", fields) + ")";
	}
}
