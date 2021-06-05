package dev.tk2575.golfstats.core.strokesgained;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static dev.tk2575.Utils.roundToTwoDecimalPlaces;

class BroadieImputed implements ShotsGainedComputation {

	private static SortedMap<Integer, BigDecimal> teeMap;
	private static SortedMap<Integer, BigDecimal> fairwayMap;
	private static SortedMap<Integer, BigDecimal> roughMap;
	private static SortedMap<Integer, BigDecimal> sandMap;
	private static SortedMap<Integer, BigDecimal> recoveryMap;
	private static SortedMap<Integer, BigDecimal> greenMap;

	private Map<String, BigDecimal> strokesGainedMap;

	Map<String, BigDecimal> getStrokesGainedMap() {
		return Collections.unmodifiableMap(this.strokesGainedMap);
	}

	private static class SingletonHelper {

		private static final BroadieImputed INSTANCE = new BroadieImputed();
	}

	static BroadieImputed getInstance() {
		return SingletonHelper.INSTANCE;
	}

	@Override
	public Shot analyzeShot(Shot shot, Shot result) {
		BigDecimal baseline = strokesGainedMap.get(shotKey(shot));
		BigDecimal strokesGained = Utils.roundToTwoDecimalPlaces(baseline.subtract(strokesGainedMap.get(shotKey(result)))
		                              .subtract(BigDecimal.valueOf(shot.getCount())));
		return Shot.strokesGained(shot, baseline, strokesGained);
	}

	String shotKey(Shot shot) {
		return shot.getLie().getAbbrev() + shot.getDistance().getValue();
	}

	private BroadieImputed() {
		initGreenMap();
		initFairwayMap();
		initRoughMap();
		initSandMap();
		initTeeMap();
		initRecoveryMap();
		initStrokesGainedMap();
	}

	private void initStrokesGainedMap() {
		strokesGainedMap = new HashMap<>();
		strokesGainedMap.put("h0", BigDecimal.ZERO);
		addGreenToStrokesGainedMap();
		addToStrokesGainedMapByYardage("t", teeMap);
		addToStrokesGainedMapByYardage("f", fairwayMap);
		addToStrokesGainedMapByYardage("r", roughMap);
		addToStrokesGainedMapByYardage("s", sandMap);
		addToStrokesGainedMapByYardage("y", recoveryMap);
	}

	private void addGreenToStrokesGainedMap() {
		BigDecimal lowValue = greenMap.get(3);
		BigDecimal highValue = greenMap.get(4);

		if (lowValue == null || highValue == null) {
			throw new IllegalStateException("unable to initialize strokes gained map");
		}

		strokesGainedMap.put("g1", lowValue);
		strokesGainedMap.put("g2", lowValue);

		BigDecimal currentValue;
		int increments = 5;
		int lowDistance = 3;
		int nextDistance = 15;

		for (int distance = 3; distance <= 90; distance++) {
			if (distance == 20) { increments = 10; }
			if (distance == 60) { increments = 30; }

			currentValue = greenMap.get(distance);
			if (currentValue == null) {
				currentValue = computeImputedValue(distance, lowDistance, nextDistance, lowValue, highValue);
			}
			else {
				lowValue = currentValue;
				lowDistance = distance;
				nextDistance = distance + increments;
				highValue = greenMap.get(nextDistance);
			}

			strokesGainedMap.put("g" + distance, roundToTwoDecimalPlaces(currentValue));
		}
	}

	private void addToStrokesGainedMapByYardage(String abbrev, SortedMap<Integer, BigDecimal> sourceMap) {
		int lowYardage = 1;
		int nextYardage = 10;

		BigDecimal lowValue = sourceMap.get(lowYardage);
		BigDecimal highValue = sourceMap.get(nextYardage);

		if (lowValue == null || highValue == null) {
			throw new IllegalStateException("unable to initialize strokes gained map");
		}

		strokesGainedMap.put(abbrev + lowYardage, lowValue);

		BigDecimal currentValue;
		for (int yards = 2; yards <= 600; yards++) {
			currentValue = sourceMap.get(yards);
			if (currentValue == null) {
				currentValue = computeImputedValue(yards, lowYardage, nextYardage, lowValue, highValue);
			}
			else {
				lowValue = currentValue;
				lowYardage = yards;
				nextYardage = lowYardage + getYardIncrements(yards);
				highValue = sourceMap.get(nextYardage);
			}

			strokesGainedMap.put(abbrev + yards, roundToTwoDecimalPlaces(currentValue));
		}
	}

