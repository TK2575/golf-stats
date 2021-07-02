package dev.tk2575.golfstats.details.api.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	@ToString.Exclude
	private final List<GolfRound> golfRounds;

	private final String golfer;
	private final BigDecimal fairwaysInRegulation;
	private final BigDecimal greensInRegulation;
	private final BigDecimal puttsPerHole;
	private final Long minutesPer18Holes;
	private final Long roundCount;
	private final LocalDate from;
	private final LocalDate asOf;

	private final BigDecimal handicapIndex;
	private final BigDecimal trendingHandicap;
	private final BigDecimal antiHandicap;
	private final BigDecimal medianHandicap;

	@ToString.Exclude
	private final List<RoundSummary> rounds = new ArrayList<>();

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		this.golfRounds = GolfRound.stream(roundsUnsorted).compileTo18HoleRounds().asList();
		//FIXME what if there are 0 18 hole rounds?

		this.golfer = rounds().golferNames();
		this.from = rounds().getOldestRound().getDate();
		this.asOf = rounds().getMostRecentRound().getDate();
		this.fairwaysInRegulation = rounds().getFairwaysInRegulation();
		this.greensInRegulation = rounds().getGreensInRegulation();
		this.puttsPerHole = rounds().getPuttsPerHole();
		this.minutesPer18Holes = rounds().getMinutesPerRound();
		this.roundCount = rounds().count();

		this.handicapIndex = HandicapIndex.newIndex(this.golfRounds).getValue();
		this.trendingHandicap = HandicapIndex.lastFiveRoundsTrendingHandicap(this.golfRounds).getValue();
		this.antiHandicap = HandicapIndex.antiHandicapOf(this.golfRounds).getValue();
		this.medianHandicap = HandicapIndex.medianHandicap(this.golfRounds).getValue();

		int i = 1;
		for (GolfRound each : this.golfRounds) {
			rounds.add(new RoundSummary(i, each));
			i++;
		}
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
