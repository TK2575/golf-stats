package dev.tk2575.golfstats.core;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Getter
public class PerformanceSummary {

	private final String golfer;
	private final Collection<GolfRound> golfRounds;
	private final Collection<GolfRound> eighteenHoleRounds;
	private final HandicapIndex handicapIndex;
	private final HandicapIndex trendingHandicap;
	private final HandicapIndex antiHandicap;
	private final HandicapIndex overallHandicap;
	private final HandicapIndex medianHandicap;
	private final GolfRound lowRoundByDiff;
	private final GolfRound highRoundByDiff;
	private final GolfRound lowRoundByScore;
	private final GolfRound highRoundByScore;

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		this.golfRounds = GolfRound.stream(roundsUnsorted).sortOldestToNewest().asList();
		this.eighteenHoleRounds = rounds().compileTo18HoleRounds().sortOldestToNewest().asList();
		this.golfer = rounds().golferNames();
		this.handicapIndex = HandicapIndex.newIndex(this.eighteenHoleRounds);
		this.trendingHandicap = HandicapIndex.lastFiveRoundsTrendingHandicap(this.eighteenHoleRounds);
		this.antiHandicap = HandicapIndex.antiHandicapOf(this.eighteenHoleRounds);
		this.overallHandicap = HandicapIndex.overallHandicap(this.eighteenHoleRounds);
		this.medianHandicap = HandicapIndex.medianHandicap(this.eighteenHoleRounds);
		this.lowRoundByDiff = eighteenHoleRounds().getBestDifferential();
		this.highRoundByDiff = eighteenHoleRounds().getWorstDifferential();
		this.lowRoundByScore = eighteenHoleRounds().getLowestScoreRound();
		this.highRoundByScore = eighteenHoleRounds().getHighestScoreRound();
	}

	public String toString() {
		List<String> fields = new ArrayList<>();
		fields.add(String.format("golfer=%s", getGolfer()));
		fields.add(String.format("handicapIndex=%s", getHandicapIndex().getValue().setScale(1, HALF_UP).toPlainString()));
		fields.add(String.format("trendingHandicap=%s", getTrendingHandicap().getValue().setScale(1, HALF_UP).toPlainString()));
		fields.add(String.format("antiHandicap=%s", getAntiHandicap().getValue().setScale(1, HALF_UP).toPlainString()));
		fields.add(String.format("medianHandicap=%s", getMedianHandicap().getValue().setScale(1, HALF_UP).toPlainString()));
		fields.add(String.format("overallHandicap=%s", getOverallHandicap().getValue().setScale(1, HALF_UP).toPlainString()));
		fields.add(String.format("bestRoundByDifferential=%s", getLowRoundByDiff().toString()));
		fields.add(String.format("bestRoundByScore=%s", getLowRoundByScore().toString()));
		fields.add(String.format("worstRoundByDifferential=%s", getHighRoundByDiff().toString()));
		fields.add(String.format("worstRoundByScore=%s", getHighRoundByScore().toString()));
		fields.add(String.format("fairwaysInRegulation=%s", getFairwaysInRegulation()));
		fields.add(String.format("greensInRegulation=%s", getGreensInRegulation()));
		fields.add(String.format("puttsPerHole=%s", getPuttsPerHole()));
		fields.add(String.format("minutesPer18Holes=%s", getMinutesPer18Holes()));
		fields.add(String.format("asOf=%s", asOf()));

		return this.getClass().getSimpleName() + "(" + String.join(", ", fields) + ")";
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

	private GolfRoundStream eighteenHoleRounds() { return GolfRound.stream(this.eighteenHoleRounds); }
}
