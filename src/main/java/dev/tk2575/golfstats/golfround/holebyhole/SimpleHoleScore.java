package dev.tk2575.golfstats.golfround.holebyhole;

import lombok.*;

@Getter
@AllArgsConstructor
public class SimpleHoleScore implements Hole {
	private final Integer number;
	private final Integer index;
	private final Integer par;
	private final Integer score;
	private final boolean fairwayPresent;
	private final boolean fairwayInRegulation;
	private final boolean greenInRegulation;
	private final Integer putts;
}
