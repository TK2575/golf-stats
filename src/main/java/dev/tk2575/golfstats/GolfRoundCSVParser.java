package dev.tk2575.golfstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GolfRoundCSVParser {

	private static final Logger log = LoggerFactory.getLogger(GolfRoundCSVParser.class);

	private static final String EXPECTED_HEADERS = "date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round";

	static List<GolfRound> readCsvData(File directory) {
		final List<File> csvs = getCSVFiles(directory);

		List<GolfRound> rounds = new ArrayList<>();
		String golfer;
		String line;
		String sep = ",";
		GolfRound round;

		for (File csv : csvs) {
			boolean headersVerified = false;
			golfer = parseGolferName(csv.getName());

			try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
				while ((line = br.readLine()) != null) {
					if (!headersVerified) {
						if (!line.equalsIgnoreCase(EXPECTED_HEADERS)) {
							log.error("found headers = " + line);
							throw new IllegalArgumentException("incorrect headers");
						}
						headersVerified = true;
					}
					else {
						round = new GolfRound(golfer, line.split(sep));
						log.info(round.toString());
						rounds.add(round);
					}
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rounds;
	}

	private static List<File> getCSVFiles(File directory) {
		return Arrays.stream(directory.listFiles()).filter(f -> f.getName().endsWith(".csv")).filter(File::isFile).collect(Collectors.toList());
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
			} else if (convertNext) {
				ch = Character.toTitleCase(ch);
				convertNext = false;
			} else {
				ch = Character.toLowerCase(ch);
			}
			converted.append(ch);
		}

		return converted.toString();
	}
}
