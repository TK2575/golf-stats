package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.course.tee.Tee;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
public class SimpleGolfRound implements GolfRound {

	@Getter(AccessLevel.NONE)
	private static final Integer HOLES = 18;

	public Integer getHoleCount() {
		return HOLES;
	}

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final Tee tee;
	private final Transport transport;

	private BigDecimal scoreDifferential;

	private Integer strokes;
	private Integer strokesAdjusted;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	private boolean nineHoleRound;

	public SimpleGolfRound(SimpleGolfRoundCSVParser factory) {
		this.date = factory.getDate();
		this.golfer = Golfer.newGolfer(factory.getGolferName());
		this.course = Course.of(factory.getCourseName());
		this.tee = Tee.of(factory.getTees(), factory.getRating(), factory.getSlope(), factory.getPar());
		this.duration = factory.getDuration();
		this.transport = factory.getTransport();
		this.strokes = factory.getScore();
		this.strokesAdjusted = this.strokes;
		this.fairwaysInRegulation = factory.getFairwaysInRegulation();
		this.fairways = factory.getFairways();
		this.greensInRegulation = factory.getGreensInRegulation();
		this.putts = factory.getPutts();
		this.scoreDifferential = computeScoreDifferential();
		this.nineHoleRound = factory.getNineHoleRound();
	}

	public SimpleGolfRound(String[] row, DateTimeFormatter dateFormat, DateTimeFormatter durationFormat) {
		this.golfer = Golfer.newGolfer(row[1]);
		this.date = LocalDate.parse(row[2], dateFormat);
		this.course = Course.of(row[3]);
		this.tee = Tee.of(row[4], new BigDecimal(row[5]), new BigDecimal(row[6]), Integer.valueOf(row[7]));
		this.duration = row[8] == null || row[8].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[8], durationFormat));
		this.transport = Transport.valueOf(row[9]);
	}
}
