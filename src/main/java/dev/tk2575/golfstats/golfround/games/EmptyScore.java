package dev.tk2575.golfstats.golfround.games;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.holebyhole.Hole;

public class EmptyScore extends Game {

	public EmptyScore(GolfRound round) {
		super(round);
	}

	@Override
	public boolean isScoreComputed() {
		return false;
	}

	@Override
	public int score(Hole value) {
		return 0;
	}
}
