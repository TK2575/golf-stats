package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.Hole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface CSVParser {

	default List<String[]> parseFile(File file, String headers) throws IOException {
		List<String[]> results = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			String sep = ",";
			boolean headersVerified = false;

			while ((line = br.readLine()) != null) {
				if (!headersVerified) {
					headersVerified = verifyHeaders(headers, line);
				}
				else {
					results.add(line.split(sep));
				}
			}
		}
		return results;
	}

	static boolean verifyHeaders(String expectedHeaders, String line) {
		if (!line.equalsIgnoreCase(expectedHeaders)) {
			throw new IllegalArgumentException(String.format("found headers = %s; expected headers = %s", line, expectedHeaders));
		}
		return true;
	}

	default Map<Integer, List<Hole>> convertRowsToHoles(List<String[]> rows, Function<String[], Hole> conversionFunction) {
		Map<Integer, List<Hole>> results = new HashMap<>();
		rows.forEach(row -> {
			Integer roundId = Integer.valueOf(row[0]);
			Hole hole = conversionFunction.apply(row);

			List<Hole> holeList = results.getOrDefault(roundId, new ArrayList<>());
			holeList.add(hole);
			results.put(roundId, holeList);
		});
		return results;
	}
}
