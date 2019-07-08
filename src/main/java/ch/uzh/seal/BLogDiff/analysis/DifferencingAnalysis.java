package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.differencing.ASTDifferencer;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogNode;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;
import ch.uzh.seal.BLogDiff.model.rest.Log;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RestController
public class DifferencingAnalysis {

    @Autowired
    private TravisService travisService;

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @RequestMapping("/analysis/differencing")
    private void analyseDifferencing(){
        Log l1 = travisService.getLog("554605518");
        Log l2 = travisService.getLog("554643985");

//        BuildLogTree t1 = getOneLevelTree(l1);
//        BuildLogTree t2 = getOneLevelTree(l2);

        BuildLogTree t1 = getOneLevelTree("C:\\Data\\BA\\rl1.txt");
        BuildLogTree t2 = getOneLevelTree("C:\\Data\\BA\\rl2.txt");

//        BuildLogTree t1 = travisService.getBuildLogTree("554605518", true);
//        BuildLogTree t2 = travisService.getBuildLogTree("554643985", true);

        long ts = System.nanoTime();
        EditTree editTree = nodeLevelMapper.map(t1, t2, new LineDifferencer());
        long te = System.nanoTime();
        log.info("Lines in node: " + t1.getNodes().get(0).getLinesBefore().size());
        log.info("EditTreeSize: " + editTree.getChildrenActions().get(0).getLinesBeforeActions().size());
        log.info("Time elapsed LD: " +  (te-ts));

        ts = System.nanoTime();
        editTree = nodeLevelMapper.map(t1, t2, new ASTDifferencer());
        te = System.nanoTime();
        log.info("Lines in node: " + t1.getNodes().get(0).getLinesBefore().size());
        log.info("EditTreeSize: " + editTree.getChildrenActions().get(0).getLinesBeforeActions().size());
        log.info("Time elapsed AST: " +  (te-ts));
    }

    private BuildLogTree getOneLevelTree(Log log){
        String[] lines = log.getContent().split("(\\r?\\n)|\\r");
        List<LogLine> lineList = new ArrayList<>();
        for(int i = 0; i < lines.length; i++){
            lineList.add(LogLine.builder().content(lines[i]).lineIndex(i).internalLineIndex(i).build());
        }

        List<BuildLogNode> nodes = new ArrayList<>();
        nodes.add(BuildLogNode.builder().linesBefore(lineList).nodeName("N1").build());
        return BuildLogTree.builder().nodes(nodes).build();
    }

    private BuildLogTree getOneLevelTree(String path){
        List<String> lines = FileUtils.readFileAsList(path);
        List<LogLine> lineList = new ArrayList<>();
        for(int i = 0; i < lines.size(); i++){
            lineList.add(LogLine.builder().content(lines.get(i)).lineIndex(i).internalLineIndex(i).build());
        }

        List<BuildLogNode> nodes = new ArrayList<>();
        nodes.add(BuildLogNode.builder().linesBefore(lineList).nodeName("N1").build());
        return BuildLogTree.builder().nodes(nodes).build();
    }
}
