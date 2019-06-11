package ch.uzh.seal.BLogDiff.model.survey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "survey")
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String experience;

    @JsonProperty("number_of_people")
    private String numberOfPeople;

    @JsonProperty("failing_builds")
    private String failingBuilds;

    @JsonProperty("how_long")
    private String howLong;

    private int useful;

    @JsonProperty("integrate_workflow")
    private int integrateWorkflow;

    @JsonProperty("find_faster")
    private int findFaster;

    private int accuracy;

    private String suggestions;

    @JsonProperty("use_again")
    private int useAgain;


}
