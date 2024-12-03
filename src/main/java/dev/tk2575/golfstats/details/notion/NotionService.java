package dev.tk2575.golfstats.details.notion;

import dev.tk2575.Application;
import dev.tk2575.golfstats.ApplicationProperties;
import lombok.extern.log4j.Log4j2;
import notion.api.v1.NotionClient;
import notion.api.v1.logging.NotionLogger;
import notion.api.v1.model.databases.QueryResults;
import notion.api.v1.model.pages.Page;
import notion.api.v1.request.databases.QueryDatabaseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
      notion.setLogger(new NoLog()); // hack to disables request/response logging

      // golf rounds database
      String url = "https://www.notion.so/tk2575/ee0e935d0b7d4a268974508179012383?v=25203d645af84a09bacdc3e4df8965ad";
      String uuid = getUUIDFromUrl(url);
      var db = notion.retrieveDatabase(uuid);
      log.info("db ID: {}", db.getId());

      //TODO add filter for "Trigger Validation" is true/checked on query
      QueryDatabaseRequest query = new QueryDatabaseRequest(db.getId());
      QueryResults queryResults = notion.queryDatabase(query);
//      log.info("query results: {}", queryResults);
      log.info("first result property keys: {}", queryResults.getResults().getFirst().getProperties().keySet());
      List<Page> awaitingValidation =
          queryResults.getResults().stream()
              .filter(page -> page.getProperties().get("Trigger Validation").getCheckbox())
              .toList();

      // TODO parse shorthands
      Page last = awaitingValidation.getLast();
//      log.info(last.getProperties())
      var shorthand = last.getProperties().get("Shots Shorthand").getRichText();
      if (shorthand == null) {
        log.info("no shorthands");
      }
      else if (shorthand.size() > 1) {
        log.info("shorthand is of size {}", shorthand.size());
      }
      else {
        log.info("shorthand: {}", shorthand.getFirst().getPlainText());
      }

      // TODO change values on page
    }
  }

  protected static String getUUIDFromUrl(String pageUrl) {
    // Regular expression to capture the last 32-character ID in the URL
    Pattern pattern = Pattern.compile("([a-fA-F0-9]{32})(?:\\?|$)");
    Matcher matcher = pattern.matcher(pageUrl);

    if (matcher.find()) {
      String id = matcher.group(1);

      // Convert the ID into UUID format by inserting hyphens
      return id.substring(0, 8) + "-" +
          id.substring(8, 12) + "-" +
          id.substring(12, 16) + "-" +
          id.substring(16, 20) + "-" +
          id.substring(20);
    }

    throw new IllegalArgumentException("No valid ID found in the URL");
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
