package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.Transport;
import dev.tk2575.golfstats.details.CSVFile;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

@Getter
@Log4j2
public class HoleByHoleRoundCSVParser implements CSVParser {

    private static final String EXPECTED_HEADERS_ROUND = "id,golfer,date,course,tees,rating,slope,duration,transport";
    private static final String EXPECTED_HEADERS_HOLES = "id,hole,index,par,strokes,fir,putts";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");
    private static final DateTimeFormatter DURATION_FORMAT = DateTimeFormatter.ofPattern("H:m");

    private CSVFile roundFile = null;
    private CSVFile holesFile = null;

	private final Map<Integer, RoundMeta> roundMetas = new HashMap<>();
	private final Map<String, Golfer> golfers = new HashMap<>();
	private final Map<Integer, List<Hole>> holes = new HashMap<>();

    public HoleByHoleRoundCSVParser(@NonNull List<CSVFile> files) {
    	if (files.size() != 2) {
		    throw new IllegalArgumentException("Expecting exactly two csv files, one round detail file, and one hole result file");
	    }

	    for (CSVFile file : files) {
		    if (verifyHeaders(EXPECTED_HEADERS_ROUND, file.getHeader())) {
		    	this.roundFile = file;
		    }
		    else if (verifyHeaders(EXPECTED_HEADERS_HOLES, file.getHeader())) {
		    	this.holesFile = file;
		    }
		    else {
		    	throw new IllegalArgumentException(String.format("Problem parsing file %s. Found headers = %s", file.getName(), file.getHeader()))
		    }
	    }

	    if (this.roundFile == null) {
		    throw new IllegalArgumentException("Did not detect a round detail file");
	    }

	    if (this.holesFile == null) {
		    throw new IllegalArgumentException("Did not detect a hole result file");
	    }
    }



	@Override
    public List<GolfRound> parse() {
        parseRoundDetails();
        parseHolesDetails();
        return GolfRound.compile(this.roundMetas, this.holes);
    }

    private void parseRoundDetails() {
	    int line = 1;
	    int id;
	    RoundMeta meta;

	    for (String row : this.roundFile.getBody().split("\n")) {
		    line++;
		    String[] cells = row.split(",");
		    try {
			    id = Integer.parseInt(cells[0]);
			    meta = recordRoundMeta(cells);
			    this.roundMetas.put(id, meta);
		    }
		    catch (Exception e) {
			    log.error(
					    String.format("Encountered parse error on line %s in file %s. Skipping row",
							    line,
							    this.roundFile.getName())
			    );
			    e.printStackTrace();
		    }
	    }
    }

	private RoundMeta recordRoundMeta(String[] cells) {
		var date = LocalDate.parse(cells[2], DATE_FORMAT);
		var course = Course.of(cells[3], cells[4], cells[5]);
		var teeName = cells[6];
		var rating = new BigDecimal(cells[7]);
		var slope = new BigDecimal(cells[8]);
		LocalTime from = LocalTime.parse(cells[9], DURATION_FORMAT);
		LocalTime thru = LocalTime.parse(cells[10], DURATION_FORMAT);
		var duration = Duration.between(from, thru);
		var transport = Transport.valueOf(cells[11]);

		var golferString = cells[1];
		var golfer = this.golfers.computeIfAbsent(golferString, Golfer::newGolfer);

		return new RoundMeta(date, duration, golfer, course, rating, slope, teeName, transport);
	}

	//TODO refactor to avoid duplicated code
	private void parseHolesDetails() {
		int line = 1;
		int id;
		Hole hole;

		for (String row : this.holesFile.getBody().split("\n")) {
			line++;
			String[] cells = row.split(",");
			try {
				id = Integer.parseInt(cells[0]);
				hole = recordHole(cells);
				this.holes.merge(id, List.of(hole), (prior, current) -> {
					List<Hole> result = new ArrayList<>(prior);
					result.addAll(current);
					return result;
				});
			}
			catch (Exception e) {
				log.error(
						String.format("Encountered parse error on line %s in file %s. Skipping row",
								line,
								this.holesFile.getName())
				);
				e.printStackTrace();
			}
		}
    }

    private Hole recordHole(String[] row) {
        Integer number = Integer.valueOf(row[1]);
        Integer index = Integer.valueOf(row[2]);
        Integer par = Integer.valueOf(row[3]);
        Integer strokes = Integer.valueOf(row[4]);
        boolean fairwayInRegulation = Boolean.parseBoolean(row[5]);
        Integer putts = Integer.valueOf(row[6]);
        return Hole.of(number, index, par, strokes, fairwayInRegulation, putts);
    }

}
