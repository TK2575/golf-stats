package dev.tk2575.golfstats.core.golfround;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

class CompositeHoleByHoleRound extends HoleByHoleRound {

	CompositeHoleByHoleRound(@NonNull GolfRound round1, @NonNull GolfRound round2) {
		super(RoundMeta.compositeOf(round1, round2), HoleStream.compositeOf(round1, round2));
	}

	private CompositeHoleByHoleRound(@NonNull RoundMeta round, @NonNull Collection<Hole> holes, BigDecimal incomingIndex) {
		super(round, holes);
		setIncomingHandicapIndex(incomingIndex);
	}

	@Override
	public GolfRound applyNetDoubleBogey(BigDecimal incomingIndex) {
		List<Hole> holesAdjusted = holes().applyNetDoubleBogey(getTee().handicapStrokes(incomingIndex)).toList();
		return new CompositeHoleByHoleRound(new RoundMeta(this), holesAdjusted, incomingIndex);
	}
}
