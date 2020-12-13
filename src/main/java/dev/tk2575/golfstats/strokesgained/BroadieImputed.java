package dev.tk2575.golfstats.strokesgained;

import dev.tk2575.golfstats.golfround.shotbyshot.Shot;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.TreeMap;

public class BroadieImputed implements ShotsGainedComputation {

	//TODO singleton that calculates/stores all strokes gained values by shot (lie & distance)

	private static SortedMap<Integer, BigDecimal> teeMap;
	private static SortedMap<Integer, BigDecimal> fairwayMap;
	private static SortedMap<Integer, BigDecimal> roughMap;
	private static SortedMap<Integer, BigDecimal> sandMap;
	private static SortedMap<Integer, BigDecimal> recoveryMap;

	private static SortedMap<Integer, BigDecimal> greenMap;

	public BroadieImputed() {
		initTeeMap();
		initFairwayMap();
		initRoughMap();
		initSandMap();
		initRecoveryMap();
		initGreenMap();
	}

	private static void initTeeMap() {
		teeMap = new TreeMap<>();
		teeMap.put(100, new BigDecimal("2.92"));
		teeMap.put(120, new BigDecimal("2.99"));
		teeMap.put(140, new BigDecimal("2.97"));
		teeMap.put(160, new BigDecimal("2.99"));
		teeMap.put(180, new BigDecimal("3.05"));
		teeMap.put(200, new BigDecimal("3.12"));
		teeMap.put(220, new BigDecimal("3.17"));
	}

	@Override
	public ShotsGained getShotsGained(Shot shot) {

	}
}
