package ch.uzh.seal.BLogDiff.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Repository {

        @Getter
        @Setter
        private Long id;

        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String slug;
}
