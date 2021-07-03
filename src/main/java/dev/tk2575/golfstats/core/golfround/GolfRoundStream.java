package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.ObjectStream;
import dev.tk2575.Utils;
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
		return this.stream.map(r -> r.getGolfer().getName()).distinct().collect(Collectors.joining(", "));
	}

	public BigDecimal meanDifferential() {
		List<GolfRound> rounds = asList();
		if (rounds.isEmpty()) { return BigDecimal.ZERO; }

		return rounds.stream()
		             .map(GolfRound::getScoreDifferential)
		             .reduce(BigDecimal.ZERO, BigDecimal::add)
		             .divide(BigDecimal.valueOf(rounds.size()), 1, RoundingMode.HALF_UP);
	}

	public GolfRoundStream compileTo18HoleRounds() {
		List<GolfRound> compiledRounds = new ArrayList<>();
		GolfRound pendingNineHoleRound = null;
		GolfRound thisRound;

		for (GolfRound round : this.sortOldestToNewest().asList()) {
			thisRound = null;
			if (round.isNineHoleRound()) {
				if (pendingNineHoleRound == null) {
					pendingNineHoleRound = round;
				}
				else {
					thisRound = GolfRound.compositeOf(pendingNineHoleRound, round);
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
		return new GolfRoundStream(this.stream.sorted(Comparator.comparing(GolfRound::getDate).reversed()));
	}

	public GolfRound getMostRecentRound() {
		return this.sortNewestToOldest().asList().get(0);
	}

	public GolfRound getOldestRound() {
		return this.sortOldestToNewest().asList().get(0);
	}

	//TODO can methods that re-collect the stream to list be refactored to
	// pure stream operations? perhaps some functional interface to avoid
	// code duplication?
	public GolfRoundStream getBestDifferentials() {
		final List<GolfRound> rounds = this.asList();
		long subsetSize = (long) (rounds.size() * .4);
		return new GolfRoundStream(rounds.stream()
		                                 .sorted(Comparator.comparing(GolfRound::getScoreDifferential))
		                                 .limit(subsetSize));
	}

	public GolfRoundStream getWorstDifferentials() {
		final List<GolfRound> rounds = this.asList();
		long subsetSize = (long) (rounds.size() * .4);
		return new GolfRoundStream(rounds.stream()
		                                 .sorted(Comparator.comparing(GolfRound::getScoreDifferential).reversed())
		                                 .limit(subsetSize));
	}

	public BigDecimal getFairwaysInRegulation() {
		final List<GolfRound> rounds = this.asList();
		long fairwaysInRegulation = rounds.stream().mapToLong(GolfRound::getFairwaysInRegulation).sum();
		long fairways = rounds.stream().mapToLong(GolfRound::getFairways).sum();
		return BigDecimal.valueOf(fairwaysInRegulation).divide(BigDecimal.valueOf(fairways), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getGreensInRegulation() {
		final List<GolfRound> rounds = this.asList();
		final long greensInRegulation = rounds.stream().mapToLong(GolfRound::getGreensInRegulation).sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(greensInRegulation).divide(BigDecimal.valueOf(holes), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getPuttsPerHole() {
		final List<GolfRound> rounds = this.asList();
		final long putts = rounds.stream().mapToLong(GolfRound::getPutts).sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(putts).divide(BigDecimal.valueOf(holes), 2, RoundingMode.HALF_UP);
	}

	public Long getMinutesPerRound() {
		final List<GolfRound> rounds = this.asList();
		final Duration duration = rounds.stream()
		                                .map(GolfRound::getDuration)
		                                .filter(d -> !d.isZero())
		                                .reduce(Duration::plus)
		                                .orElse(Duration.ZERO);

		return duration.equals(Duration.ZERO) ? 0L : duration.toMinutes() / rounds.size();
	}

	private long getHoles() {
		return this.stream.mapToLong(GolfRound::getHoleCount).sum();
	}

	@Override
	public GolfRoundStream limit(long maxSize) {
		return new GolfRoundStream(this.stream.limit(maxSize));
	}

	public BigDecimal getMedianDifferential() {
		return Utils.median(this.stream.map(GolfRound::getScoreDifferential).collect(Collectors.toList()));
	}

	public GolfRound getBestDifferential() {
		return this.stream.reduce((a, b) -> a.getScoreDifferential().compareTo(b.getScoreDifferential()) < 0 ? a : b)
		                  .orElseThrow();
	}

	public GolfRound getWorstDifferential() {
		return this.stream.reduce((a, b) -> a.getScoreDifferential().compareTo(b.getScoreDifferential()) > 0 ? a : b)
		                  .orElseThrow();
	}

	public GolfRound getLowestScoreRound() {
		return this.stream.reduce((a, b) -> {
			if (!a.getScoreToPar().equals(b.getScoreToPar())) {
				return a.getScoreToPar() < b.getScoreToPar() ? a : b;
			}
			else {
				return a.getScoreDifferential().compareTo(b.getScoreDifferential()) < 0 ? a : b;
			}
		}).orElseThrow();
	}

	public GolfRound getHighestScoreRound() {
		return this.stream.reduce((a, b) -> {
			if (!a.getScoreToPar().equals(b.getScoreToPar())) {
				return a.getScoreToPar() > b.getScoreToPar() ? a : b;
			}
			else {
				return a.getScoreDifferential().compareTo(b.getScoreDifferential()) > 0 ? a : b;
			}
		}).orElseThrow();
	}
}
