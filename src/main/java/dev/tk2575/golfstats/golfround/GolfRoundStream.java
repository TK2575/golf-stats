package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.ObjectStream;
import dev.tk2575.golfstats.Utils;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class GolfRoundStream implements ObjectStream<GolfRound> {

	private final Stream<GolfRound> stream;

	public String golferNames() {
		return this.stream.map(GolfRound::getGolfer)
		                  .distinct()
		                  .collect(Collectors.joining(", "));
	}

	public GolfRoundStream subsetMostRecentForHandicap() {
		return new GolfRoundStream(sortNewestToOldest().limit(20));
	}

	public BigDecimal meanDifferential() {
		List<GolfRound> rounds = collectToList();
		return rounds.stream()
		             .map(GolfRound::getScoreDifferential)
		             .reduce(BigDecimal.ZERO, BigDecimal::add)
		             .divide(BigDecimal.valueOf(rounds.size()), RoundingMode.HALF_UP)
		             .setScale(2, RoundingMode.HALF_UP);
	}

	public GolfRoundStream compileTo18HoleRounds() {
		List<GolfRound> roundsList = this.sortOldestToNewest()
		                                 .collect(Collectors.toList());

		List<GolfRound> compiledRounds = new ArrayList<>();
		NineHoleRound pendingNineHoleRound = null;
		GolfRound thisRound;

		//TODO refactor without for loop
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
				compiledRounds.add(thisRound);
			}
		}
		return GolfRound.stream(compiledRounds);
	}

	public GolfRoundStream sortOldestToNewest() {
		return new GolfRoundStream(this.stream.sorted(Comparator.comparing(GolfRound::getDate)));
	}

	public GolfRoundStream sortNewestToOldest() {
		return new GolfRoundStream(this.stream.sorted(Comparator.comparing(GolfRound::getDate)
		                                                        .reversed()));
	}

	public GolfRound getMostRecentRound() {
		return this.sortNewestToOldest().collect(Collectors.toList()).get(0);
	}

	public String toTSV() {
		final List<String[]> datalines = this.stream.map(GolfRound::roundToArray)
		                                            .collect(Collectors.toList());

		String rowsOfRounds = datalines.stream()
		                               .map(Utils::convertToTSV)
		                               .collect(Collectors.joining("\n"));

		return String.format("%n%s%n%s", Utils.convertToTSV(GolfRound.roundHeaders()), rowsOfRounds);
	}

	//TODO can methods that re-collect the stream to list be refactored to
	// pure stream operations? perhaps some functional interface to avoid
	// code duplication?
	public GolfRoundStream getBestDifferentials() {
		final List<GolfRound> rounds = this.collectToList();
		long subsetSize = (long) (rounds.size() * .4);
		return new GolfRoundStream(rounds.stream()
		                                 .sorted(Comparator.comparing(GolfRound::getScoreDifferential))
		                                 .limit(subsetSize));
	}

	public BigDecimal getFairwaysInRegulation() {
		final List<GolfRound> rounds = this.collectToList();
		long fairwaysInRegulation = rounds.stream()
		                                  .mapToLong(GolfRound::getFairwaysInRegulation)
		                                  .sum();
		long fairways = rounds.stream().mapToLong(GolfRound::getFairways).sum();
		return BigDecimal.valueOf(fairwaysInRegulation).divide(BigDecimal.valueOf(fairways), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getGreensInRegulation() {
		final List<GolfRound> rounds = this.collectToList();
		final long greensInRegulation = rounds.stream()
		                                      .mapToLong(GolfRound::getGreensInRegulation)
		                                      .sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(greensInRegulation).divide(BigDecimal.valueOf(holes), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getPuttsPerHole() {
		final List<GolfRound> rounds = this.collectToList();
		final long putts = rounds.stream().mapToLong(GolfRound::getPutts).sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(putts).divide(BigDecimal.valueOf(holes),
				2, RoundingMode.HALF_UP);
	}

	private long getHoles() {
		return this.stream.mapToLong(GolfRound::getHoles).sum();
	}

	public Long getMinutesPerRound() {
		final List<GolfRound> rounds = this.collect(Collectors.toList());
		final Duration duration = rounds.stream()
		                                .map(GolfRound::getDuration)
		                                .filter(d -> !d.isZero())
		                                .reduce(Duration::plus)
		                                .orElse(Duration.ZERO);

		return duration.equals(Duration.ZERO)
		       ? 0L
		       : duration.toMinutes() / rounds.size();
	}

	private List<GolfRound> collectToList() {
		return this.stream.collect(Collectors.toList());
	}
}
