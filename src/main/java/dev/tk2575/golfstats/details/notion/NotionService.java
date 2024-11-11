package dev.tk2575.golfstats.details.notion;

import dev.tk2575.Application;
import dev.tk2575.golfstats.ApplicationProperties;
import dev.tk2575.golfstats.core.course.Course;
import dev.tk2575.golfstats.core.course.tee.Tee;
import dev.tk2575.golfstats.core.golfer.Golfer;
import dev.tk2575.golfstats.core.golfround.GolfRound;
import dev.tk2575.golfstats.core.golfround.RoundMeta;
import dev.tk2575.golfstats.details.api.stats.RoundDetailTableColumn;
import lombok.extern.log4j.Log4j2;
import notion.api.v1.NotionClient;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.blocks.BlockElementUpdate;
import notion.api.v1.model.blocks.BlockType;
import notion.api.v1.model.blocks.Blocks;
import notion.api.v1.model.blocks.TableRowBlock;
import notion.api.v1.model.pages.PageProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Log4j2
@Service
public class NotionService {
  private final String notionToken;
  
  //TODO notion token env var for docker-compose

  @Autowired
  public NotionService(ApplicationProperties config) {
    this.notionToken = config.getNotionToken();
  }

  private void run() {
    try (var notion = new NotionClient(notionToken)) {
      String uuid = "bcfdfbf7-6c91-4776-92cd-868fe28a8c3a";
      var page = notion.retrievePage(uuid, List.of()); //latest round detail
      log.info("Page ID: {}", page.getId());
      log.info("Page properties:");
      page.getProperties().forEach((k, v) -> log.info("{} : {}", k, v));

      Blocks blocks = notion.retrieveBlockChildren(uuid, null, 100);
      blocks.getResults().forEach(block -> {
        if (block.getId() != null) {
          log.info("Block ID: {}; Block type: {}", block.getId(), block.getType());
        }
      });
    }
  }


  public static void main(String[] args) {
    try (ConfigurableApplicationContext context = SpringApplication.run(Application.class, args)) {
      NotionService notionService = context.getBean(NotionService.class);
      notionService.run();
    } catch (Exception e) {
      log.error(e);
    } finally {
      System.exit(0);
    }
  }

}
