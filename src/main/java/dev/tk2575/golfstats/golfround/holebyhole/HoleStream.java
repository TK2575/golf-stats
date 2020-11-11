package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.ObjectStream;
import dev.tk2575.golfstats.course.tee.Tee;
import dev.tk2575.golfstats.golfround.games.Game;
import dev.tk2575.golfstats.golfround.GolfRound;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		return new HoleStream(holes);
	}

	public HoleStream sortFirstToLast() {
		return new HoleStream(this.stream.sorted(Comparator.comparing(Hole::getNumber)), this.empty);
	}

	public HoleStream applyNetDoubleBogey(Tee tee, Golfer golfer) {
		Integer handicapStrokes = tee.handicapOf(golfer).getHandicapStrokesForGolfer(golfer);
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

	public static List<Hole> compositeOf(GolfRound round1, GolfRound round2) {
		List<Hole> list = new ArrayList<>();
		if (round1.getDate().isAfter(round2.getDate())) {
			list.addAll(round2.getHoles().asList());
			list.addAll(round1.getHoles().asList());
		}
		else {
			list.addAll(round1.getHoles().asList());
			list.addAll(round2.getHoles().asList());
		}

		return new HoleStream(list).validate().asList();

	}

	public List<Hole> asList() {
		return this.stream.collect(Collectors.toList());
	}

	private Integer sumInteger(Function<Hole, Integer> field) {
		return this.stream.map(field).reduce(0, Integer::sum);
	}

	private Integer sumBoolean(Predicate<Hole> field) {
		return Math.toIntExact(this.stream.filter(field).count());
	}
}
