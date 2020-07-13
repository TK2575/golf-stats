package dev.tk2575.golfstats;

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
public class GolfRound {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	LocalDate date;
	String golfer;
	String course;
	String tees;
	Duration duration;
	String transport;
	BigDecimal rating;
	BigDecimal slope;
	Long par;
	Long score;
	Long fairwaysInRegulation;
	Long fairways;
	Long greensInRegulation;
	Long putts;
	Boolean nineHoleRound;

	public GolfRound(String golfer, String[] row) {
		this.golfer = golfer;
		this.date = LocalDate.parse(row[0], DATE_FORMAT);
		this.course = row[1];
		this.tees = row[2];
		this.rating = new BigDecimal(row[3]);
		this.slope = new BigDecimal(row[4]);
		this.par = Long.valueOf(row[5]);
		this.duration = row[6] == null || row[6].isBlank() ? null : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));
		this.transport = row[7];
		this.score = Long.valueOf(row[8]);
		this.fairwaysInRegulation = Long.valueOf(row[9]);
		this.fairways = Long.valueOf(row[10]);
		this.greensInRegulation = Long.valueOf(row[11]);
		this.putts = Long.valueOf(row[12]);
		this.nineHoleRound = Boolean.getBoolean(row[13]);
	}
}
