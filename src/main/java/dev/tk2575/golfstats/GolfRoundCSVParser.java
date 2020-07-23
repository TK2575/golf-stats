package dev.tk2575.golfstats;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.GolfRoundFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GolfRoundCSVParser {

	private static final Logger log = LoggerFactory.getLogger(GolfRoundCSVParser.class);
	private static final String EXPECTED_HEADERS = "date,course,tees,rating,slope,par,duration,transport,score,fairways_hit,fairways,greens_in_reg,putts,nine_hole_round";

	static Map<String, List<GolfRound>> readCsvData(File directory) {
		final List<File> csvs = getCSVFiles(directory);

		Map<String, List<GolfRound>> results = new HashMap<>();
		List<GolfRound> rounds;
		String golfer;
		String line;
		String sep = ",";

		for (File csv : csvs) {
			rounds = new ArrayList<>();
			boolean headersVerified = false;
			golfer = parseGolferName(csv.getName());

			try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
				while ((line = br.readLine()) != null) {
					if (!headersVerified) {
						headersVerified = verifyHeaders(line);
					}
					else {
						rounds.add(new GolfRoundFactory().recordRound(golfer, line.split(sep)));
					}
				}
				results.put(golfer, combinePrior(rounds, results.get(golfer)));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	private static boolean verifyHeaders(String line) {
		if (!line.equalsIgnoreCase(EXPECTED_HEADERS)) {
			log.error(String.format("found headers = %s", line));
			throw new IllegalArgumentException("incorrect headers");
		}
		return true;
	}

	private static List<GolfRound> combinePrior(List<GolfRound> rounds, List<GolfRound> prior) {
		if (prior != null && !prior.isEmpty()) {
			rounds.addAll(prior);
		}
		return rounds;
	}

	private static List<File> getCSVFiles(File directory) {
		final File[] fileList = directory.listFiles();
		if (fileList != null) {
			return Arrays.stream(fileList).filter(f -> f.getName().endsWith(".csv")).filter(File::isFile).collect(Collectors.toList());
		}
		else throw new IllegalArgumentException("could not find any csv files in " + directory);
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
