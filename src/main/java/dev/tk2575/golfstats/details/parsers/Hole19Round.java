package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class Hole19Round {
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
		return LocalDateTime.parse(this.startedAt, dateTimeFormatter());
	}

	public LocalDateTime getEndedAt() {
		return LocalDateTime.parse(this.endedAt, dateTimeFormatter());
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

	public boolean sameAs(GolfRound simpleRound) {
		if (!getStartedAt().toLocalDate().equals(simpleRound.getDate())) {
			return false;
		}

		if (!simpleRound.getGolfer().getName().equalsIgnoreCase("Tom")) {
			return false;
		}

		if (!simpleRound.getCourse().getName().split("\\s+")[0].equalsIgnoreCase(getCourse().split("\\s+")[0])) {
			return false;
		}

		return true;
	}
}
