package dev.tk2575.golfstats.handicapindex;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.tee.Tee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StablefordQuotaTest {

	@Test
	void testSingleBogeyGolferOnLevelTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Tee tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 72);
		StablefordQuota quota = tee.stablefordQuota(golfer);

		assertEquals(18, quota.getQuotaPerGolfer().get(golfer.getKey()));
	}

	@Test
	void testSingleGolferOnUnlevelTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Tee tee = Tee.of("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);
		StablefordQuota quota = tee.stablefordQuota(golfer);

		assertEquals(36-15, quota.getQuotaPerGolfer().get(golfer.getKey()));
	}

	@Test
	void testGolfersOnLevelTee() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("24.0")));
		Tee tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 72);
		StablefordQuota quota = tee.stablefordQuota(List.of(tom, will));

		assertEquals(18, quota.getQuotaPerGolfer().get(tom.getKey()));
		assertEquals(36-24, quota.getQuotaPerGolfer().get(will.getKey()));
		assertEquals(18+36-24, quota.getTotalQuota());
	}

	@Test
	void testGolfersUnLevelTee() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("25.9")));
		Tee tee = Tee.of("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);
		StablefordQuota quota = tee.stablefordQuota(List.of(tom, will));

		assertEquals(36-15, quota.getQuotaPerGolfer().get(tom.getKey()));
		assertEquals(36-28, quota.getQuotaPerGolfer().get(will.getKey()));
		assertEquals(72-15-28, quota.getTotalQuota());
	}

	@Test
	void testSingleBogeyGolferOnNineHoleTee() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Tee tee = Tee.of("White", new BigDecimal("30.7"), new BigDecimal("111"), 32);
		StablefordQuota quota = tee.stablefordQuota(golfer);

		assertEquals(18-8, quota.getQuotaPerGolfer().get(golfer.getKey()));
	}

	@Test
	void testSingleGolferZeroQuota() {
		Golfer golfer = Golfer.of("Double-Bogey Dave", HandicapIndex.of(new BigDecimal("38.0")));
		Tee tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 72);
		StablefordQuota quota = tee.stablefordQuota(golfer);
		assertEquals(0, quota.getQuotaPerGolfer().get(golfer.getKey()));

		tee = Tee.of("White", new BigDecimal("72"), new BigDecimal("113"), 36);
		quota = tee.stablefordQuota(golfer);
		assertEquals(0, quota.getQuotaPerGolfer().get(golfer.getKey()));
	}

}