package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.Transport;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.handicapindex.HandicapIndex;
import dev.tk2575.golfstats.details.CSVFile;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Getter
@Log4j2
public class ShotByShotRoundCSVParser implements CSVParser {

	private static final String EXPECTED_HEADERS_ROUND = "id,golfer,date,course,city,state,tees,rating,slope,start,end,transport";
	private static final String EXPECTED_HEADERS_HOLES = "id,hole,index,par,shots";

	private static final List<DateTimeFormatter> TIME_FORMATS = List.of(DateTimeFormatter.ofPattern("h:m a"), DateTimeFormatter.ofPattern("k:m"));
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

	private CSVFile roundFile = null;
	private CSVFile holesFile = null;

	private Map<Integer, RoundMeta> roundMetas = new HashMap<>();
	private Map<Integer, List<Hole>> holes = new HashMap<>();
	private final Map<String, Golfer> golfers = new HashMap<>();

	public ShotByShotRoundCSVParser(@NonNull List<CSVFile> files) {
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

	//TODO move to interface as default but keep exception catch logging (abstract class?)
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
		String golferString = row[1];
		var golfer = this.golfers.computeIfAbsent(golferString, Golfer::newGolfer);
		var date = LocalDate.parse(row[2], DATE_FORMAT);
		var course = Course.of(row[3], row[4], row[5]);
		var teeName = row[6];
		var rating = new BigDecimal(row[7]);
		var slope = new BigDecimal(row[8]);
		var duration = Duration.between(Utils.parseTime(TIME_FORMATS, row[9]), Utils.parseTime(TIME_FORMATS, row[10]));
		var transport = Transport.valueOf(row[11]);

		this.roundMetas.put(id, new RoundMeta(date, duration, golfer, course, rating, slope, teeName, transport));
	};

	private final BiConsumer<Integer, String[]> holeParser = (id, row) -> {
		var number = Integer.valueOf(row[1]);
		var holeIndex = Integer.valueOf(row[2]);
		var par = Integer.valueOf(row[3]);

		List<Shot> shots = Arrays.stream(row[4].split("\\."))
				.sequential()
				.map(Shot::parse)
				.collect(Collectors.toList());

		Hole hole = Hole.of(number, holeIndex, par, shots);
		this.holes.merge(id, List.of(hole), (prior, current) -> {
			List<Hole> result = new ArrayList<>(prior);
			result.addAll(current);
			return result;
		});
	};
}