	private static int getYardIncrements(int yards) {
		return yards < 100 ? 10 : 20;
	}

	private static BigDecimal computeImputedValue(int distance, int lowDistance, int nextDistance, BigDecimal lowValue, BigDecimal nextValue) {
		int increments = nextDistance - lowDistance;
		int steps = (distance - lowDistance) % increments;
		BigDecimal diff = nextValue.subtract(lowValue).abs();
		BigDecimal stepValue = diff.divide(BigDecimal.valueOf(increments), 4, RoundingMode.HALF_UP);
		BigDecimal totalStepValue = stepValue.multiply(BigDecimal.valueOf(steps));

		return totalStepValue.add(lowValue.compareTo(nextValue) <= 0 ? lowValue : nextValue);
	}

	private static void initTeeMap() {
		teeMap = new TreeMap<>();

		teeMap.put(1, fairwayMap.get(1));
		teeMap.put(10, fairwayMap.get(10));
		teeMap.put(20, fairwayMap.get(20));
		teeMap.put(30, fairwayMap.get(30));
		teeMap.put(40, fairwayMap.get(40));
		teeMap.put(50, fairwayMap.get(50));
		teeMap.put(60, fairwayMap.get(60));
		teeMap.put(70, fairwayMap.get(70));
		teeMap.put(80, fairwayMap.get(80));
		teeMap.put(90, fairwayMap.get(90));

		teeMap.put(100, new BigDecimal("2.92"));
		teeMap.put(120, new BigDecimal("2.99"));
		teeMap.put(140, new BigDecimal("2.97"));
		teeMap.put(160, new BigDecimal("2.99"));
		teeMap.put(180, new BigDecimal("3.05"));
		teeMap.put(200, new BigDecimal("3.12"));
		teeMap.put(220, new BigDecimal("3.17"));
		teeMap.put(240, new BigDecimal("3.25"));
		teeMap.put(260, new BigDecimal("3.45"));
		teeMap.put(280, new BigDecimal("3.65"));
		teeMap.put(300, new BigDecimal("3.71"));
		teeMap.put(320, new BigDecimal("3.79"));
		teeMap.put(340, new BigDecimal("3.86"));
		teeMap.put(360, new BigDecimal("3.92"));
		teeMap.put(380, new BigDecimal("3.96"));
		teeMap.put(400, new BigDecimal("3.99"));
		teeMap.put(420, new BigDecimal("4.02"));
		teeMap.put(440, new BigDecimal("4.08"));
		teeMap.put(460, new BigDecimal("4.17"));
		teeMap.put(480, new BigDecimal("4.28"));
		teeMap.put(500, new BigDecimal("4.41"));
		teeMap.put(520, new BigDecimal("4.54"));
		teeMap.put(540, new BigDecimal("4.65"));
		teeMap.put(560, new BigDecimal("4.74"));
		teeMap.put(580, new BigDecimal("4.79"));
		teeMap.put(600, new BigDecimal("4.82"));
	}

	private static void initFairwayMap() {
		fairwayMap = new TreeMap<>();

		fairwayMap.put(1, greenMap.get(3));

		fairwayMap.put(10, new BigDecimal("2.18"));
		fairwayMap.put(20, new BigDecimal("2.40"));
		fairwayMap.put(30, new BigDecimal("2.52"));
		fairwayMap.put(40, new BigDecimal("2.60"));
		fairwayMap.put(50, new BigDecimal("2.66"));
		fairwayMap.put(60, new BigDecimal("2.70"));
		fairwayMap.put(70, new BigDecimal("2.72"));
		fairwayMap.put(80, new BigDecimal("2.75"));
		fairwayMap.put(90, new BigDecimal("2.77"));
		fairwayMap.put(100, new BigDecimal("2.80"));
		fairwayMap.put(120, new BigDecimal("2.85"));
		fairwayMap.put(140, new BigDecimal("2.91"));
		fairwayMap.put(160, new BigDecimal("2.98"));
		fairwayMap.put(180, new BigDecimal("3.08"));
		fairwayMap.put(200, new BigDecimal("3.19"));
		fairwayMap.put(220, new BigDecimal("3.32"));
		fairwayMap.put(240, new BigDecimal("3.45"));
		fairwayMap.put(260, new BigDecimal("3.58"));
		fairwayMap.put(280, new BigDecimal("3.69"));
		fairwayMap.put(300, new BigDecimal("3.78"));
		fairwayMap.put(320, new BigDecimal("3.84"));
		fairwayMap.put(340, new BigDecimal("3.88"));
		fairwayMap.put(360, new BigDecimal("3.95"));
		fairwayMap.put(380, new BigDecimal("4.03"));
		fairwayMap.put(400, new BigDecimal("4.11"));
		fairwayMap.put(420, new BigDecimal("4.15"));
		fairwayMap.put(440, new BigDecimal("4.20"));
		fairwayMap.put(460, new BigDecimal("4.29"));
		fairwayMap.put(480, new BigDecimal("4.40"));
		fairwayMap.put(500, new BigDecimal("4.53"));
		fairwayMap.put(520, new BigDecimal("4.66"));
		fairwayMap.put(540, new BigDecimal("4.78"));
		fairwayMap.put(560, new BigDecimal("4.86"));
		fairwayMap.put(580, new BigDecimal("4.91"));
		fairwayMap.put(600, new BigDecimal("4.94"));
	}

