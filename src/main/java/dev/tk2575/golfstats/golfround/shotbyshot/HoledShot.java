package dev.tk2575.golfstats.golfround.shotbyshot;

public class HoledShot implements Shot {

	@Override
	public Lie getLie() {
		return Lie.hole();
	}

	@Override
	public Distance getDistance() {
		return Distance.zero();
	}

	@Override
	public MissAngle getMissAngle() {
		return MissAngle.center();
	}

	@Override
	public ShotCategory getShotCategory() { return ShotCategory.unknown(); }

	@Override
	public Integer getCount() {
		return 0;
	}
}
