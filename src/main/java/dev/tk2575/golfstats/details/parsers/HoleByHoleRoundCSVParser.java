package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.IncompleteRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.SimpleHoleScore;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.NONE)
@Getter
@Log4j2
public class HoleByHoleRoundCSVParser implements CSVParser {

	private static final String EXPECTED_HEADERS_ROUND = "id,golfer,date,course,tees,rating,slope,duration,transport";
	private static final String EXPECTED_HEADERS_HOLES = "id,hole,index,par,strokes,fir,putts";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private final File roundFile;
	private final File holesFile;

	private final HandicapIndex index;

	private Map<Integer, IncompleteRound> roundDetails;
	private Map<Integer, List<Hole>> holes;

	public HoleByHoleRoundCSVParser(File roundFile, File holesFile) {
		this(roundFile, holesFile, null);
	}

	public HoleByHoleRoundCSVParser(File roundFile, File holesFile, HandicapIndex index) {
		this.roundFile = roundFile;
		this.holesFile = holesFile;
		this.index = index;
	}

	public List<GolfRound> parse() {
		this.roundDetails = new HashMap<>();
		this.holes = new HashMap<>();
		parseRoundDetails(this.roundFile);
		parseHolesDetails(this.holesFile);
		return GolfRound.compile(this.roundDetails, this.holes);
	}

	private void parseRoundDetails(File roundFile) {
		try {
			List<String[]> rows = parseFile(roundFile, EXPECTED_HEADERS_ROUND);
			rows.forEach(row -> this.roundDetails.put(Integer.valueOf(row[0]), new IncompleteRound(row, DATE_FORMAT, DURATION_FORMAT, index)));
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void parseHolesDetails(File holesFile) {
		try {
			List<String[]> rows = parseFile(holesFile, EXPECTED_HEADERS_HOLES);
			this.holes = convertRowsToHoles(rows, this::recordHole);
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private Hole recordHole(String[] row) {
		return SimpleHoleScore.builder()
		                      .number(Integer.valueOf(row[1]))
		                      .index(Integer.valueOf(row[2]))
		                      .par(Integer.valueOf(row[3]))
		                      .strokes(Integer.valueOf(row[4]))
		                      .fairwayInRegulation(Boolean.parseBoolean(row[5]))
		                      .putts(Integer.valueOf(row[6]))
		                      .build();
	}

}
