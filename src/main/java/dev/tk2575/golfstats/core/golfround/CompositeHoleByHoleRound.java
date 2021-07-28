package dev.tk2575.golfstats.core.golfround;

import lombok.*;

class CompositeHoleByHoleRound extends HoleByHoleRound {

	CompositeHoleByHoleRound(@NonNull GolfRound round1, @NonNull GolfRound round2) {
		super(RoundMeta.compositeOf(round1, round2), HoleStream.compositeOf(round1, round2));
	}
}
