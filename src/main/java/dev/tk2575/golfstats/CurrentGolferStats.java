package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@ToString
public class CurrentGolferStats {

	private String golfer;
	private BigDecimal handicapIndex;
	private List<BigDecimal> scoreDifferentials;
	private List<BigDecimal> scoreDifferentialsSubset;
	@ToString.Exclude private List<GolfRound> rounds;

	public CurrentGolferStats(String golfer, List<GolfRound> roundsUnsorted) {
		List<GolfRound> roundsSorted = new ArrayList<>(roundsUnsorted);
		roundsSorted.sort(Comparator.comparing(GolfRound::getDate).reversed());
		this.rounds = roundsSorted;

		this.golfer = golfer;
		calculateDiffsAndIndex();
	}

	private void calculateDiffsAndIndex() {
		List<BigDecimal> differentials = new ArrayList<>();
		BigDecimal pendingNineHoleDifferential = null;
		BigDecimal thisDiff;

		for (GolfRound round : rounds) {
			thisDiff = null;
			if (Boolean.TRUE.equals(round.getNineHoleRound())) {
				if (pendingNineHoleDifferential == null) {
					pendingNineHoleDifferential = round.getScoreDifferential();
				}
				else {
					thisDiff = pendingNineHoleDifferential.add(round.getScoreDifferential());
					pendingNineHoleDifferential = null;
				}
			}
			else {
				thisDiff = round.getScoreDifferential();
			}

			if (thisDiff != null) {
				differentials.add(thisDiff);
			}

			if (differentials.size() >= 20) break;
		}

		this.handicapIndex = subsetAndAverageDiffs(differentials);
	}

	private BigDecimal subsetAndAverageDiffs(final List<BigDecimal> differentials) {
		scoreDifferentials = new ArrayList<>(differentials);
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
