package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Getter
@ToString
public class Hole19Round {
	private static final ZoneId EST_TIMEZONE = ZoneId.of("America/New_York");

	private String shareId;
	private String startedAt;
	private String endedAt;
	private String inputMode;
	private String scoringMode;
	private Hole19Course course;
	private Hole19Tee tee;
	private BigDecimal handicap;
	private Long playingHandicap;
	private Long distanceWalked;
	private Long steps;
	private List<Hole19Score> scores;

	private List<Hole> holes = null;

	public boolean isNineHoleRound() {
		return holes().isNineHoleRound();
	}

	public String getCourse() {
		return course.getName();
	}

	public String getTee() {
		return tee.getName();
	}

	private DateTimeFormatter dateTimeFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s z");
	}

	public LocalDateTime getStartedAt() {
		return ZonedDateTime.parse(this.startedAt, dateTimeFormatter()).withZoneSameInstant(EST_TIMEZONE).toLocalDateTime();
	}

	public LocalDateTime getEndedAt() {
		return ZonedDateTime.parse(this.endedAt, dateTimeFormatter()).withZoneSameInstant(EST_TIMEZONE).toLocalDateTime();
	}

	public List<Hole> getHoles() {
		if (this.holes == null) {
			this.holes = scores.stream().map(Hole19Score::convert).sorted(Comparator.comparing(Hole::getNumber)).toList();
		}
		return this.holes;
	}

	private HoleStream holes() {
		return Hole.stream(getHoles());
	}

	public int getStrokes() {
		return Hole.stream(getHoles()).totalStrokes();
	}
}
