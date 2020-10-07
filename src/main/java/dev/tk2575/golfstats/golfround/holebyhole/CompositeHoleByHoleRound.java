package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.SimpleCompositeGolfRound;

import java.util.List;

public class CompositeHoleByHoleRound extends SimpleCompositeGolfRound implements HoleByHole {

	private List<Hole> holes;

	public HoleStream getHoles() {
		return Hole.stream(this.holes);
	}

	@Override
	public Integer getHoleCount() {
		return this.holes.size();
	}

	public CompositeHoleByHoleRound(HoleByHoleRound round1, HoleByHoleRound round2) {
		super(round1, round2);
		this.holes = HoleStream.compositeOf(round1, round2);
	}
}
