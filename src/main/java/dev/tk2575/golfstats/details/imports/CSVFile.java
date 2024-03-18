package dev.tk2575.golfstats.details.imports;

import dev.tk2575.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
@ToString
class CSVFile {
  private final String name;
  private final String header;

  @ToString.Exclude
  private final List<String[]> rowsOfDelimitedValues;

  CSVFile(String name, String content) {
    if (!name.endsWith(".csv")) {
      throw new IllegalArgumentException("File extension is not .csv");
    }
    this.name = name;

    if (!content.contains(",")) {
      throw new IllegalArgumentException("CSV file has no commas");
    }
    String[] split = content.split("\n", 2);
    this.header = split[0];

    this.rowsOfDelimitedValues = new ArrayList<>();
    for (String row : split[1].split("\n")) {
      this.rowsOfDelimitedValues.add(row.split(","));
    }
  }

  static List<CSVFile> readCSVFilesInDirectory(@NonNull String directory) {
    List<CSVFile> files = new ArrayList<>();
    ClassLoader classLoader = Utils.class.getClassLoader();
    InputStream directoryStream = classLoader.getResourceAsStream(directory);

    if (directoryStream != null) {
      List<String> fileNames =
          new BufferedReader(new InputStreamReader(directoryStream))
              .lines()
              .filter(each -> !each.contains(" "))
              .filter(each -> each.endsWith(".csv"))
              .toList();


      InputStream fileStream;
      for (String each : fileNames) {
        fileStream = classLoader.getResourceAsStream(String.join("/", directory, each));
        if (fileStream != null) {
          files.add(new CSVFile(each, new BufferedReader(new InputStreamReader(fileStream)).lines().collect(joining("\n"))));
        }
      }
    }

    return files;
  }
}
