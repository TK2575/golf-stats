package dev.tk2575.golfstats.golfround.holebyhole;

import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.golfround.IncompleteRound;
import dev.tk2575.golfstats.handicapindex.HandicapIndex;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.NONE)
@Getter
public class HoleByHoleRoundCSVParser {

	private static final Logger log = LoggerFactory.getLogger(HoleByHoleRoundCSVParser.class);
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
		parseRoundDetails(this.roundFile);
		parseHolesDetails(this.holesFile);
		return GolfRound.compile(this.roundDetails, this.holes);
	}

	private void parseRoundDetails(File roundFile) {
		try (BufferedReader br = new BufferedReader(new FileReader(roundFile))) {
			String line;
			String sep = ",";
			boolean headersVerified = false;
			this.roundDetails = new HashMap<>();

			while ((line = br.readLine()) != null) {
				if (!headersVerified) {
					headersVerified = verifyHeaders(EXPECTED_HEADERS_ROUND, line);
				}
				else {
					String[] row = line.split(sep);
					this.roundDetails.put(Integer.valueOf(row[0]), new IncompleteRound(row, DATE_FORMAT, DURATION_FORMAT, index));
				}
			}
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static boolean verifyHeaders(String expectedHeaders, String line) {
		if (!line.equalsIgnoreCase(expectedHeaders)) {
			log.error(String.format("found headers = %s", line));
			throw new IllegalArgumentException("incorrect headers");
		}
		return true;
	}

	private void parseHolesDetails(File holesFile) {
		try (BufferedReader br = new BufferedReader(new FileReader(holesFile))) {

			this.holes = new HashMap<>();
			boolean headersVerified = false;
			String[] row;
			String line;
			String sep = ",";
			List<Hole> holeList;
			Hole hole;
			Integer roundId;

			while ((line = br.readLine()) != null) {
				if (!headersVerified) {
					headersVerified = verifyHeaders(EXPECTED_HEADERS_HOLES, line);
				}
				else {
					row = line.split(sep);
					roundId = Integer.valueOf(row[0]);
					hole = recordHole(row);

					holeList = this.holes.getOrDefault(roundId, new ArrayList<>());
					holeList.add(hole);
					this.holes.put(roundId, holeList);
				}
			}
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
