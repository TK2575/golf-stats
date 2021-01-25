package dev.tk2575.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.tk2575.golfstats.golfround.GolfRound;
import dev.tk2575.golfstats.parsers.SimpleGolfRoundCSVParser;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Log4j2
@Component
public class MongoInteraction implements Runnable {

	//TODO retrieve from application.properties
	private static final String mongoDbUri = "mongodb://tkain:probsting@192.168.1.55:27017";

	private static final String DATABASE = "golf-stats";

	@Override
	public void run() {
		CodecRegistry codec = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider
				.builder()
				.automatic(true)
				.build()));

		log.info(mongoDbUri);
		try (MongoClient client = MongoClients.create(mongoDbUri)) {
			MongoDatabase database = client.getDatabase(DATABASE).withCodecRegistry(codec);
			MongoCollection<GolfRound> roundCollection = database.getCollection("golf_rounds", GolfRound.class);

			List<GolfRound> roundPojos = getCSVData();
			roundCollection.insertOne(roundPojos.get(0));
		}
	}

	private static List<GolfRound> getCSVData() {
		final File dataDirectory = new File(System.getProperty("user.dir"), "src\\main\\resources\\data");
		Map<String, List<GolfRound>> roundsByGolfer = new SimpleGolfRoundCSVParser(dataDirectory).readCsvData();

		List<GolfRound> results = new ArrayList<>();
		for (List<GolfRound> list : roundsByGolfer.values()) {
			results.addAll(list);
		}
		return results;
	}
}
