package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.ObjectStream;
import dev.tk2575.Utils;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GolfRoundStream implements ObjectStream<GolfRound> {

	private final Stream<GolfRound> stream;
	private final boolean empty;

	public GolfRoundStream(@NonNull Collection<GolfRound> rounds) {
		this.stream = rounds.stream();
		this.empty = rounds.isEmpty();
	}

	public static GolfRoundStream empty() {
		return new GolfRoundStream(Stream.empty(), true);
	}

	public String golferNames() {
		return this.stream.map(r -> r.getGolfer().getName()).distinct().collect(Collectors.joining(", "));
	}

	public BigDecimal meanDifferential() {
		List<GolfRound> rounds = toList();
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

		for (GolfRound round : this.sortOldestToNewest().toList()) {
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
		return new GolfRoundStream(this.stream.sorted(Comparator.comparing(GolfRound::getDate)), this.empty);
	}

	public GolfRoundStream sortNewestToOldest() {
		return new GolfRoundStream(this.stream.sorted(Comparator.comparing(GolfRound::getDate).reversed()), this.empty);
	}

	public Optional<GolfRound> newestRound() {
		return this.empty
				? Optional.empty()
				: Optional.of(this.sortNewestToOldest().toList().get(0));
	}

	public Optional<GolfRound> oldestRound() {
		return this.empty
				? Optional.empty()
				: Optional.of(this.sortOldestToNewest().toList().get(0));
	}

	//TODO can methods that re-collect the stream to list be refactored to
	// pure stream operations? perhaps some functional interface to avoid
	// code duplication?
	public GolfRoundStream lowestDifferentials() {
		final List<GolfRound> rounds = this.toList();
		long subsetSize = (long) (rounds.size() * .4);
		return new GolfRoundStream(rounds.stream()
		                                 .sorted(Comparator.comparing(GolfRound::getScoreDifferential))
		                                 .limit(subsetSize), this.empty);
	}

	public GolfRoundStream highestDifferentials() {
		final List<GolfRound> rounds = this.toList();
		long subsetSize = (long) (rounds.size() * .4);
		return new GolfRoundStream(rounds.stream()
		                                 .sorted(Comparator.comparing(GolfRound::getScoreDifferential).reversed())
		                                 .limit(subsetSize), this.empty);
	}

	public BigDecimal fairwaysInRegulation() {
		if (this.empty) { return BigDecimal.ZERO; }
		final List<GolfRound> rounds = this.toList();
		long fairwaysInRegulation = rounds.stream().mapToLong(GolfRound::getFairwaysInRegulation).sum();
		long fairways = rounds.stream().mapToLong(GolfRound::getFairways).sum();
		return BigDecimal.valueOf(fairwaysInRegulation).divide(BigDecimal.valueOf(fairways), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal greensInRegulation() {
		if (this.empty) { return BigDecimal.ZERO; }
		final List<GolfRound> rounds = this.toList();
		final long greensInRegulation = rounds.stream().mapToLong(GolfRound::getGreensInRegulation).sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(greensInRegulation).divide(BigDecimal.valueOf(holes), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal puttsPerHole() {
		if (this.empty) { return BigDecimal.ZERO; }
		final List<GolfRound> rounds = this.toList();
		final long putts = rounds.stream().mapToLong(GolfRound::getPutts).sum();
		final long holes = GolfRound.stream(rounds).getHoles();
		return BigDecimal.valueOf(putts).divide(BigDecimal.valueOf(holes), 2, RoundingMode.HALF_UP);
	}

	public Long minutesPerRound() {
		if (this.empty) { return 0L; }
		final List<GolfRound> rounds = this.toList();
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
		return new GolfRoundStream(this.stream.limit(maxSize), this.empty);
	}

	public BigDecimal medianDifferential() {
		return this.empty
				? BigDecimal.ZERO
				: Utils.median(this.stream.map(GolfRound::getScoreDifferential).collect(Collectors.toList()));
	}

	public Optional<GolfRound> lowestDifferential() {
		return this.stream.reduce((a, b) -> a.getScoreDifferential().compareTo(b.getScoreDifferential()) < 0 ? a : b);
	}

	public Optional<GolfRound> largestDifferential() {
		return this.stream.reduce((a, b) -> a.getScoreDifferential().compareTo(b.getScoreDifferential()) > 0 ? a : b);
	}

	public Optional<GolfRound> lowestScoreRound() {
		return this.stream.reduce((a, b) -> {
			if (!a.getScore().equals(b.getScore())) {
				return a.getScore() < b.getScore() ? a : b;
			}
			else {
				return a.getScoreDifferential().compareTo(b.getScoreDifferential()) < 0 ? a : b;
			}
		});
	}

	public Optional<GolfRound> highestScoreRound() {
		return this.stream.reduce((a, b) -> {
			if (!a.getScore().equals(b.getScore())) {
				return a.getScore() > b.getScore() ? a : b;
			}
			else {
				return a.getScoreDifferential().compareTo(b.getScoreDifferential()) > 0 ? a : b;
			}
		});
	}
}
