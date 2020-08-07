package dev.tk2575.golfstats.golfround;

import dev.tk2575.golfstats.Utils;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class MultiRoundSummary implements GolfRound {

	private LocalDate date;
	private Duration duration;

	private String golfer;
	private String course;
	private String tees;
	private String transport;
	private BigDecimal rating;
	private BigDecimal slope;
	private BigDecimal scoreDifferential;

	private Integer par;
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;
	private Integer holes;

	@ToString.Exclude private List<GolfRound> rounds;


	public MultiRoundSummary(List<GolfRound> rounds) {
		if (rounds == null) {
			throw new IllegalArgumentException("rounds is a required argument");
		}

		this.rounds = rounds.stream().filter(Objects::nonNull).collect(Collectors.toList());

		if (this.rounds.isEmpty()) {
			throw new IllegalArgumentException("need at least one round to build summary");
		}

		this.rounds.sort(Comparator.comparing(GolfRound::getDate).reversed());

		this.date = this.rounds.stream().max(Comparator.comparing(GolfRound::getDate)).get().getDate();
		this.golfer = getUniqueValues(GolfRound::getGolfer);
		this.course = getUniqueValues(GolfRound::getCourse);
		this.tees = getUniqueValues(GolfRound::getTees);
		this.transport = getUniqueValues(GolfRound::getTransport);

		this.par = sumIntegerValues(GolfRound::getPar);
		this.score = sumIntegerValues(GolfRound::getScore);
		this.fairwaysInRegulation = sumIntegerValues(GolfRound::getFairwaysInRegulation);
		this.fairways = sumIntegerValues(GolfRound::getFairways);
		this.greensInRegulation = sumIntegerValues(GolfRound::getGreensInRegulation);
		this.putts = sumIntegerValues(GolfRound::getPutts);
		this.holes = sumIntegerValues(GolfRound::getHoles);

		this.rating = Utils.median(this.rounds.stream().map(GolfRound::getRating).collect(Collectors.toList()));
		this.slope = Utils.median(this.rounds.stream().map(GolfRound::getSlope).collect(Collectors.toList()));

		this.duration = this.rounds.stream().map(GolfRound::getDuration).reduce(Duration::plus).orElse(Duration.ZERO);

		this.scoreDifferential = computeScoreDifferential();
	}

	private String getUniqueValues(Function<? super GolfRound, ? extends String> mapper) {
		return this.rounds.stream().map(mapper).distinct().collect(Collectors.joining(", "));
	}

	private Integer sumIntegerValues(Function<? super GolfRound, Integer> mapper) {
		return this.rounds.stream().map(mapper).reduce(Integer::sum).orElse(0);
	}

}
