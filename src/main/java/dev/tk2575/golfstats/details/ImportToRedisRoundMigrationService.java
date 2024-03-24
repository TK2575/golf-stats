package dev.tk2575.golfstats.details;

import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.imports.GolfRoundImporter;
import dev.tk2575.golfstats.details.redis.RedisService;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class ImportToRedisRoundMigrationService {

  //run locally with redis running 
  //should only be run as a one-off to import rounds into a fresh redis instance
  public static void main(String[] args) {
    RedisService redis = new RedisService("192.168.1.55", 16379);
    if (redis.countRounds() == 0) {
      log.info("Importing rounds into Redis");
      List<GolfRound> rounds = new GolfRoundImporter().getRoundsByGolfer().get("Tom");
      Set<String> ids = rounds.stream().map(redis::saveRound).collect(Collectors.toSet());
      log.info("Imported {} rounds with ids: {}", ids.size(), ids);
    }
  }
}
