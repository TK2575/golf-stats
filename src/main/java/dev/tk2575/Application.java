package dev.tk2575;

import dev.tk2575.golfstats.details.GolfStatsConsoleOutput;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		new GolfStatsConsoleOutput().run();

		System.exit(0);
	}

}
