package ch.uzh.seal.BLogDiff.model.tracking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "bugs")
public class BugReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String repository;
    private String jobId1;
    private String jobId2;
    private Long lineNr1;
    private Long lineNr2;
    private Date timestamp;
    @Column(length = 2048)
    private String bug;
}
