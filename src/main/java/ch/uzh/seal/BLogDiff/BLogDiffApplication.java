package ch.uzh.seal.BLogDiff;

import ch.uzh.seal.BLogDiff.analysis.PreprocessingAnalysis;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogNode;
import ch.uzh.seal.BLogDiff.model.parsing.NodeAction;
import ch.uzh.seal.BLogDiff.service.TravisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
public class BLogDiffApplication {

	public static void main(String[] args) {

		NodeLevelMapper nlm = new NodeLevelMapper();
		List<BuildLogNode> n1 = new ArrayList<>();
		n1.add(BuildLogNode.builder().nodeName("N1").build());
		n1.add(BuildLogNode.builder().nodeName("N2").build());
		n1.add(BuildLogNode.builder().nodeName("N3").build());
		n1.add(BuildLogNode.builder().nodeName("N5").build());

		List<BuildLogNode> n2 = new ArrayList<>();
		n2.add(BuildLogNode.builder().nodeName("N2").build());
		n2.add(BuildLogNode.builder().nodeName("N1").build());
		n2.add(BuildLogNode.builder().nodeName("N3").build());
		n2.add(BuildLogNode.builder().nodeName("N4").build());

//		List<NodeAction> nodeActions = nlm.mapNodes(n1,n2);
 		SpringApplication.run(BLogDiffApplication.class, args);
	}

}
