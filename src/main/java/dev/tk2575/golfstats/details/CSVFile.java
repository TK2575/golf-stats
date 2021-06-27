package dev.tk2575.golfstats.details;

import lombok.*;

@Getter
public class CSVFile {
	private final String name;
	private final String header;
	private final String body;

	public CSVFile(String name, String content) {
		if (!name.endsWith(".csv")) {
			throw new IllegalArgumentException("File extension is not .csv");
		}
		this.name = name;

		if (!content.contains(",")) {
			throw new IllegalArgumentException("CSV file has no commas");
		}
		String[] split = content.split("\n", 2);
		this.header = split[0];
		this.body = split[1];

	}
}
