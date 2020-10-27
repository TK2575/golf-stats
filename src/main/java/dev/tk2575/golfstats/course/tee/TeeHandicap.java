package dev.tk2575.golfstats.course.tee;

import java.util.Map;

public interface TeeHandicap extends Tee {

	Map<String, Integer> getHandicapStrokes();
}
