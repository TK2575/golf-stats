package dev.tk2575.golfstats.core.golfround.games;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import lombok.*;

import java.math.BigDecimal;
import java.util.function.ToIntFunction;

@Getter
public class StablefordAllPositive extends Game {

	private final Integer score;

	protected StablefordAllPositive(GolfRound round) {
		super(round);
		score = getRound().getHoles().totalScore(this::score);
	}

	static final ToIntFunction<Hole> rules = h -> {
		int diff = h.getPar() - h.getStrokes() + 1;
		if (diff < 0) { return 0; }
		if (diff == 0) { return 1; }
		return (int) Math.pow(2, diff);
	};

	@Override
	public int score(Hole h) { return rules.applyAsInt(h); }
}
