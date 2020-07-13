package dev.tk2575.golfstats;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class HandicapIndex {
	//TODO merge analysis class, constructor from list of round data

	private List<GolfRoundAnalysis> data;
	private String golfer;
	private BigDecimal handicapIndex;

}
