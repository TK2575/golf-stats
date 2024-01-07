package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.details.GolfRoundResourceManager;
import dev.tk2575.golfstats.details.api.PerformanceDetail;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class StatsApi {

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


}
