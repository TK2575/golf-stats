package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Transport;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.NONE)
@Log4j2
public class SimpleGolfRoundCSVParser implements CSVParser {

	private static final String EXPECTED_HEADERS = "date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private final File directory;

	public SimpleGolfRoundCSVParser(File directory) {
		this.directory = directory;
	}

	public Map<String, List<GolfRound>> readCsvData() {
		final List<File> csvs = getCSVFiles(directory);
		Map<String, List<GolfRound>> results = new HashMap<>();

		for (File csv : csvs) {
			try {
				List<GolfRound> rounds = new ArrayList<>();
				String golfer = parseGolferName(csv.getName());
				List<String[]> rows = parseFile(csv, EXPECTED_HEADERS);
				rows.forEach(row -> rounds.add(recordSimpleRound(golfer, row)));
				results.put(golfer, combinePrior(rounds, results.get(golfer)));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	private static List<GolfRound> combinePrior(List<GolfRound> rounds, List<GolfRound> prior) {
		if (prior != null && !prior.isEmpty()) {
			rounds.addAll(prior);
		}
		return rounds;
	}

	private GolfRound recordSimpleRound(String golferName, String[] row) {
		var golfer = Golfer.newGolfer(golferName);
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

		//TODO update index on each round
		return GolfRound.of(date, duration, golfer, course, tee, transport, score, score, fairwaysInRegulation, fairways, greensInRegulation, putts, nineHoleRound);
	}

	private static List<File> getCSVFiles(File directory) {
		final File[] fileList = directory.listFiles();
		if (fileList != null) {
			return Arrays.stream(fileList)
			             .filter(f -> f.getName().endsWith(".csv"))
			             .filter(File::isFile)
			             .collect(Collectors.toList());
		}
		else { throw new IllegalArgumentException("could not find any csv files in " + directory); }
	}

	private static String parseGolferName(String fileName) {
		String first = fileName.substring(fileName.lastIndexOf('_') + 1);
		return toTitleCase(first.substring(0, first.lastIndexOf('.')));
	}

	private static String toTitleCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		StringBuilder converted = new StringBuilder();

		boolean convertNext = true;
		for (char ch : text.toCharArray()) {
			if (Character.isSpaceChar(ch)) {
				convertNext = true;
			}
			else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			}
			else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}

		return converted.toString();
	}
}
