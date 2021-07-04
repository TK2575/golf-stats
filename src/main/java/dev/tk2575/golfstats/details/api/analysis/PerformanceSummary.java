package dev.tk2575.golfstats.details.api.analysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@ToString
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
	private final Map<LocalDate, BigDecimal> handicapRevisionHistory;

	@ToString.Exclude
	private final List<RoundSummary> rounds = new ArrayList<>();

	public PerformanceSummary(Collection<GolfRound> roundsUnsorted) {
		this.golfRounds = GolfRound.stream(roundsUnsorted).compileTo18HoleRounds().asList();

		this.golfer = rounds().golferNames();
		this.fairwaysInRegulation = rounds().fairwaysInRegulation();
		this.greensInRegulation = rounds().greensInRegulation();
		this.puttsPerHole = rounds().puttsPerHole();
		this.minutesPer18Holes = rounds().minutesPerRound();
		this.roundCount = rounds().count();

		LocalDate today = LocalDate.now();
		Optional<GolfRound> round = rounds().oldestRound();
		this.from = round.isEmpty() ? today : round.get().getDate();
		round = rounds().newestRound();
		this.asOf = round.isEmpty() ? today : round.get().getDate();

		HandicapIndex index = HandicapIndex.newIndex(this.golfRounds);
		this.handicapIndex = index.getValue();
		this.handicapRevisionHistory = index.getRevisionHistory();
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
