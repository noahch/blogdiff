package ch.uzh.seal.BLogDiff.preprocessing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "preprocessing")
@Getter
@Setter
public class PreprocessorConfig {

    public List<String> preprocessors;

    public String replacement;
}
