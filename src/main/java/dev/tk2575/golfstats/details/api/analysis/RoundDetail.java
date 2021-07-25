package dev.tk2575.golfstats.details.api.analysis;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class RoundDetail extends RoundSummary {

	private final List<HoleAnalysis> holes;
	private final List<ShotAnalysis> shots;

	public RoundDetail(int number, GolfRound round) {
		super(number, round);
		this.holes = round.getHoles().map(HoleAnalysis::new).collect(toList());
		this.shots = round.getHoles().allShots().map(ShotAnalysis::new).collect(toList());
	}
}
