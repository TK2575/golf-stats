package dev.tk2575.golfstats.parsers;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.IncompleteRound;
import dev.tk2575.golfstats.golfround.holebyhole.Hole;
import dev.tk2575.golfstats.golfround.holebyhole.ShotByShotHole;
import dev.tk2575.golfstats.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.NONE)
@Getter
public class ShotByShotRoundCSVParser implements CSVParser {

	private static final Logger log = LoggerFactory.getLogger(ShotByShotRoundCSVParser.class);
	private static final String EXPECTED_HEADERS_ROUND = "id,golfer,date,course,city,state,tees,rating,slope,start,end,transport";
	private static final String EXPECTED_HEADERS_HOLES = "id,hole,index,par,shots";

	private static final List<DateTimeFormatter> timeFormats = List.of(DateTimeFormatter.ofPattern("h:m a"), DateTimeFormatter.ofPattern("k:m"));
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

	private final File roundFile;
	private final File holesFile;
	private final HandicapIndex index;

	private Map<Integer, IncompleteRound> roundDetails;
	private Map<Integer, List<Hole>> holes;

	public ShotByShotRoundCSVParser(File roundFile, File holesFile) { this(roundFile, holesFile, null); }

	public ShotByShotRoundCSVParser(File roundFile, File holesFile, HandicapIndex index) {
		this.roundFile = roundFile;
		this.holesFile = holesFile;
		this.index = index;
	}

	List<GolfRound> parse() {
		this.roundDetails = new HashMap<>();
		this.holes = new HashMap<>();
		parseRoundDetails(this.roundFile);
		parseHolesDetails(this.holesFile);
		return GolfRound.compile(this.roundDetails, this.holes);
	}

	private void parseRoundDetails(File roundFile) {
		try {
			List<String[]> rows = parseFile(roundFile, EXPECTED_HEADERS_ROUND);
			rows.forEach(row -> this.roundDetails.put(Integer.valueOf(row[0]), new IncompleteRound(row, DATE_FORMAT, timeFormats, index)));
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
		return ShotByShotHole.builder()
		                     .number(Integer.valueOf(row[1]))
		                     .index(Integer.valueOf(row[2]))
		                     .par(Integer.valueOf(row[3]))
		                     .shots(parseShots(row[4]))
		                     .build();
	}

	private List<Shot> parseShots(String shotsString) {
		return Arrays.stream(shotsString.split("\\.")).sequential().map(Shot::parse).collect(Collectors.toList());
	}
}
