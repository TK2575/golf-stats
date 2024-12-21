package dev.tk2575.golfstats.details.notion;

import lombok.*;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotionGolfRound {
	private Optional<BigDecimal> slope;
	private Optional<String> validationFailure;
	private Optional<BigDecimal> differential;
	private Optional<Integer> score;
	private Optional<BigDecimal> rating;
	private Optional<BigDecimal> tot;
	private Optional<BigDecimal> putt;
	private boolean triggerValidation;
	private Optional<String> shotsShorthand;
	private Optional<Long> yards;
	private Optional<String> city;
	private Optional<String> tees;
	private Optional<String> state;
	private Optional<BigDecimal> app;
	private Optional<ZonedDateTime> start;
	private Optional<ZonedDateTime> end;
	private Optional<Integer> holesPlayed;
	private Optional<BigDecimal> handicapIndex;
	private Optional<BigDecimal> ott;
	private Optional<Integer> poorShots;
	private Optional<String> course;
	private Optional<String> transport;
	private String id;
	private Optional<BigDecimal> p75Drive;
	private Optional<BigDecimal> rcvr;
	private Optional<Integer> strokes;
	private Optional<BigDecimal> arg;
	private Optional<Integer> greatShots;
	private Optional<String> name;

	public NotionGolfRound(Page page) {
		var props = page.getProperties();

		this.slope = Optional.ofNullable(props.get("Slope"))
				.map(
						property -> property.getNumber() == null
								? 0f // Default to 0 if null
								: property.getNumber().floatValue()
				)
				.filter(f -> f != 0f)
				.map(BigDecimal::valueOf);

		this.validationFailure = getValidationFailure(props);
	}

	private Optional<String> getValidationFailure(Map<String, PageProperty> props) {
		PageProperty vf = props.get("Validation Failure");
		if (vf != null) {
			var rt = vf.getRichText();
			if (rt != null) {
				var first = rt.getFirst();
				if (first != null) {
					var text = first.getText();
					if (text != null) {
						var content = text.getContent();
						if (content != null) {
							return Optional.of(content);
						}
					}
				}
			}
		}
		return Optional.empty();
	}


}
