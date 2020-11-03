package dev.tk2575.golfstats.golferperformance;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.GolfRoundStream;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CurrentGolferStats implements GolferPerformance {

	private final String golfer;
	private final Collection<GolfRound> golfRounds;
	private final HandicapIndex handicapIndex;
	private final HandicapIndex trendingHandicap;
	private final HandicapIndex antiHandicap;

	public CurrentGolferStats(Collection<GolfRound> roundsUnsorted) {
		this.golfRounds = GolfRound.stream(roundsUnsorted)
		                           .sortOldestToNewest()
		                           .collect(Collectors.toList());
		this.golfer = rounds().golferNames();
		this.handicapIndex = HandicapIndex.newIndex(roundsUnsorted);
		this.trendingHandicap = HandicapIndex.lastFiveRoundsTrendingHandicap(roundsUnsorted);
		this.antiHandicap = HandicapIndex.antiHandicapOf(roundsUnsorted);
	}

	public String toString() {
		return toStringDefault();
	}

	public LocalDate asOf() {
		return rounds().getMostRecentRound().getDate();
	}

	public BigDecimal getFairwaysInRegulation() {
		return rounds().getFairwaysInRegulation();
	}

	public BigDecimal getGreensInRegulation() {
		return rounds().getGreensInRegulation();
	}

	public BigDecimal getPuttsPerHole() {
		return rounds().getPuttsPerHole();
	}

	public Long getMinutesPer18Holes() {
		return rounds().compileTo18HoleRounds().getMinutesPerRound();
	}

	private GolfRoundStream rounds() {
		return GolfRound.stream(this.golfRounds);
	}
}
