package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FeetDistance implements Distance {

	private final Long value;

	@Override
	public String getLengthUnit() {
		return "feet";
	}
}
