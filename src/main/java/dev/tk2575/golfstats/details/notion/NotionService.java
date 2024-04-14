package dev.tk2575.golfstats.details.notion;

import dev.tk2575.Application;
import dev.tk2575.golfstats.ApplicationProperties;
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

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Log4j2
@Service
public class NotionService {
  private final String notionToken;

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
          if (block.getType().getValue().equals("paragraph")) {
            log.info("Block content: {}", block.asParagraph().getParagraph().getRichText());
          } else if (block.getType().getValue().equals("table")) {
            Blocks tableBlocks = notion.retrieveBlockChildren(block.getId(), null, 100);
            //TODO verify correct width & headers (is this the right table?)
            updateTable(notion, tableBlocks, List.of());
          }
        }
      });
    }
  }

  private void updateTable(NotionClient notion, Blocks currentTable, List<RoundDetailTableColumn> newContent) {
    Queue<RoundDetailTableColumn> contentQueue = new LinkedList<>(newContent);
    List<Block> rows = currentTable.getResults().stream().filter(row -> row.getType().getValue().equals("table_row")).toList();
    updateRow(notion, rows.getFirst().asTableRow(), RoundDetailTableColumn.HEADERS);

    rows.subList(1, rows.size()).forEach(row -> {
      TableRowBlock tableRow = row.asTableRow();
      RoundDetailTableColumn content = contentQueue.poll();
      if (content != null) {
        updateRow(notion, tableRow, content.toValues());
      }
    });
  }

  private void updateRow(NotionClient notion, TableRowBlock tableRow, List<String> values) {
    String rowId = tableRow.getId();
    List<List<PageProperty.RichText>> cells = tableRow.getTableRow().getCells();
    if (rowId != null && cells.size() > 10) {
      Map<BlockType, BlockElementUpdate> updates = new EnumMap<>(BlockType.class);
      for (int i = 0; i <= 10; i++) {
        cells.get(i).getFirst().getText().setContent(values.get(i));
        updates.put(tableRow.getType(), new BlockElementUpdate(cells.get(i)));
      }
      notion.updateBlock(rowId, updates);
    }

  }

  //TODO create a new table with the new content

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
