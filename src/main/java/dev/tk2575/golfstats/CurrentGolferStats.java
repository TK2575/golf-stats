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
import java.util.stream.Stream;

@Getter
@ToString
public class CurrentGolferStats {

	private final String golfer;

	private final BigDecimal handicapIndex;
	private final BigDecimal handicapIndexTrend;

	@ToString.Exclude private final List<GolfRound> allGolfRounds;
	@ToString.Exclude private final List<GolfRound> currentGolfRounds;
	private final List<GolfRound> indexCalculatingGolfRounds;
	private final List<GolfRound> mostRecentGolfRounds;


	public CurrentGolferStats(String golfer, List<GolfRound> roundsUnsorted) {
		this.golfer = golfer;

		this.allGolfRounds = new ArrayList<>(roundsUnsorted);
		this.allGolfRounds.sort(Comparator.comparing(GolfRound::getDate).reversed());
		this.currentGolfRounds = generateCurrentGolfRounds(this.allGolfRounds);
		this.currentGolfRounds.sort(Comparator.comparing(GolfRound::getDate).reversed());
		this.mostRecentGolfRounds = this.currentGolfRounds.size() > 5
				? new ArrayList<>(this.currentGolfRounds.subList(0, 5))
				: new ArrayList<>(this.currentGolfRounds);

		this.currentGolfRounds.sort(Comparator.comparing(GolfRound::getScoreDifferential));
		this.indexCalculatingGolfRounds = subsetForIndexCalc(currentGolfRounds);
		this.handicapIndex = computeIndex(this.indexCalculatingGolfRounds);
		this.handicapIndexTrend = computeIndex(this.mostRecentGolfRounds);
	}

	private List<GolfRound> generateCurrentGolfRounds(List<GolfRound> roundsList) {
		List<GolfRound> differentials = new ArrayList<>();
		NineHoleRound pendingNineHoleRound = null;
		GolfRound thisRound;

		for (GolfRound round : roundsList) {
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
		return differentials;
	}

	private List<GolfRound> subsetForIndexCalc(List<GolfRound> roundsList) {
		int subsetSize = (int) Math.floor(roundsList.size() * .4);
		return roundsList.subList(0, subsetSize);
	}

	private BigDecimal computeIndex(List<GolfRound> roundsList) {
		return roundsList.stream()
				.map(GolfRound::getScoreDifferential)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.divide(BigDecimal.valueOf(roundsList.size()), RoundingMode.HALF_UP)
				.setScale(2, RoundingMode.HALF_UP);
	}

	public String currentRoundsToCSV() {
		final List<String[]> datalines = this.currentGolfRounds.stream().map(GolfRound::toCSV).collect(Collectors.toList());
		return datalines.stream().map(this::convertToCSV).collect(Collectors.joining("\n"));
	}

	private String convertToCSV(String[] data) {
		return Stream.of(data)
				.map(this::escapeSpecialCharacters)
				.collect(Collectors.joining("\t"));
	}

	private String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

}
