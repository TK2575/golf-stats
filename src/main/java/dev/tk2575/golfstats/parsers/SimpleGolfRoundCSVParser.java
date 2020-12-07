package dev.tk2575.golfstats.parsers;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.SimpleGolfRound;
import dev.tk2575.golfstats.golfround.Transport;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class SimpleGolfRoundCSVParser implements CSVParser {

	private static final Logger log = LoggerFactory.getLogger(SimpleGolfRoundCSVParser.class);
	private static final String EXPECTED_HEADERS = "date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private final File directory;

	public SimpleGolfRoundCSVParser(File directory) {
		this.directory = directory;
	}

	private LocalDate date;
	private String golferName;
	private String courseName;
	private String tees;
	private Duration duration;
	private Transport transport;
	private BigDecimal rating;
	private BigDecimal slope;
	private Integer par;
	private Integer score;
	private Integer fairwaysInRegulation;
	private Integer fairways;
	private Integer greensInRegulation;
	private Integer putts;
	private Boolean nineHoleRound;

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
		//TODO better defend against individual fields being null
		this.golferName = golferName;
		this.date = LocalDate.parse(row[0], DATE_FORMAT);
		this.courseName = row[1];
		this.tees = row[2];
		this.rating = new BigDecimal(row[3]);
		this.slope = new BigDecimal(row[4]);
		this.par = Integer.valueOf(row[5]);
		this.duration = row[6] == null || row[6].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[6], DURATION_FORMAT));
		this.transport = Transport.valueOf(row[7]);
		this.score = Integer.valueOf(row[8]);
		this.fairwaysInRegulation = Integer.valueOf(row[9]);
		this.fairways = Integer.valueOf(row[10]);
		this.greensInRegulation = Integer.valueOf(row[11]);
		this.putts = Integer.valueOf(row[12]);
		this.nineHoleRound = Boolean.parseBoolean(row[13]);

		//TODO update index on each round
		return new SimpleGolfRound(this);
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
