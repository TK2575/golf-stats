package dev.tk2575.golfstats.details.imports;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.function.BiConsumer;

@Log4j2
abstract class CSVParser {

	abstract List<CSVFile> getFiles();

	abstract List<GolfRound> parse();

	protected void parse(CSVFile file, BiConsumer<Integer, String[]> parser) {
		int line = 1;
		for (String[] row : file.getRowsOfDelimitedValues()) {
			line++;
			try {
				parser.accept(Integer.parseInt(row[0]), row);
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
}
