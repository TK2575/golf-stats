package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.ObjectStream;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Getter
@AllArgsConstructor
public class HoleStream implements ObjectStream<Hole> {

	private final Stream<Hole> stream;

	private final boolean empty;

	public HoleStream(Collection<Hole> holes) {
		this.stream = holes.stream();
		this.empty = holes.isEmpty();
	}

	public static HoleStream empty() {
		return new HoleStream(Stream.empty(), true);
	}

	public HoleStream validate() {
		List<Hole> holes = this.asList();
		if (holes == null || holes.isEmpty()) {
			throw new IllegalArgumentException("holes collection cannot be empty");
		}

		long count = holes.size();
		if (count != 9 && count != 18) {
			throw new IllegalArgumentException("must record 9 or 18 holes");
		}
		if (holes.stream().collect(groupingBy(Hole::getNumber, counting())).values().stream().anyMatch(l -> l > 1)) {
			throw new IllegalArgumentException("found multiple holes with the same number");
		}

		return new HoleStream(holes);
	}

	public HoleStream sortFirstToLast() {
		return new HoleStream(this.stream.sorted(Comparator.comparing(Hole::getNumber)), this.empty);
	}

	public Hole byNumber(Integer number) {
		return this.stream.filter(hole -> hole.getNumber().equals(number)).findAny().orElseThrow(NoSuchElementException::new);
	}

	public HoleStream applyNetDoubleBogey(Tee tee, Golfer golfer) {
		Integer handicapStrokes = tee.handicapOf(golfer).getHandicapStrokesForGolfer(golfer);
		return new HoleStream(this.stream.map(h -> h.applyNetDoubleBogey(handicapStrokes)), this.empty);
	}

	public HoleStream applyNetDoubleBogey(Integer handicapStrokes) {
		return new HoleStream(this.stream.map(h -> h.applyNetDoubleBogey(handicapStrokes)), this.empty);
	}

	public Integer getPar() {
		return sumInteger(Hole::getPar);
	}

	public Integer totalStrokes() {
		return sumInteger(Hole::getStrokes);
	}

	public Integer totalStrokesAdjusted() {
		return sumInteger(Hole::getStrokesAdjusted);
	}

	public Integer totalNetStrokes() {
		return sumInteger(Hole::getNetStrokes);
	}

	public Integer totalFairwaysInRegulation() {
		return sumBoolean(Hole::isFairwayInRegulation);
	}

	public Integer totalFairways() {
		return sumBoolean(Hole::isFairwayPresent);
	}

	public Integer totalGreensInRegulation() {
		return sumBoolean(Hole::isGreenInRegulation);
	}

	public Integer totalPutts() {
		return sumInteger(Hole::getPutts);
	}

	public Integer totalScore(ToIntFunction<Hole> rules) {
		return sumInteger(rules::applyAsInt);
	}

	public boolean isNineHoleRound() {
		return this.stream.count() == 9;
	}

	public static List<Hole> compositeOf(@NonNull GolfRound round1, @NonNull GolfRound round2) {
		List<Hole> list = new ArrayList<>();
		list.addAll(round1.getHoles().asList());
		list.addAll(round2.getHoles().invertHoleNumbers().asList());
		return new HoleStream(list).validate().asList();
	}

	private HoleStream invertHoleNumbers() {
		return new HoleStream(this.stream.map(Hole::invertNumber).collect(Collectors.toList()));
	}

	public ShotStream allShots() {
		return Shot.stream(this.stream.flatMap(Hole::getShots).collect(Collectors.toList()));
	}

	public Map<Integer, BigDecimal> strokesGainedByHole() {
		return empty
		       ? Collections.emptyMap()
		       : this.stream.collect(Collectors.toUnmodifiableMap(Hole::getNumber, Hole::getStrokesGained));
	}

	public Map<String, BigDecimal> strokesGainedByShotType() {
		return empty ? Collections.emptyMap() : allShots().strokesGainedByShotType();
	}

	public BigDecimal totalStrokesGained() {
		return empty ? BigDecimal.ZERO : allShots().totalStrokesGained();
	}

	public BigDecimal totalStrokesGainedBaseline() {
		return sumBigDecimal(Hole::getStrokesGainedBaseline);
	}

	public Long totalYards() {
		return sumLong(Hole::getYards);
	}
}
