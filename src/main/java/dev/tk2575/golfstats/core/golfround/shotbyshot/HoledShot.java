package dev.tk2575.golfstats.core.golfround.shotbyshot;

public class HoledShot implements Shot {

	@Override
	public Lie getLie() {
		return Lie.hole();
	}

	//FIXME this feels wrong, only used by strokes gained,
	// might be able to do that calculation and categorization earlier
	@Override
	public Distance getDistanceFromTarget() {
		return Distance.zero();
	}

	@Override
	public MissAngle getMissAngle() {
		return MissAngle.center();
	}

	@Override
	public Distance getMissDistance() { return Distance.zero(); }

	@Override
	public ShotCategory getShotCategory() { return ShotCategory.unknown(); }

	@Override
	public Integer getCount() {
		return 0;
	}
}
