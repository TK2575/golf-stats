package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.core.golfround.Transport;
import dev.tk2575.golfstats.details.CSVFile;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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
		    	throw new IllegalArgumentException(String.format("Problem parsing file %s. Found headers = %s", file.getName(), file.getHeader()));
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
	public List<CSVFile> getFiles() {
		return List.of(this.roundFile, this.holesFile);
	}

	@Override
    public List<GolfRound> parse() {
		parse(this.roundFile, roundMetaParser);
		parse(this.holesFile, holeParser);
        return GolfRound.compile(this.roundMetas, this.holes);
    }

    private void parse(CSVFile file, BiConsumer<Integer, String[]> parser) {
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

	private final BiConsumer<Integer, String[]> roundMetaParser = (id, row) -> {
		var golferString = row[1];
		var golfer = this.golfers.computeIfAbsent(golferString, Golfer::newGolfer);
		var date = LocalDate.parse(row[2], DATE_FORMAT);
		var course = Course.of(row[3]);
		var teeName = row[4];
		var rating = new BigDecimal(row[5]);
		var slope = new BigDecimal(row[6]);
		var durationString = row[7];
		var duration = durationString == null || durationString.isBlank()
				? Duration.ZERO
				: Duration.between(LocalTime.MIN, LocalTime.parse(durationString, DURATION_FORMAT));
		var transport = Transport.valueOf(row[8]);

		this.roundMetas.put(id, new RoundMeta(date, duration, golfer, course, rating, slope, teeName, transport));
	};

    private final BiConsumer<Integer, String[]> holeParser = (id, row) -> {
	    var number = Integer.valueOf(row[1]);
	    var index = Integer.valueOf(row[2]);
	    var par = Integer.valueOf(row[3]);
	    var strokes = Integer.valueOf(row[4]);
	    var fairwayInRegulation = Boolean.parseBoolean(row[5]);
	    var putts = Integer.valueOf(row[6]);

	    Hole hole = Hole.of(number, index, par, strokes, fairwayInRegulation, putts);
		this.holes.merge(id, List.of(hole), (prior, current) -> {
			List<Hole> result = new ArrayList<>(prior);
			result.addAll(current);
			return result;
		});
	};

}
