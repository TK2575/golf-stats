package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.ObjectStream;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class HoleStream implements ObjectStream<Hole> {

	private final Stream<Hole> stream;

	public HoleStream validate() {
		List<Hole> holes = this.asList();
		long count = holes.size();
		if (count != 9 && count != 18) {
			throw new IllegalArgumentException("must record 9 or 18 holes");
		}
		return new HoleStream(holes.stream());
	}

	public HoleStream sortFirstToLast() {
		return new HoleStream(this.stream.sorted(Comparator.comparing(Hole::getNumber)));
	}

	public Integer totalStrokes() {
		return sumInteger(Hole::getScore);
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

	public boolean isNineHoleRound() {
		return this.stream.count() == 9;
	}

	public static List<Hole> compositeOf(HoleByHoleRound round1, HoleByHoleRound round2) {
		List<Hole> list = new ArrayList<>();
		if (round1.getDate().isAfter(round2.getDate())) {
			list.addAll(round2.getHoles().asList());
			list.addAll(round1.getHoles().asList());
		}
		else {
			list.addAll(round1.getHoles().asList());
			list.addAll(round2.getHoles().asList());
		}

		return new HoleStream(list.stream()).validate().asList();

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
