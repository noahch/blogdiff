package ch.uzh.seal.BLogDiff.repository;

import ch.uzh.seal.BLogDiff.model.survey.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<SurveyResult, Long> {

}
