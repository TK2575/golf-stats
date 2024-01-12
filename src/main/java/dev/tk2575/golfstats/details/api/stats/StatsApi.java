package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
		//TODO combine 9 hole rounds?
		List<RoundTableRow> rows = getTomStats().stream()
				.map(RoundTableRow::new)
				.sorted(Comparator.comparing(RoundTableRow::getDate).reversed())
				.toList();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(RoundTableRow.headers())).append("\n");
		rows.forEach(row -> sb.append(Utils.convertToCSV(row.values())).append("\n"));
		return sb.toString();
	}

	@RequestMapping(value = "putting", produces = "text/csv")
	public String putting() {
		//TODO bin distances?
		List<PuttingDistanceStat> stats = getTomStats().stream()
				.flatMap(round -> round.getHoles().allShots().greenShots())
				.collect(Collectors.groupingBy(shot -> shot.getDistanceFromTarget().getLengthInFeet()))
				.values().stream().map(PuttingDistanceStat::new)
				.sorted(Comparator.comparing(PuttingDistanceStat::getDistance)).toList();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Utils.convertToCSV(PuttingDistanceStat.headers())).append("\n");
		stats.forEach(stat -> sb.append(Utils.convertToCSV(stat.values())).append("\n"));
		return sb.toString();
	}

	private static List<GolfRound> getTomStats() {
		return GolfRoundResourceManager.getInstance().getRoundsByGolfer().get("Tom");
	}
	

}
