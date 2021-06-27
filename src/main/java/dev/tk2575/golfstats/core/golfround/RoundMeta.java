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

	/*public RoundMeta(String[] row, DateTimeFormatter dateFormat, List<DateTimeFormatter> timeFormats, HandicapIndex index) {
		this.golfer = Golfer.of(row[1], index);
		this.date = LocalDate.parse(row[2], dateFormat);
		this.course = Course.of(row[3], row[4], row[5]);
		this.teeName = row[6];
		this.rating = new BigDecimal(row[7]);
		this.slope = new BigDecimal(row[8]);
		this.duration = Duration.between(Utils.parseTime(timeFormats, row[9]), Utils.parseTime(timeFormats, row[10]));
		this.transport = Transport.valueOf(row[11]);
	}*/
}