	private static void initRoughMap() {
		roughMap = new TreeMap<>();

		roughMap.put(1, greenMap.get(3));

		roughMap.put(10, new BigDecimal("2.34"));
		roughMap.put(20, new BigDecimal("2.59"));
		roughMap.put(30, new BigDecimal("2.70"));
		roughMap.put(40, new BigDecimal("2.78"));
		roughMap.put(50, new BigDecimal("2.87"));
		roughMap.put(60, new BigDecimal("2.91"));
		roughMap.put(70, new BigDecimal("2.93"));
		roughMap.put(80, new BigDecimal("2.96"));
		roughMap.put(90, new BigDecimal("2.99"));
		roughMap.put(100, new BigDecimal("3.02"));
		roughMap.put(120, new BigDecimal("3.08"));
		roughMap.put(140, new BigDecimal("3.15"));
		roughMap.put(160, new BigDecimal("3.23"));
		roughMap.put(180, new BigDecimal("3.31"));
		roughMap.put(200, new BigDecimal("3.42"));
		roughMap.put(220, new BigDecimal("3.53"));
		roughMap.put(240, new BigDecimal("3.64"));
		roughMap.put(260, new BigDecimal("3.64"));
		roughMap.put(280, new BigDecimal("3.83"));
		roughMap.put(300, new BigDecimal("3.90"));
		roughMap.put(320, new BigDecimal("3.95"));
		roughMap.put(340, new BigDecimal("4.02"));
		roughMap.put(360, new BigDecimal("4.11"));
		roughMap.put(380, new BigDecimal("4.21"));
		roughMap.put(400, new BigDecimal("4.30"));
		roughMap.put(420, new BigDecimal("4.34"));
		roughMap.put(440, new BigDecimal("4.39"));
		roughMap.put(460, new BigDecimal("4.48"));
		roughMap.put(480, new BigDecimal("4.59"));
		roughMap.put(500, new BigDecimal("4.72"));
		roughMap.put(520, new BigDecimal("4.85"));
		roughMap.put(540, new BigDecimal("4.97"));
		roughMap.put(560, new BigDecimal("5.05"));
		roughMap.put(580, new BigDecimal("5.10"));
		roughMap.put(600, new BigDecimal("5.13"));
	}

	private static void initSandMap() {
		sandMap = new TreeMap<>();

		sandMap.put(1, greenMap.get(3));

		sandMap.put(10, new BigDecimal("2.43"));
		sandMap.put(20, new BigDecimal("2.53"));
		sandMap.put(30, new BigDecimal("2.66"));
		sandMap.put(40, new BigDecimal("2.82"));
		sandMap.put(50, new BigDecimal("2.92"));
		sandMap.put(60, new BigDecimal("3.15"));
		sandMap.put(70, new BigDecimal("3.21"));
		sandMap.put(80, new BigDecimal("3.24"));
		sandMap.put(90, new BigDecimal("3.24"));
		sandMap.put(100, new BigDecimal("3.23"));
		sandMap.put(120, new BigDecimal("3.21"));
		sandMap.put(140, new BigDecimal("3.22"));
		sandMap.put(160, new BigDecimal("3.28"));
		sandMap.put(180, new BigDecimal("3.40"));
		sandMap.put(200, new BigDecimal("3.55"));
		sandMap.put(220, new BigDecimal("3.70"));
		sandMap.put(240, new BigDecimal("3.84"));
		sandMap.put(260, new BigDecimal("3.93"));
		sandMap.put(280, new BigDecimal("4.00"));
		sandMap.put(300, new BigDecimal("4.04"));
		sandMap.put(320, new BigDecimal("4.12"));
		sandMap.put(340, new BigDecimal("4.26"));
		sandMap.put(360, new BigDecimal("4.41"));
		sandMap.put(380, new BigDecimal("4.55"));
		sandMap.put(400, new BigDecimal("4.69"));
		sandMap.put(420, new BigDecimal("4.73"));
		sandMap.put(440, new BigDecimal("4.78"));
		sandMap.put(460, new BigDecimal("4.87"));
		sandMap.put(480, new BigDecimal("4.98"));
		sandMap.put(500, new BigDecimal("5.11"));
		sandMap.put(520, new BigDecimal("5.24"));
		sandMap.put(540, new BigDecimal("5.36"));
		sandMap.put(560, new BigDecimal("5.44"));
		sandMap.put(580, new BigDecimal("5.49"));
		sandMap.put(600, new BigDecimal("5.52"));
	}

