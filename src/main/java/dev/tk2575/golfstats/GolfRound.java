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

	private LocalDate date;
	private String golfer;
	private String course;
	private String tees;
	private Duration duration;
	private String transport;
	private BigDecimal rating; //put whatever's on the scorecard, adjust if it's way off of par
	private BigDecimal slope;
	private Integer par; //expect par of holes played to be inputted (i.e. put 36 if playing a nine hole round)
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;
	private Boolean nineHoleRound;

	public GolfRound(String golfer, String[] row) {
		this.golfer = golfer;
		this.date = LocalDate.parse(row[0], DATE_FORMAT);
		this.course = row[1];
		this.tees = row[2];
		this.rating = new BigDecimal(row[3]);
		this.slope = new BigDecimal(row[4]);
		this.par = Integer.valueOf(row[5]);
		this.duration = row[6] == null || row[6].isBlank() ? null : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));
		this.transport = row[7];
		this.score = Integer.valueOf(row[8]);
		this.fairwaysInRegulation = Integer.valueOf(row[9]);
		this.fairways = Integer.valueOf(row[10]);
		this.greensInRegulation = Integer.valueOf(row[11]);
		this.putts = Integer.valueOf(row[12]);
		this.nineHoleRound = Boolean.parseBoolean(row[13]);
	}
}
