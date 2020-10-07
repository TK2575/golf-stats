package dev.tk2575.golfstats.golfround.holebyhole;

import java.util.List;

public interface Hole {

	Integer getNumber();

	Integer getIndex();

	Integer getPar();

	Integer getScore();

	boolean isFairwayPresent();

	boolean isFairwayInRegulation();

	boolean isGreenInRegulation();

	Integer getPutts();

	static HoleStream stream(List<Hole> holes) {
		return new HoleStream(holes.stream());
	}
}
