package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.SimpleCompositeGolfRound;

import java.util.Collection;

public class CompositeHoleByHoleRound extends SimpleCompositeGolfRound {

	private final Collection<Hole> holes;

	@Override
	public HoleStream getHoles() {
		return Hole.stream(this.holes);
	}

	@Override
	public Integer getHoleCount() {
		return this.holes.size();
	}

	public CompositeHoleByHoleRound(GolfRound round1, GolfRound round2) {
		super(round1, round2);
		this.holes = HoleStream.compositeOf(round1, round2);
	}
}
