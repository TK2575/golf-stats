package dev.tk2575.golfstats.core.golfround;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.golfer.Golfer;
import lombok.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class RoundMeta {

	private final LocalDate date;
	private final Duration duration;

	private final Golfer golfer;
	private final Course course;
	private final BigDecimal rating;
	private final BigDecimal slope;
	private final String teeName;
	private final Transport transport;
}
