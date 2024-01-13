package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("calculator")
@Log4j2
public class HandicapCalculatorApi {

	@GetMapping(value = "sample", produces = "application/json")
	public GolferHandicapDTO sample() {
		/*@formatter:off*/
		List<TeeDTO> tees = List.of(
				new TeeDTO("Blue", new BigDecimal("70.0"), new BigDecimal("124"), 70),
				new TeeDTO("White", new BigDecimal("68.1"), new BigDecimal("116"), 70)
		);

		List<GolferHandicaps> handicaps = List.of(
			new GolferHandicaps("Tom", Map.of(
					"current", new BigDecimal("13.2"),
					"trend", new BigDecimal("15.4"),
					"anti", new BigDecimal("22.3")
			)),
			new GolferHandicaps("Will", Map.of(
					"current", new BigDecimal("26.3"),
					"trend", new BigDecimal("31.7"),
					"anti", new BigDecimal("34.8")
			))
		);
		/*@formatter:on*/
		return new GolferHandicapDTO(tees, handicaps);
	}

	@GetMapping(produces = "application/json", consumes = "application/json")
	public List<HandicapCalculation> calculate() {
//	public List<HandicapCalculation> calculate(@NonNull GolferHandicapDTO dto) {
		//FIXME dto's fields are null
		/*@formatter:off*/
		List<HandicapCalculation> results = new ArrayList<>();
		GolferHandicapDTO dto = sample();
		dto.getTees().forEach(tee ->
				dto.getHandicaps().forEach(handicap ->
						handicap.getHandicapIndexes().forEach((key, value) ->
								results.add(new HandicapCalculation(tee, handicap.getName(), key, value)))));
		return results;
		/*@formatter:on*/
	}
}
