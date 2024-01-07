package dev.tk2575;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		logRawArgs(args);

//		https://www.baeldung.com/jcommander-parsing-command-line-parameters
		var appArgs = new ApplicationArgs();
		JCommander helloCmd = 
				JCommander.newBuilder()
						.addObject(appArgs)
						.build();
		helloCmd.parse(args);
		log.info("Hello " + appArgs.getName());
	}

	private void logRawArgs(String... args) {
		for (int i = 0; i < args.length; ++i) {
            log.info("args[{}]: {}", i, args[i]);
        }
	}
}
