package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotStream;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.tk2575.Utils.percentile;
import static dev.tk2575.Utils.roundToOneDecimalPlace;
import static java.math.RoundingMode.HALF_UP;

public interface GolfRound {

	GolfRound applyNetDoubleBogey(BigDecimal incomingIndex);

	LocalDate getDate();

	Duration getDuration();

	Golfer getGolfer();

	Course getCourse();

	Tee getTee();

	String getTransport();

	BigDecimal getIncomingHandicapIndex();

	BigDecimal getScoreDifferential();

	Integer getStrokes();

	Integer getStrokesAdjusted();

	Integer getFairwaysInRegulation();

	Integer getFairways();

	Integer getGreensInRegulation();

	Integer getPutts();

	Integer getHoleCount();

	boolean isNineHoleRound();

	default HoleStream getHoles() {
		return HoleStream.empty();
	}
	
	default ShotStream getShots() {
		return getHoles().allShots();
	}

	default BigDecimal computeScoreDifferential() {
		BigDecimal firstTerm = BigDecimal.valueOf(113)
		                                 .divide(getSlope(), 2, HALF_UP);
		BigDecimal secondTerm = BigDecimal.valueOf(getStrokesAdjusted())
		                                  .subtract(getRating())
		                                  .setScale(2, HALF_UP);
		return roundToOneDecimalPlace(firstTerm.multiply(secondTerm));
	}

	Integer getNetStrokes();

	default Integer getNetScore() {
		return getNetStrokes() - getPar();
	}

	default BigDecimal getPuttsPerHole() {
		return Utils.divide(getPutts(), getHoleCount());
	}

	default BigDecimal getFairwayInRegulationRate() {
		return Utils.divide(getFairwaysInRegulation(), getFairways());
	}

	default BigDecimal getGreensInRegulationRate() {
		return Utils.divide(getGreensInRegulation(), getHoleCount());
	}

	default BigDecimal getMinutesPerHole() {
		return getDuration() == null
		       ? BigDecimal.ZERO
		       : Utils.divide(Math.toIntExact(getDuration().toMinutes()), getHoleCount());
	}

	default BigDecimal getRating() {
		return getTee().getRating();
	}

	default BigDecimal getSlope() {
		return getTee().getSlope();
	}

	default Integer getPar() {
		return getTee().getPar();
	}

	default Integer getScore() { return getStrokes() - getPar(); }

	//TODO verify, should be: use shots gained, then tee yards, then default to 0
	default Long getYards() { return getTee().getYards(); }

	default BigDecimal getStrokesGained() { return BigDecimal.ZERO; }

	default Long getP75DrivingDistance() {
		List<Long> drives = getHoles().allShots().teeShots().map(shot -> shot.getDistance().getLengthInYards()).toList();
		return drives.isEmpty() ? 0L : percentile(drives, 75);
	}
	
	default Long getLongestDrive() {
		return getShots().teeShots()
				.map(shot -> shot.getDistance().getLengthInYards()).max(Long::compareTo).orElse(0L);
	}

	default Map<String, BigDecimal> getStrokesGainedByCategory() {
		return getHoles().strokesGainedByShotType();
	}
	
	static GolfRound compositeOf(@NonNull GolfRound round1, @NonNull GolfRound round2) {
		if (round1.equals(round2)) {
			throw new IllegalArgumentException("these rounds are the same");
		}

		if (!round1.isNineHoleRound() || !round2.isNineHoleRound()) {
			throw new IllegalArgumentException("both rounds must be nine hole rounds");
		}

		if (!round1.getGolfer().equals(round2.getGolfer())) {
			throw new IllegalArgumentException("cannot create composite round for two different golfers' rounds");
		}

		if (round1.getHoles().isEmpty() || round2.getHoles().isEmpty()) {
			return round1.getDate().isBefore(round2.getDate())
					? new SimpleCompositeGolfRound(round1, round2)
					: new SimpleCompositeGolfRound(round2, round1);

		}

		return round1.getDate().isBefore(round2.getDate())
				? new CompositeHoleByHoleRound(round1, round2)
				: new CompositeHoleByHoleRound(round2, round1);
	}

	static GolfRoundStream stream(Collection<GolfRound> rounds) {
		return new GolfRoundStream(rounds);
	}

	static List<GolfRound> compile(Map<Integer, RoundMeta> roundDetails, Map<Integer, List<Hole>> holes) {
		List<GolfRound> results = new ArrayList<>();

		for (Map.Entry<Integer, RoundMeta> each : roundDetails.entrySet()) {
			RoundMeta meta = each.getValue();
			Integer id = each.getKey();
			List<Hole> holeList = holes.get(id);
			if (meta != null && holeList != null) {
				results.add(of(meta, holeList));
			}
		}
		return results;
	}

	static GolfRound of(RoundMeta round, List<Hole> holes) {
		return new HoleByHoleRound(round, holes);
	}

	static GolfRound of(RoundMeta meta, Integer strokes, Integer fairwaysInRegulation, Integer fairways, Integer greensInRegulation, Integer putts, boolean nineHoleRound) {
		return new SimpleGolfRound(meta, strokes, fairwaysInRegulation, fairways, greensInRegulation, putts, nineHoleRound);
	}

}
