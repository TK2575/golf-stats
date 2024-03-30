package dev.tk2575.golfstats.details;

import dev.tk2575.Application;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.details.imports.GolfRoundImporter;
import dev.tk2575.golfstats.details.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ImportsToRedisRoundMigrationService {

  private final RedisService redis;

  @Autowired
  public ImportsToRedisRoundMigrationService(RedisService redis) {
    this.redis = redis;
  }

  void importRounds() {
    if (redis.countRounds() > 0) {
      log.info("Rounds already imported into Redis");
    } else {
      log.info("Importing rounds into Redis");
      List<GolfRound> rounds = new GolfRoundImporter().getRoundsByGolfer().get("Tom");
      Set<String> ids = rounds.stream().map(redis::saveRound).collect(Collectors.toSet());
      log.info("Imported {} rounds with ids: {}", ids.size(), ids);
    }
  }

  //run locally with redis running 
  //should only be run as a one-off to import rounds into a fresh redis instance
  public static void main(String[] args) {
    try (ConfigurableApplicationContext context = SpringApplication.run(Application.class, args)) {
      ImportsToRedisRoundMigrationService service = context.getBean(ImportsToRedisRoundMigrationService.class);
      service.importRounds();
    } catch (Exception e) {
      log.error(e);
    } finally {
      System.exit(0);
    }
  }
}
