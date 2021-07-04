package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceSummaryTest {

	@Test
	void lessThan18HolesPlayed() {
		RoundMeta meta = new RoundMeta(LocalDate.now(), Duration.ofHours(2), Golfer.newGolfer("Tom"), Course.of("Balboa GC"), new BigDecimal("69"), new BigDecimal("128"), "Blue", "Walk");
		GolfRound round = GolfRound.of(meta, 36, 36, 7, 7, 9, 18, true);
		assertNotNull(new PerformanceSummary(List.of(round)));
	}

}