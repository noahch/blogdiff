package ch.uzh.seal.BLogDiff.repository;

import ch.uzh.seal.BLogDiff.model.survey.MiniSurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiniSurveyRepository extends JpaRepository<MiniSurveyResult, Long> {

}