	private static void initRecoveryMap() {
		recoveryMap = new TreeMap<>();

		recoveryMap.put(1, roughMap.get(1));

		recoveryMap.put(10, new BigDecimal("3.45"));
		recoveryMap.put(20, new BigDecimal("3.51"));
		recoveryMap.put(30, new BigDecimal("3.57"));
		recoveryMap.put(40, new BigDecimal("3.71"));
		recoveryMap.put(50, new BigDecimal("3.79"));
		recoveryMap.put(60, new BigDecimal("3.83"));
		recoveryMap.put(70, new BigDecimal("3.84"));
		recoveryMap.put(80, new BigDecimal("3.84"));
		recoveryMap.put(90, new BigDecimal("3.82"));
		recoveryMap.put(100, new BigDecimal("3.80"));
		recoveryMap.put(120, new BigDecimal("3.78"));
		recoveryMap.put(140, new BigDecimal("3.80"));
		recoveryMap.put(160, new BigDecimal("3.81"));
		recoveryMap.put(180, new BigDecimal("3.82"));
		recoveryMap.put(200, new BigDecimal("3.87"));
		recoveryMap.put(220, new BigDecimal("3.92"));
		recoveryMap.put(240, new BigDecimal("3.97"));
		recoveryMap.put(260, new BigDecimal("4.03"));
		recoveryMap.put(280, new BigDecimal("4.10"));
		recoveryMap.put(300, new BigDecimal("4.20"));
		recoveryMap.put(320, new BigDecimal("4.31"));
		recoveryMap.put(340, new BigDecimal("4.44"));
		recoveryMap.put(360, new BigDecimal("4.56"));
		recoveryMap.put(380, new BigDecimal("4.66"));
		recoveryMap.put(400, new BigDecimal("4.75"));
		recoveryMap.put(420, new BigDecimal("4.79"));
		recoveryMap.put(440, new BigDecimal("4.84"));
		recoveryMap.put(460, new BigDecimal("4.93"));
		recoveryMap.put(480, new BigDecimal("5.04"));
		recoveryMap.put(500, new BigDecimal("5.17"));
		recoveryMap.put(520, new BigDecimal("5.30"));
		recoveryMap.put(540, new BigDecimal("5.42"));
		recoveryMap.put(560, new BigDecimal("5.50"));
		recoveryMap.put(580, new BigDecimal("5.55"));
		recoveryMap.put(600, new BigDecimal("5.58"));
	}

	private static void initGreenMap() {
		greenMap = new TreeMap<>();
		greenMap.put(3, new BigDecimal("1.04"));
		greenMap.put(4, new BigDecimal("1.13"));
		greenMap.put(5, new BigDecimal("1.23"));
		greenMap.put(6, new BigDecimal("1.34"));
		greenMap.put(7, new BigDecimal("1.42"));
		greenMap.put(8, new BigDecimal("1.50"));
		greenMap.put(9, new BigDecimal("1.56"));
		greenMap.put(10, new BigDecimal("1.61"));
		greenMap.put(15, new BigDecimal("1.78"));
		greenMap.put(20, new BigDecimal("1.87"));
		greenMap.put(30, new BigDecimal("1.98"));
		greenMap.put(40, new BigDecimal("2.06"));
		greenMap.put(50, new BigDecimal("2.14"));
		greenMap.put(60, new BigDecimal("2.21"));
		greenMap.put(90, new BigDecimal("2.40"));
	}
}
