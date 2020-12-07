package dev.tk2575.golfstats.golfround.shotbyshot;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class YardsDistance implements Distance {

	private final Long value;

	@Override
	public String getLengthUnit() {
		return "yards";
	}
}
