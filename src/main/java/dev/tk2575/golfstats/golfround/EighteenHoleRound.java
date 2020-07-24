package dev.tk2575.golfstats.golfround;

import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
public class EighteenHoleRound implements GolfRound {

	@Getter(AccessLevel.NONE) private static final Integer HOLES = 18;

	public Integer getHoles() {
		return HOLES;
	}

	private LocalDate date;
	private Duration duration;

	private String golfer;
	private String course;
	private String tees;
	private String transport;

	private BigDecimal rating; //put whatever's on the scorecard, adjust if it's way off of par
	private BigDecimal slope;
	private BigDecimal scoreDifferential;

	private Integer par; //expect par of holes played to be inputted (i.e. put 36 if playing a nine hole round)
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;


	public EighteenHoleRound(GolfRoundFactory factory) {
		this.date = factory.getDate();
		this.golfer = factory.getGolfer();
		this.course = factory.getCourse();
		this.tees = factory.getTees();
		this.duration = factory.getDuration();
		this.transport = factory.getTransport();
		this.rating = factory.getRating();
		this.slope = factory.getSlope();
		this.par = factory.getPar();
		this.score = factory.getScore();
		this.fairwaysInRegulation = factory.getFairwaysInRegulation();
		this.fairways = factory.getFairways();
		this.greensInRegulation = factory.getGreensInRegulation();
		this.putts = factory.getPutts();
		this.scoreDifferential = computeScoreDifferential();
	}
}
