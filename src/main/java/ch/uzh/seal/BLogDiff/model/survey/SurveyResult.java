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

    private double experience;

    @JsonProperty("failing_builds")
    private double failingBuilds;

    private int useful;

    @JsonProperty("find_faster")
    private int findFaster;

    private int accuracy;

    @JsonProperty("use_again")
    private int useAgain;

    private double age;

    private String gender;

    private String education;

    private String position;

    @JsonProperty("build_log_experience")
    private String buildLogExperience;

    @JsonProperty("month_on_project")
    private int monthOnProject;

    @JsonProperty("people_on_project")
    private int peopleOnProject;

    private String role;

    @JsonProperty("time_to_find_failure")
    private double timeToFindFailure;

    @JsonProperty("easy_to_use")
    private int easyToUse;

    @JsonProperty("filtering_noise")
    private int filteringNoise;

    private int integration;

    private int advantage;

    private int recommend;

    private String suggestion;

    private String comment;

    @JsonProperty("contact_again")
    private String contactAgain;

    private String email;

    private int source;

    @JsonProperty("primary_language")
    private String primaryLanguage;

    @JsonProperty("build_tool_use")
    private String buildToolUse;

    @JsonProperty("ci_use")
    private String ciUse;

    @JsonProperty("usually_process")
    private String usuallyProcess;

}
