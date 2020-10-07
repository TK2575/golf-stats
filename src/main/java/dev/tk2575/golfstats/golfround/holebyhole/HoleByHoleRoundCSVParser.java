package dev.tk2575.golfstats.golfround.holebyhole;

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
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.NONE)
@Getter
public class HoleByHoleRoundCSVParser {

	private static final Logger log = LoggerFactory.getLogger(HoleByHoleRoundCSVParser.class);
	private static final String EXPECTED_HEADERS_ROUND = "golfer,date,course,tees,rating,slope,par,duration,transport";
	private static final String EXPECTED_HEADERS_HOLES = "hole,index,par,strokes,fir,gir,putts";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

	private final File roundFile;
	private final File holesFile;

	private String golfer;
	private LocalDate date;
	private String course;
	private String tees;
	private BigDecimal rating;
	private BigDecimal slope;
	private Integer par;
	private Duration duration;
	private String transport;

	private List<Hole> holes;

	public HoleByHoleRoundCSVParser(File roundFile, File holesFile) {
		this.roundFile = roundFile;
		this.holesFile = holesFile;
	}

	public HoleByHoleRound parse() {
		parseRoundDetails(this.roundFile);
		parseHolesDetails(this.holesFile);
		return new HoleByHoleRound(this);
	}

	private void parseRoundDetails(File roundFile) {
		try (BufferedReader br = new BufferedReader(new FileReader(roundFile))) {
			String line;
			String sep = ",";
			boolean headersVerified = false;
			boolean roundDetails = false;

			while ((line = br.readLine()) != null) {
				if (!headersVerified) {
					headersVerified = verifyHeaders(EXPECTED_HEADERS_ROUND, line);
				}
				else if (!roundDetails) {
					roundDetails = recordRoundDetails(line.split(sep));
				}
				else {
					log.warn(String.format("Found multiple rounds' details in %s, skipping all but the first", roundFile
							.getName()));
					break;
				}
			}
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean recordRoundDetails(String[] row) {
		//TODO better defend against individual fields being null/empty
		this.golfer = row[0];
		this.date = LocalDate.parse(row[1], DATE_FORMAT);
		this.course = row[2];
		this.tees = row[3];
		this.rating = new BigDecimal(row[4]);
		this.slope = new BigDecimal(row[5]);
		this.par = Integer.valueOf(row[6]);
		this.duration = row[7] == null || row[7].isBlank()
		                ? Duration.ZERO
		                : Duration.between(LocalTime.MIN, LocalTime.parse(row[7], DURATION_FORMAT));
		this.transport = row[8];
		return true;
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
			String line;
			boolean headersVerified = false;
			String sep = ",";
			List<Hole> holeList = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				if (!headersVerified) {
					headersVerified = verifyHeaders(EXPECTED_HEADERS_HOLES, line);
				}
				else {
					holeList.add(recordHole(line.split(sep)));
				}
			}
			this.holes = holeList;
		}
		catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private Hole recordHole(String[] row) {
		Integer number = Integer.valueOf(row[0]);
		Integer index = Integer.valueOf(row[1]);
		Integer parHole = Integer.valueOf(row[2]);
		Integer score = Integer.valueOf(row[3]);
		boolean fairwayPresent = parHole > 4;
		boolean fairwayInRegulation = Boolean.parseBoolean(row[4]);
		boolean greenInRegulation = Boolean.parseBoolean(row[5]);
		Integer putts = Integer.valueOf(row[6]);

		return new SimpleHoleScore(number, index, parHole, score, fairwayPresent, fairwayInRegulation, greenInRegulation, putts);
	}

}
