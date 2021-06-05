package dev.tk2575.golfstats.core.golfround;

import java.util.Collection;

class CompositeHoleByHoleRound extends SimpleCompositeGolfRound {

	private final Collection<Hole> holes;

	@Override
	public HoleStream getHoles() {
		return Hole.stream(this.holes);
	}

	@Override
	public Integer getHoleCount() {
		return this.holes.size();
	}

	CompositeHoleByHoleRound(GolfRound round1, GolfRound round2) {
		super(round1, round2);
		this.holes = HoleStream.compositeOf(round1, round2);
	}
}
