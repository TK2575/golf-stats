package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.MovingAverage;
import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.GolfRoundStream;
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
    /*@formatter:off*/
		return GolfRoundResourceManager.getInstance()
				.getRoundsByGolfer()
				.values()
				.stream()
				.map(PerformanceDetail::new)
				.toList();
		/*@formatter:on*/
  }

  @RequestMapping(value = "recentShots.tsv", produces = "text/tsv")
  public String recentShots() {
    List<List<String>> rows = new ArrayList<>();

    /*@formatter:off*/
		rows.add(
				List.of("Lie",
						"Category",
						"Distance Value",
						"Distance Unit",
						"Strokes Gained",
						"Result Lie",
						"Result Distance Value",
						"Result Distance Unit",
						"Miss Direction",
						"Count")
		);
		stats().stream()
				.filter(each -> each.getGolfer().equals("Tom"))
				.map(PerformanceDetail::getRoundDetails).flatMap(List::stream)
				.filter(each -> each.getDate().getYear() >= 2020 && each.getDate().getYear() < 2022)
				.map(RoundDetail::getShots).flatMap(List::stream)
				.filter(each -> each.getCategory().equals("Tee"))
				.forEach(each -> {
					List<String> fields = new ArrayList<>();
					fields.add(each.getLie());
					fields.add(each.getCategory());
					fields.add(String.valueOf(each.getDistanceValue()));
					fields.add(each.getDistanceUnit());
					fields.add(each.getStrokesGained().toString());
					fields.add(each.getResultLie());
					fields.add(String.valueOf(each.getMissDistanceValue()));
					fields.add(each.getMissDistanceUnit());
					fields.add(each.getMissDescription());
					fields.add(String.valueOf(each.getCount()));
					rows.add(fields);
				});
		/*@formatter:on*/

    StringBuilder sb = new StringBuilder();
    rows.forEach(row -> sb.append(Utils.convertToTSV(row)).append("\n"));
    return sb.toString();
  }

  @RequestMapping(value = "rounds", produces = "text/csv")
  public String rounds() {
		List<RoundTableRow> rows = getTomStats()
				.compileTo18HoleRounds()
				.sortNewestToOldest()
				.map(RoundTableRow::new)
				.toList();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(RoundTableRow.headers())).append("\n");
		rows.forEach(row -> sb.append(Utils.convertToCSV(row.values())).append("\n"));
		return sb.toString();
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
		List<RollingStat> results = new ArrayList<>();
		Map<String, MovingAverage> movingAverages = new HashMap<>();
		List<GolfRound> rounds = getTomStats()
				.compileTo18HoleRounds()
				.sortOldestToNewest()
				.toList();

		//strokes gained, all categories, per 18 hole round
		new GolfRoundStream(rounds)
				.forEachOrdered(round -> round.getStrokesGainedByCategory().forEach((cat, sg) -> {
			var mAvg = movingAverages.getOrDefault(cat, new MovingAverage(window));
			var next = mAvg.next(sg);
			results.add(
					new RollingStat(
							"Strokes Gained: " + cat, 
							next.getValue(), 
							round.getDate(), 
							next.getKey()));
			movingAverages.put(cat, mAvg);
		}));

		//TODO driving distance (p75)

		//birdie or better vs double or worse rate
		var mAvg = new MovingAverage(window);
		rounds.forEach(round -> {
			var next = mAvg.next(new BigDecimal(round.getBirdieVsDoubleRatio()));
			results.add(
					new RollingStat(
							"Birdie vs Double Ratio", 
							next.getValue(), 
							round.getDate(), 
							next.getKey()));
		});
		
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
