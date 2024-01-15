package dev.tk2575;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	//TODO need golf round input validator, rather than waiting on 500 error on stats api
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
