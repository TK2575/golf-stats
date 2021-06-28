package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.CSVFile;

import java.util.List;

public interface CSVParser {

	List<CSVFile> getFiles();

	List<GolfRound> parse();

	default boolean verifyHeaders(String expectedHeaders, String line) {
		return line.equalsIgnoreCase(expectedHeaders);
	}
}
