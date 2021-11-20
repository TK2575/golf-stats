package dev.tk2575.golfstats.details.parsers;

import dev.tk2575.Utils;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.core.golfround.shotbyshot.Shot;
import dev.tk2575.golfstats.core.golfround.shotbyshot.ShotAbbreviation;
import dev.tk2575.golfstats.details.CSVFile;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Getter
@Log4j2
public class ShotByShotRoundCSVParser extends CSVParser {

	private static final String EXPECTED_HEADERS_ROUND = "id,golfer,date,course,city,state,tees,rating,slope,start,end,transport";
	private static final String EXPECTED_HEADERS_HOLES = "id,hole,index,par,shots";

	private static final List<DateTimeFormatter> TIME_FORMATS = List.of(DateTimeFormatter.ofPattern("h:m a"), DateTimeFormatter.ofPattern("k:m"));
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

	private CSVFile roundFile = null;
	private CSVFile holesFile = null;

	private final Map<Integer, RoundMeta> roundMetas = new HashMap<>();
	private final Map<Integer, List<Hole>> holes = new HashMap<>();
	private final Map<String, Golfer> golfers = new HashMap<>();

	public ShotByShotRoundCSVParser(@NonNull List<CSVFile> files) {
		if (files.size() != 2) {
			throw new IllegalArgumentException("Expecting exactly two csv files, one round detail file, and one hole result file");
		}

		for (CSVFile file : files) {
			if (EXPECTED_HEADERS_ROUND.equalsIgnoreCase(file.getHeader())) {
				this.roundFile = file;
			}
			else if (EXPECTED_HEADERS_HOLES.equalsIgnoreCase(file.getHeader())) {
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
		super.parse(this.roundFile, roundMetaParser);
		super.parse(this.holesFile, holeParser);
		return GolfRound.compile(this.roundMetas, this.holes);
	}

	private final BiConsumer<Integer, String[]> roundMetaParser = (id, row) -> {
		String golferString = Utils.toTitleCase(row[1]);
		var golfer = this.golfers.computeIfAbsent(golferString, Golfer::newGolfer);
		var date = LocalDate.parse(row[2], DATE_FORMAT);
		var teeName = Utils.toTitleCase(row[6]);
		var rating = new BigDecimal(row[7]);
		var slope = new BigDecimal(row[8]);
		var tee = Tee.of(teeName, rating, slope);
		var course = Course.of(row[3], tee, row[4], row[5]);
		var duration = Duration.between(Utils.parseTime(TIME_FORMATS, row[9]), Utils.parseTime(TIME_FORMATS, row[10]));
		var transport = Utils.toTitleCase(row[11]);

		this.roundMetas.put(id, new RoundMeta(date, duration, golfer, course, tee, transport));
	};

	private final BiConsumer<Integer, String[]> holeParser = (id, row) -> {
		var number = Integer.valueOf(row[1]);
		var holeIndex = Integer.valueOf(row[2]);
		var par = Integer.valueOf(row[3]);

		List<ShotAbbreviation> shotAbbreviations = Arrays.stream(row[4].split("\\."))
				.sequential()
				.map(ShotAbbreviation::parse)
				.toList();

		Hole hole = Hole.of(number, holeIndex, par, Shot.compile(shotAbbreviations));
		this.holes.merge(id, List.of(hole), (prior, current) -> {
			List<Hole> result = new ArrayList<>(prior);
			result.addAll(current);
			return result;
		});
	};
}
