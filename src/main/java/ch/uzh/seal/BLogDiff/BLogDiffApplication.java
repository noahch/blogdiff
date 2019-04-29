package ch.uzh.seal.BLogDiff;

import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class BLogDiffApplication {

	public static void main(String[] args) {
		SpringApplication.run(BLogDiffApplication.class, args);
	}

}
