package dev.tk2575.golfstats.core.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class PerformanceSummary {

	@Getter(AccessLevel.NONE)
	@JsonIgnore
	private final List<GolfRound> golfRounds;

	private final String golfer;
	private final BigDecimal fairwaysInRegulation;
	private final BigDecimal greensInRegulation;
	private final BigDecimal puttsPerHole;
	private final Long minutesPer18Holes;
	private final LocalDate asOf;

	private final HandicapIndex handicapIndex;
	private final HandicapIndex trendingHandicap;
	private final HandicapIndex antiHandicap;
	private final HandicapIndex medianHandicap;

	private final List<RoundSummary> rounds = new ArrayList<>();

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		this.golfRounds = GolfRound.stream(roundsUnsorted).compileTo18HoleRounds().asList();
		//FIXME what if there are 0 18 hole rounds?

		this.golfer = rounds().golferNames();
		this.asOf = rounds().getMostRecentRound().getDate();
		this.fairwaysInRegulation = rounds().getFairwaysInRegulation();
		this.greensInRegulation = rounds().getGreensInRegulation();
		this.puttsPerHole = rounds().getPuttsPerHole();
		this.minutesPer18Holes = rounds().getMinutesPerRound();

		this.handicapIndex = HandicapIndex.newIndex(this.golfRounds);
		this.trendingHandicap = HandicapIndex.lastFiveRoundsTrendingHandicap(this.golfRounds);
		this.antiHandicap = HandicapIndex.antiHandicapOf(this.golfRounds);
		this.medianHandicap = HandicapIndex.medianHandicap(this.golfRounds);

		int i = 1;
		for (GolfRound each : this.golfRounds) {
			rounds.add(new RoundSummary(i, each));
			i++;
		}
	}

	public String toString() {
		List<String> fields = List.of(
			String.format("golfer=%s", getGolfer()),
			String.format("fairwaysInRegulation=%s", getFairwaysInRegulation()),
			String.format("greensInRegulation=%s", getGreensInRegulation()),
			String.format("puttsPerHole=%s", getPuttsPerHole()),
			String.format("minutesPer18Holes=%s", getMinutesPer18Holes()),
			String.format("handicapIndex=%s", getHandicapIndex().getRoundedValue()),
			String.format("trendingHandicap=%s", getTrendingHandicap().getRoundedValue()),
			String.format("antiHandicap=%s", getAntiHandicap().getRoundedValue()),
			String.format("medianHandicap=%s", getMedianHandicap().getRoundedValue()),
			String.format("asOf=%s", getAsOf())
		);

		return this.getClass().getSimpleName() + "(" + String.join(", ", fields) + ")";
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
