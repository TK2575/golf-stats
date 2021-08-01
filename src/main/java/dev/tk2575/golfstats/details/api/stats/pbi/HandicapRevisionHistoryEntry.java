package dev.tk2575.golfstats.details.api.stats.pbi;

import dev.tk2575.golfstats.details.api.stats.PerformanceSummary;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class HandicapRevisionHistoryEntry {
	private final String golfer;
	private final LocalDate date;
	private final BigDecimal value;

	static List<HandicapRevisionHistoryEntry> compile(PerformanceSummary summary) {
		/*@formatter:off*/
		return summary.getHandicapRevisionHistory()
				.entrySet()
				.stream()
				.map(entry -> new HandicapRevisionHistoryEntry(summary.getGolfer(), entry.getKey(), entry.getValue()))
				.toList();
		/*@formatter:on*/
	}
}
