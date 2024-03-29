package dev.tk2575.golfstats.details.imports;

import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.Hole;
import dev.tk2575.golfstats.core.golfround.HoleStream;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

@Log4j2
public class GolfRoundImporter {

    public Map<String, List<GolfRound>> getRoundsByGolfer() {
        return this.readGolfRoundsFromFiles().stream().collect(groupingBy(each -> each.getGolfer().getName()));
    }

    private List<GolfRound> readGolfRoundsFromFiles() {
        List<GolfRound> simpleRounds = new SimpleGolfRoundCSVParser(CSVFile.readCSVFilesInDirectory("data/simple")).parse();

        List<GolfRound> rounds = new ArrayList<>(merge(
                simpleRounds,
                Hole19JsonParser.parse("data/hole19/hole19_export-tom.json")
        ));

        rounds.addAll(new HoleByHoleRoundCSVParser(CSVFile.readCSVFilesInDirectory("data/hole-by-hole")).parse());
        rounds.addAll(new ShotByShotRoundCSVParser(CSVFile.readCSVFilesInDirectory("data/shot-by-shot")).parse());

        return rounds.stream().sorted(
                comparing((GolfRound each) -> each.getGolfer().getName())
                        .thenComparing(GolfRound::getDate)).toList();
    }

    static List<GolfRound> merge(@NonNull List<GolfRound> simpleRounds, @NonNull List<Hole19Round> hole19Rounds) {
        if (hole19Rounds.isEmpty()) {
            return simpleRounds;
        }

        Map<String, List<Hole19Round>> hole19Map =
                hole19Rounds.stream()
                        .filter(each -> !each.getHoles().isEmpty())
                        .collect(groupingBy(GolfRoundImporter::roundKey));

        Map<String, Course> courseTeeMap = missingCourseDetailsMap();
        List<GolfRound> results = new ArrayList<>();

        for (GolfRound simpleRound : simpleRounds) {
            if (simpleRound.getGolfer().getName().equalsIgnoreCase("Tom")) {
                results.add(rightJoin(simpleRound, hole19Map));
            } else {
                results.add(simpleRound);
            }

            courseTeeMap.merge(
                    simpleRound.getCourse().getName().toLowerCase(),
                    simpleRound.getCourse(),
                    (c1, c2) -> {
                        Set<Tee> tees = new HashSet<>(c1.getTees());
                        tees.addAll(c2.getTees());
                        return c1.setTees(tees.stream().toList());
                    }
            );
        }

        GolfRound round;
        Golfer tom = Golfer.newGolfer("Tom");
        for (List<Hole19Round> holeList : hole19Map.values()) {
            round = createRound(tom, holeList, courseTeeMap);
            if (round != null) {
                results.add(round);
            }
        }

        return results;
    }

    private static String roundKey(Hole19Round round) {
        return String.join("-", round.getStartedAt().toLocalDate().toString(), round.getCourse()).toLowerCase();
    }

    private static String roundKey(GolfRound round) {
        return String.join("-", round.getDate().toString(), round.getCourse().getName()).toLowerCase();
    }

    private static Map<String, Course> missingCourseDetailsMap() {
        HashMap<String, Course> results = new HashMap<>();

        Tee tee = Tee.of("white", new BigDecimal("58"), new BigDecimal("113"), 58, 2666L);
        Course course = Course.of("Lake San Marcos CC (South)", List.of(tee), "San Marcos", "CA");
        results.put(course.getName().toLowerCase(), course);

        return results;
    }

    private static GolfRound createRound(Golfer golfer, List<Hole19Round> holeList, Map<String, Course> courseTeeMap) {
        GolfRound result = null;
        if (isValid(holeList)) {
            Hole19Round round = holeList.getFirst();
            Course course = courseTeeMap.get(round.getCourse().toLowerCase());
            if (course != null) {
                Tee tee = null;
                Optional<Tee> teeByName = course.getTee(round.getTee());
                if (teeByName.isPresent()) {
                    tee = teeByName.get();
                } else if (!course.getTees().isEmpty()) {
                    tee = course.getTees().getFirst();
                    course = course.setTees(List.of(tee));
                }
                if (tee != null) {
                    RoundMeta meta = new RoundMeta(golfer, round.getStartedAt(), round.getEndedAt(), course, tee);
                    result = GolfRound.of(meta, cleanHoleData(holeList));
                }
            }
        }
        return result;
    }

    private static GolfRound rightJoin(GolfRound simpleRound, Map<String, List<Hole19Round>> hole19Map) {
        String key = roundKey(simpleRound);
        List<Hole19Round> roundList = hole19Map.get(key);
        if (isValid(roundList, key)) {
            hole19Map.remove(key);
            return GolfRound.of(new RoundMeta(simpleRound), cleanHoleData(roundList));

        }
        return simpleRound;
    }

    private static boolean isValid(List<Hole19Round> roundList) {
        if (roundList != null && !roundList.isEmpty()) {
            return isValid(roundList, roundKey(roundList.getFirst()));
        }
        return false;
    }

    private static boolean isValid(List<Hole19Round> roundList, @NonNull String key) {
        if (roundList == null) {
            log.debug(String.format("Could not find Hole19 data for %s", key));
            return false;
        }
        if (roundList.size() > 2) {
            log.debug(String.format("Found more than two rounds for %s, skipping", key));
            return false;
        }

        long holeCount = roundList.stream().map(Hole19Round::getHoles).mapToLong(Collection::size).sum();
        if (holeCount == 0) {
            log.debug(String.format("Hole19 export did not include any hole data for %s", key));
            return false;
        }
        if (holeCount > 18) {
            log.debug(String.format("Found more than 18 holes for %s, skipping", key));
            return false;
        }
        if (holeCount < 9) {
            log.debug(String.format("Found less than 9 holes for %s, skipping", key));
            return false;
        }
        return true;
    }

    private static List<Hole> cleanHoleData(List<Hole19Round> input) {
        List<Hole> holes = new ArrayList<>(input.stream().map(Hole19Round::getHoles).flatMap(Collection::stream).toList());
        if (holes.size() > 9 && holes.size() < 18) {
            holes = new ArrayList<>(holes.subList(0, 9));
        }
        if (new HoleStream(holes).duplicateNumbers()) {
            holes = reassignHoleNumbers(holes);
        }
        if (holes.stream().map(Hole::getIndex).anyMatch(index -> index == 0)) {
            holes = randomlyAssignIndexes(holes);
        }
        if (new HoleStream(holes).duplicateIndexes()) {
            holes = new HoleStream(holes).shuffleEighteenHoleIndexes().toList();
        }
        return holes;
    }

    private static List<Hole> reassignHoleNumbers(List<Hole> holes) {
        List<Hole> results = new ArrayList<>(holes.subList(0, 9).stream().toList());
        results.addAll(holes.subList(9, holes.size()).stream().map(each -> each.getNumber() < 10 ? each.setNumber(each.getNumber() + 9) : each).toList());
        return results;
    }

    private static List<Hole> randomlyAssignIndexes(List<Hole> holes) {
        List<Integer> indexes = new ArrayList<>(IntStream.rangeClosed(1, 9).boxed().toList());
        Collections.shuffle(indexes);

        if (holes.size() == 18) {
            List<Integer> secondNine = new ArrayList<>(IntStream.rangeClosed(10, 18).boxed().toList());
            Collections.shuffle(secondNine);
            indexes.addAll(secondNine);
        }

        List<Hole> results = new ArrayList<>();
        int i = 0;
        for (Hole hole : holes) {
            results.add(hole.setIndex(indexes.get(i)));
            i++;
        }
        return results;
    }
}
