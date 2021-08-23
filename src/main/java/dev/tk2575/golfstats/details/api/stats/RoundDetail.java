package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.util.List;

@Getter
public class RoundDetail extends RoundSummary {

	private final List<HoleAnalysis> holes;
	private final List<ShotAnalysis> shots;

	public RoundDetail(int number, GolfRound round) {
		super(number, round);
		this.holes = round.getHoles().map(HoleAnalysis::new).toList();
		this.shots = round.getHoles().allShots().map(ShotAnalysis::new).toList();
	}
}
