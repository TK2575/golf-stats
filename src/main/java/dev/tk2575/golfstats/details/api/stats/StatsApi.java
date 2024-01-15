package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("stats")
@Log4j2
public class StatsApi {

  @GetMapping(produces = "application/json")
  public List<PerformanceDetail> stats() {
	return GolfRoundResourceManager.getInstance()
			.getRoundsByGolfer()
			.values()
			.stream()
			.map(PerformanceDetail::new)
			.toList();
  }

  @RequestMapping(value = "rounds", produces = "text/csv")
  public String rounds() {
		var rounds = getTomStats()
				.compileTo18HoleRounds()
				.sortNewestToOldest().toList();
		
		HandicapIndex index = HandicapIndex.newIndex(rounds);
		var rows = rounds.stream().map(round -> new RoundTableRow(round, index)).toList();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(RoundTableRow.headers())).append("\n");
		rows.forEach(row -> sb.append(Utils.convertToCSV(row.values())).append("\n"));
		return sb.toString();
	}
	
	@RequestMapping(value = "latest", produces = "text/csv")
	public String latestRound() {
		List<RoundDetailTableColumn> columns = getTomStats()
				.compileTo18HoleRounds()
				.sortNewestToOldest()
				.findFirst()
				.map(RoundDetailTableColumn::compile)
				.orElseThrow();

		return RoundDetailTableColumn.toCSV(columns);
	}

	@RequestMapping(value = "putting", produces = "text/csv")
	public String putting() {
		//TODO bin distances?
		List<PuttingDistanceStat> stats = getTomStats()
				.flatMap(round -> round.getHoles().allShots().greenShots())
				.collect(Collectors.groupingBy(shot -> shot.getDistanceFromTarget().getLengthInFeet()))
				.values().stream().map(PuttingDistanceStat::new)
				.sorted(Comparator.comparing(PuttingDistanceStat::getDistance)).toList();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(PuttingDistanceStat.headers())).append("\n");
		stats.forEach(stat -> sb.append(Utils.convertToCSV(stat.values())).append("\n"));
		return sb.toString();
	}
	
	@RequestMapping(value = "rolling", produces = "text/csv")
	public String rolling(@RequestParam(defaultValue = "10") int window) {
		List<GolfRound> rounds = getTomStats()
				.compileTo18HoleRounds()
				.sortOldestToNewest()
				.toList();
		
		List<RollingStat> results = new ArrayList<>();
		Map<String, MovingAverage> movingAverages = new HashMap<>();
		var driveAvg = new MovingAverage(window);
		var birdieAvg = new MovingAverage(window);
		
		for (GolfRound round : rounds) {
			//driving distance
			var driveDist = round.getP75DrivingDistance();
			if (driveDist > 0) {
				results.add(new RollingStat(
						"p75 Driving Distance", 
						driveAvg.next(new BigDecimal(driveDist)), 
						round));
			}
			
			//birdie or better rate
			results.add(new RollingStat(
					"Birdie vs Double Ratio", 
					birdieAvg.next(new BigDecimal(round.getBirdieVsDoubleRatio())), 
					round)
			);
			
			//strokes gained
			for (Map.Entry<String, BigDecimal> entry : round.getStrokesGainedByCategory().entrySet()) {
				var mAvg = movingAverages.getOrDefault(entry.getKey(), new MovingAverage(window));
				results.add(new RollingStat(
						"Strokes Gained: " + entry.getKey(), 
						mAvg.next(entry.getValue()), 
						round));
				movingAverages.put(entry.getKey(), mAvg);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(RollingStat.headers())).append("\n");
		results.stream()
				.sorted(Comparator.comparing(RollingStat::getName).thenComparing(RollingStat::getSequence))
				.forEachOrdered(stat -> sb.append(Utils.convertToCSV(stat.values())).append("\n"));
		return sb.toString();
	}

	private static GolfRoundStream getTomStats() {
		return new GolfRoundStream(
				GolfRoundResourceManager.getInstance().getRoundsByGolfer().get("Tom")
		);
	}
	

}
