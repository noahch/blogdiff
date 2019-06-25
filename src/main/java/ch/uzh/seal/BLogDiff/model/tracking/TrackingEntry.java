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
@Table(name = "tracking")
public class TrackingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date timestamp;

    private String userId;

    private String repository;

    private String logId1;

    private String logId2;

    private int timeSpent;

    private boolean additions;
    private boolean deletions;
    private boolean updates;
    private boolean moves;
    private boolean highlight;
    private boolean wrap;
    private boolean differenceOnly;
    private boolean symmetricNodes;
    private boolean hideNodes;


}
