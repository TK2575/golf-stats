package dev.tk2575.golfstats.details.api.handicapcalculator;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("calculator")
@Log4j2
public class HandicapCalculatorApi {

	//TODO take example from main
	// accept multiple tees and golfers
	// return some HandicapCalculation DTO of handicap strokes and stableford quota
	// consider various indexes like trend, anti, and latest
	// probably need to move handicap calculation out of Tee object
}
