package dev.tk2575.golfstats.golfround.shotbyshot;

public interface ShotCategory {

	static ShotCategory unknown() {
		return new UnknownShotCategory();
	}

	static ShotCategory compute(Shot simpleShot) {
		//TODO
		return new UnknownShotCategory();
	}

	String getLabel();

}
