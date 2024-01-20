package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.ObjectStream;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
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
		List<Hole> holes = this.toList();
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

		if (new HoleStream(holes).duplicateIndexes()) {
			throw new IllegalArgumentException("found multiple holes with the same index");
		}

		return new HoleStream(holes);
	}

	public boolean duplicateIndexes() {
		return duplicateFieldValue(Hole::getIndex);
	}

	public boolean duplicateNumbers() {
		return duplicateFieldValue(Hole::getNumber);
	}

	private boolean duplicateFieldValue(Function<Hole, Integer> fieldReference) {
		return this.stream.collect(groupingBy(fieldReference, counting())).values().stream().anyMatch(l -> l > 1);
	}

	public HoleStream sortByNumber() {
		return new HoleStream(this.stream.sorted(comparing(Hole::getNumber)), this.empty);
	}

	public HoleStream sortByIndex() {
		return new HoleStream(this.stream.sorted(comparing(Hole::getIndex)), this.empty);
	}

	public Hole byNumber(Integer number) {
		return this.stream.filter(hole -> hole.getNumber().equals(number)).findAny().orElseThrow(NoSuchElementException::new);
	}
	
	public HoleStream frontNine() {
		return new HoleStream(this.stream.filter(hole -> hole.getNumber() <= 9).toList()).sortByNumber();
	}
	
	public HoleStream backNine() {
		return new HoleStream(this.stream.filter(hole -> hole.getNumber() > 9).toList()).sortByNumber();
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
		list.addAll(round1.getHoles().reNumber(1).reIndex(1).toList());
		list.addAll(round2.getHoles().reNumber(10).reIndex(2).toList());
		return new HoleStream(list).validate().toList();
	}

	public ShotStream allShots() {
		return Shot.stream(this.stream.flatMap(Hole::getShots).toList());
	}
	
	public Long countBirdiesOrBetter() {
		return this.stream.filter(Hole::isBirdieOrBetter).count();
	}
	
	public Long countDoubleBogeysOrWorse() {
		return this.stream.filter(Hole::isDoubleBogeyOrWorse).count();
	}
	
	public Long getBirdieVsDoubleRatio() {
		var list = this.toList();
		return new HoleStream(list).countBirdiesOrBetter() - new HoleStream(list).countDoubleBogeysOrWorse();
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

	private HoleStream reIndex(int start) {
		List<Hole> results = new ArrayList<>();
		int i = start;
		for (Hole hole : this.sortByIndex().toList()) {
			results.add(hole.setIndex(i));
			i = i + 2;
		}
		return new HoleStream(results).sortByNumber();
	}

	private HoleStream reNumber(int start) {
		List<Hole> results = new ArrayList<>();
		int i = start;
		for (Hole hole : this.stream.toList()) {
			results.add(hole.setNumber(i));
			i++;
		}
		return new HoleStream(results);
	}

	public HoleStream shuffleNineHoleIndexesOdd() {
		return shuffleNineHoleIndexes(1);
	}

	public HoleStream shuffleNineHoleIndexesEven() {
		return shuffleNineHoleIndexes(2);
	}

	private HoleStream shuffleNineHoleIndexes(int start) {
		List<Hole> holes = toList();
		if (new HoleStream(holes).isNineHoleRound()) {
			return new HoleStream(holes).reIndex(start).sortByNumber();
		}
		return new HoleStream(holes);
	}

	public HoleStream shuffleEighteenHoleIndexes() {
		List<Hole> holes = toList();
		if (!new HoleStream(holes).isNineHoleRound()) {
			Map<Boolean, List<Hole>> groups = holes.stream().collect(Collectors.partitioningBy(hole -> hole.getNumber() > 9));
			List<List<Hole>> subsets = new ArrayList<>(groups.values());

			List<Hole> result = new ArrayList<>(new HoleStream(subsets.get(0)).shuffleNineHoleIndexesOdd().toList());
			result.addAll(new HoleStream(subsets.get(1)).shuffleNineHoleIndexesEven().toList());
			return new HoleStream(result).sortByNumber();
		}
		return new HoleStream(holes);
	}
}
