package dev.tk2575.golfstats.details.imports;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

public class Hole19JsonParser {

	public static void main(String[] args) {
		String directory = "data/hole19/hole19_export-tom.json";
		List<Hole19Round> rounds = parse(directory);
		System.out.println(rounds.get(0).getStartedAt());
	}

	public static List<Hole19Round> parse(String directory) {
		InputStream input = Hole19JsonParser.class.getClassLoader().getResourceAsStream(directory);
		assert input != null;
		InputStreamReader reader = new InputStreamReader(input);
		Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
		return gson.fromJson(reader, Hole19Export.class).getData();
	}
}
