package dev.tk2575.golfstats;

import dev.tk2575.golfstats.golfround.CompositeGolfRound;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.NineHoleRound;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class CurrentGolferStats {

	private String golfer;
	private BigDecimal handicapIndex;
	private List<BigDecimal> scoreDifferentials;
	private List<BigDecimal> scoreDifferentialsSubset;
	@ToString.Exclude private List<GolfRound> rounds;

	public CurrentGolferStats(String golfer, List<GolfRound> roundsUnsorted) {
		this.rounds = new ArrayList<>(roundsUnsorted);
		this.rounds.sort(Comparator.comparing(GolfRound::getDate).reversed());
		this.golfer = golfer;
		calculateDiffsAndIndex();
	}

	private void calculateDiffsAndIndex() {
		List<GolfRound> differentials = new ArrayList<>();
		NineHoleRound pendingNineHoleRound = null;
		GolfRound thisRound;

		for (GolfRound round : this.rounds) {
			thisRound = null;
			if (round instanceof NineHoleRound) {
				if (pendingNineHoleRound == null) {
					pendingNineHoleRound = (NineHoleRound) round;
				}
				else {
					thisRound = new CompositeGolfRound(pendingNineHoleRound, (NineHoleRound) round);
					pendingNineHoleRound = null;
				}
			}
			else {
				thisRound = round;
			}

			if (thisRound != null) {
				differentials.add(thisRound);
			}

			if (differentials.size() >= 20) break;
		}

		this.handicapIndex = subsetAndAverageDiffs(differentials);
	}

	private BigDecimal subsetAndAverageDiffs(final List<GolfRound> latestRounds) {
		this.scoreDifferentials = latestRounds.stream().map(GolfRound::getScoreDifferential).collect(Collectors.toList());
		scoreDifferentials.sort(Comparator.naturalOrder());

		int subsetSize = (int) Math.floor(scoreDifferentials.size() * .4);
		scoreDifferentialsSubset = scoreDifferentials.subList(0, subsetSize);

		if (scoreDifferentialsSubset.size() == 1) {
			return scoreDifferentialsSubset.get(0);
		}
		else {
			return scoreDifferentialsSubset.stream()
					.reduce(BigDecimal.ZERO, BigDecimal::add)
					.divide(BigDecimal.valueOf(subsetSize), RoundingMode.HALF_UP)
					.setScale(1, RoundingMode.HALF_UP);
		}
	}

}
