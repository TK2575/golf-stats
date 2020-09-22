package dev.tk2575.golfstats.golfround;

import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
public class GolfRoundFactory {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private LocalDate date;
	private String golferName;
	private String courseName;
	private String tees;
	private Duration duration;
	private String transport;
	private BigDecimal rating;
	private BigDecimal slope;
	private Integer par;
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;

	public GolfRound recordRound(String golferName, String[] row) {
		this.golferName = golferName;
		this.date = LocalDate.parse(row[0], DATE_FORMAT);
		this.courseName = row[1];
		this.tees = row[2];
		this.rating = new BigDecimal(row[3]);
		this.slope = new BigDecimal(row[4]);
		this.par = Integer.valueOf(row[5]);
		this.duration = row[6] == null || row[6].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));
		this.transport = row[7];
		this.score = Integer.valueOf(row[8]);
		this.fairwaysInRegulation = Integer.valueOf(row[9]);
		this.fairways = Integer.valueOf(row[10]);
		this.greensInRegulation = Integer.valueOf(row[11]);
		this.putts = Integer.valueOf(row[12]);

		Boolean nineHoleRound = Boolean.parseBoolean(row[13]);
		if (Boolean.TRUE.equals(nineHoleRound)) {
			return new NineHoleRound(this);
		}
		else {
			return new EighteenHoleRound(this);
		}
	}
}
