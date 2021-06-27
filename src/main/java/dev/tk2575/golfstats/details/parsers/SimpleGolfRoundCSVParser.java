package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Transport;
import dev.tk2575.golfstats.details.CSVFile;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Log4j2
public class SimpleGolfRoundCSVParser implements CSVParser {

	private static final String EXPECTED_HEADERS = "date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round,golfer";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private final List<CSVFile> files;

	@Override
	public List<GolfRound> parse() {
		List<GolfRound> results = new ArrayList<>();
		Golfer golfer;
		GolfRound round;
		int line;

		for (CSVFile file : files) {
			golfer = null;
			if (!verifyHeaders(EXPECTED_HEADERS, file.getHeader())) {
				log.error(
						String.format("Problem parsing file %s. Found headers = %s; expected headers = %s",
								file.getName(),
								file.getHeader(),
								EXPECTED_HEADERS)
				);
				continue;
			}

			line = 1;
			for (String row : file.getBody().split("\n")) {
				line++;
				try {
					round = recordSimpleRound(golfer, row.split(","));
					if (golfer == null) {
						golfer = round.getGolfer();
					}
					results.add(round);
				}
				catch (Exception e) {
					log.error(
							String.format("Encountered parse error on line %s in file %s. Skipping row",
									line,
									file.getName())
					);
					e.printStackTrace();
				}
			}
		}

		return results;
	}

	private GolfRound recordSimpleRound(final Golfer golfer, String[] row) {
		var date = LocalDate.parse(row[0], DATE_FORMAT);
		var course = Course.of(row[1]);

		var teeName = row[2];
		var rating = new BigDecimal(row[3]);
		var slope = new BigDecimal(row[4]);
		var par = Integer.valueOf(row[5]);
		var tee = Tee.of(teeName, rating, slope, par);

		var duration = row[6] == null || row[6].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));

		var transport = Transport.valueOf(row[7]);
		var score = Integer.valueOf(row[8]);
		var fairwaysInRegulation = Integer.valueOf(row[9]);
		var fairways = Integer.valueOf(row[10]);
		var greensInRegulation = Integer.valueOf(row[11]);
		var putts = Integer.valueOf(row[12]);
		var nineHoleRound = Boolean.parseBoolean(row[13]);
		var golferString = row[14];

		return GolfRound.of(date, duration, golfer == null ? Golfer.newGolfer(golferString) : golfer, course, tee, transport, score, score, fairwaysInRegulation, fairways, greensInRegulation, putts, nineHoleRound);
	}

}
