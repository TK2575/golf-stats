package dev.tk2575.golfstats.details.api.stats;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RoundDetail extends RoundSummary {

	private final List<HoleAnalysis> holes = new ArrayList<>();
	private final List<ShotAnalysis> shots = new ArrayList<>();

	public RoundDetail(int number, GolfRound round, BigDecimal resultantHandicapIndex) {
		super(number, round, resultantHandicapIndex);
		for (Hole hole : round.getHoles().toList()) {
			this.holes.add(new HoleAnalysis(hole));
			for (Shot shot : hole.getShots().toList()) {
				this.shots.add(new ShotAnalysis(hole.getNumber(), shot));
			}
		}
	}
}
