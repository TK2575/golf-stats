package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
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

	private DateTimeFormatter dateTimeFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s z");
	}

	public LocalDateTime getStartedAt() {
		return LocalDateTime.parse(this.startedAt, dateTimeFormatter());
	}

	public LocalDateTime getEndedAt() {
		return LocalDateTime.parse(this.endedAt, dateTimeFormatter());
	}

	public GolfRound convert() {
		//TODO need rating, slope, transport, and course location

		//RoundMeta
		RoundMeta meta = new RoundMeta();

		//List<Hole>
		List<Hole> holes = scores.stream().map(Hole19Score::convert).toList();

		return GolfRound.of(meta, holes);
	}
}
