package dev.tk2575.golfstats.golfround.holebyhole;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class SimpleHoleScore implements Hole {
	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer strokes;
	private final boolean fairwayInRegulation;
	private final Integer putts;
}
