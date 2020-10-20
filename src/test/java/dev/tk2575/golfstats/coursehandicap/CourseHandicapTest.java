package dev.tk2575.golfstats.coursehandicap;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.SimpleGolfer;
import dev.tk2575.golfstats.golfround.Course;
import dev.tk2575.golfstats.golfround.Tee;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseHandicapTest {

	@Test
	void testSingleBogeyGolferOnLevelCourse() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Course course = Course.newCourse("Golden Pheasant");
		Tee tee = Tee.newTee("White", new BigDecimal("72"), new BigDecimal("113"), 72);

		CourseHandicap handicap = CourseHandicap.of(golfer, course, tee);
		assertEquals(18, handicap.getHandicapStrokes());
		assertEquals(18, handicap.getStablefordQuota());
	}

	@Test
	void testSingleGolferOnUnlevelCourse() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Course course = Course.newCourse("Balboa Park");
		Tee tee = Tee.newTee("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);

		CourseHandicap handicap = CourseHandicap.of(golfer, course, tee);
		assertEquals(15, handicap.getHandicapStrokes());
		assertEquals(36-15, handicap.getStablefordQuota());
	}

	@Test
	void testGolferTeamOnLevelCourse() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("24.0")));
		Course course = Course.newCourse("Golden Pheasant");
		Tee tee = Tee.newTee("White", new BigDecimal("72"), new BigDecimal("113"), 72);

		CourseHandicap handicap = CourseHandicap.teamOf(List.of(tom, will), course, tee);
		assertEquals(42, handicap.getHandicapStrokes());
		assertEquals(30, handicap.getStablefordQuota());
	}

	@Test
	void testsGolferTeamOnUnLevelCourse() {
		Golfer tom = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("14.2")));
		Golfer will = Golfer.of("Will", HandicapIndex.of(new BigDecimal("25.9")));
		Course course = Course.newCourse("Balboa Park");
		Tee tee = Tee.newTee("Blue", new BigDecimal("71.2"), new BigDecimal("125"), 72);

		CourseHandicap handicap = CourseHandicap.teamOf(List.of(tom, will), course, tee);
		assertEquals(43, handicap.getHandicapStrokes());
		assertEquals(15, handicap.getHandicapStrokesPerGolfer().get("Tom"));
		assertEquals(28, handicap.getHandicapStrokesPerGolfer().get("Will"));
		assertEquals(72-43, handicap.getStablefordQuota());
	}

	@Test
	void testSingleBogeyGolferOnNineHoleCourse() {
		Golfer golfer = Golfer.of("Tom", HandicapIndex.of(new BigDecimal("18.0")));
		Course course = Course.newCourse("Golden Pheasant");
		Tee tee = Tee.newTee("White", new BigDecimal("30.7"), new BigDecimal("111"), 32);

		CourseHandicap handicap = CourseHandicap.of(golfer, course, tee);
		assertEquals(8, handicap.getHandicapStrokes());
		assertEquals(10, handicap.getStablefordQuota());
	}

}