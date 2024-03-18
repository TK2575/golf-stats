package dev.tk2575.golfstats.details.imports;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GolfRoundImporterTest {

  @Test
  void testJoin() {
    List<CSVFile> files = CSVFile.readCSVFilesInDirectory("data/simple");
    assertFalse(files.isEmpty());
    List<GolfRound> simpleRounds = new SimpleGolfRoundCSVParser(files).parse();
    assertFalse(simpleRounds.isEmpty());

    List<Hole19Round> hole19Rounds = Hole19JsonParser.parse("data/hole19/hole19_export-tom.json");
    assertFalse(hole19Rounds.isEmpty());

    List<GolfRound> joinRounds = GolfRoundImporter.merge(simpleRounds, hole19Rounds);
    assertFalse(joinRounds.isEmpty());
    Map<String, GolfRound> joinRoundsMap = joinRounds.stream().collect(toMap(this::roundKey, identity()));

    GolfRound join;
    for (GolfRound each : simpleRounds) {
      String roundKey = roundKey(each);
      join = joinRoundsMap.get(roundKey);
      assertNotNull(join);
      assertEquals(each.getStrokes(), join.getStrokes());
    }

    for (Hole19Round each : hole19Rounds) {
      if (each.getHoles().size() >= 9) {
        String roundKey = roundKey(each);
        join = joinRoundsMap.get(roundKey);
        assertNotNull(join);
        if (each.getHoles().size() == join.getHoleCount()) {
          assertEquals(each.getStrokes(), join.getStrokes());
        }
      }
    }
  }

  private String roundKey(GolfRound round) {
    return String.join("-", round.getDate().toString(), 
        round.getGolfer().getName(), 
        round.getCourse().getName())
        .toLowerCase();
  }

  private String roundKey(Hole19Round round) {
    return String.join("-", round.getStartedAt().toLocalDate().toString(), "Tom", round.getCourse())
        .toLowerCase();
  }

}