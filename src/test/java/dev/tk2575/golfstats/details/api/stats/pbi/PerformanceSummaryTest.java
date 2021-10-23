package dev.tk2575.golfstats.details.api.stats.pbi;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.details.api.stats.PerformanceSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceSummaryTest {

	@Test
	void lessThan18HolesPlayed() {
		Tee tee = Tee.of("Blue", new BigDecimal("69"), new BigDecimal("128"), 36);
		RoundMeta meta = new RoundMeta(LocalDate.now(), Duration.ofHours(2), Golfer.newGolfer("Tom"), Course.of("Balboa GC", tee), tee, "Walk");
		GolfRound round = GolfRound.of(meta, 36, 7, 7, 9, 18, true);
		assertNotNull(new PerformanceSummary(List.of(round)));
	}

}