package dev.tk2575.golfstats.core.course.tee;

import dev.tk2575.Utils;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
class CompositeTee implements Tee {

	private final String name;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final Integer par;
	private final Integer holeCount;

	CompositeTee(Tee tee1, Tee tee2) {
		this.name = Utils.joinByHyphenIfUnequal(tee1.getName(), tee2.getName());
		this.rating = tee1.getRating().add(tee2.getRating());
		this.slope = Utils.mean(tee1.getSlope(), tee2.getSlope());
		this.par = tee1.getPar() + tee2.getPar();
		this.holeCount = tee1.getHoleCount() + tee2.getHoleCount();
	}


}
