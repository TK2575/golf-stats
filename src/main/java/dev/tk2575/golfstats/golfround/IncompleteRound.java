package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Golfer;
import dev.tk2575.golfstats.course.Course;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class IncompleteRound {

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final String teeName;
	private final Transport transport;

	public IncompleteRound(String[] row, DateTimeFormatter dateFormat, DateTimeFormatter durationFormat, HandicapIndex index) {
		this.golfer = Golfer.of(row[1], index);
		this.date = LocalDate.parse(row[2], dateFormat);
		this.course = Course.of(row[3]);
		this.teeName = row[4];
		this.rating = new BigDecimal(row[5]);
		this.slope = new BigDecimal(row[6]);
		this.duration = row[7] == null || row[7].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[7], durationFormat));
		this.transport = Transport.valueOf(row[8]);
	}

	public IncompleteRound(IncompleteRound round, HandicapIndex newIndex) {
		this.golfer = Golfer.of(round.getGolfer(), newIndex);
		this.date = round.getDate();
		this.course = round.getCourse();
		this.teeName = round.getTeeName();
		this.rating = round.getRating();
		this.slope = round.getSlope();
		this.duration = round.getDuration();
		this.transport = round.getTransport();
	}
}
