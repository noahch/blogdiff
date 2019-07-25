package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.differencing.ASTDifferencer;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.*;
import ch.uzh.seal.BLogDiff.model.rest.Log;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import com.google.gson.Gson;
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
//
//        BuildLogTree t1 = getOneLevelTree("C:\\Data\\BA\\rl1.txt");
//        BuildLogTree t2 = getOneLevelTree("C:\\Data\\BA\\rl2.txt");


        BuildLogTree t1 = travisService.getBuildLogTreeForTest(FileUtils.readFile("C:\\Data\\BA\\rl1.txt"), true);
        BuildLogTree t2 = travisService.getBuildLogTreeForTest(FileUtils.readFile("C:\\Data\\BA\\rl2.txt"),true);



//        BuildLogTree t1 = travisService.getBuildLogTree("554605518", true);
//        BuildLogTree t2 = travisService.getBuildLogTree("554643985", true);

        long ts = System.nanoTime();
        EditTree editTree = nodeLevelMapper.map(t1, t2, new LineDifferencer());
        long te = System.nanoTime();
        log.info("Lines in node: " + t1.getNodes().get(0).getLinesBefore().size());
        log.info("EditTreeSize: " + editTree.getChildrenActions().get(0).getLinesBeforeActions().size());
        log.info("Time elapsed LD: " +  (te-ts));

        Gson gson = new Gson();
        String json = gson.toJson(editTree);
        FileUtils.writeToSb("C:\\Data\\BA\\", "etLD.txt", json);

        ts = System.nanoTime();
        editTree = nodeLevelMapper.map(t1, t2, new ASTDifferencer());
        te = System.nanoTime();
        log.info("Lines in node: " + t1.getNodes().get(0).getLinesBefore().size());
        log.info("EditTreeSize: " + editTree.getChildrenActions().get(0).getLinesBeforeActions().size());
        log.info("Time elapsed AST: " +  (te-ts));

        json = gson.toJson(editTree);
        FileUtils.writeToSb("C:\\Data\\BA\\", "etAST.txt", json);
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
    @RequestMapping("/analysis/differencing2")
    private void analyseDifferencing2(){

        //successful sequence
        String[][] jobs =  {
                {"555684793","555819512"}, // noahch/blogdiff
                {"559110346","559670786"}, // apache/accumulo
                {"558000323","559134087"}, // asciidocfx/AsciidocFX
                {"535133332","535138722"}, // simpligility/android-maven-plugin
                {"432465252","435620282"}, // spotify/docker-client
                {"496369595","499906077"}, // spring-projects/spring-data-elasticsearch
                {"407839576","407842660"}, // wstrange/GoogleAuth
//                {"556112100","557479328"}, // google/dagger
//                {"421217921","421231392"}, // swagger-api/swagger-core
//                {"543128552","543716155"}, // apache/struts
                {"499807007","499816460"}, // spring-projects/spring-data-jpa
                {"339309648","375182522"}, // Nukkit/Nukkit
                {"529851442","532916599"}, // google/compile-testing
                {"445185673","445783150"}, // tumblr/jumblr
                {"445185673","445783150"}, // tumblr/jumblr
                {"545469486","547020095"}  // google/auto
        };

        String[][] jobsFailed =  {
                {"550199498","551284647"}, // noahch/blogdiff
                {"523258567","523287690"}, // apache/accumulo
                {"518111925","518116984"}, // simpligility/android-maven-plugin
                {"453979855","457391738"}, // spotify/docker-client
                {"483985956","484041151"}, // spring-projects/spring-data-elasticsearch
                {"464887312","508475465"}, // wstrange/GoogleAuth
                {"464887312","508475465"}, // google/dagger
                {"499425907","500241945"}, // apache/commons-io
                {"508001259","510402053"}, // consulo/consulo
                {"499773807","499794043"}, // spring-projects/spring-data-redis
                {"501492127","501525864"}, // spring-projects/spring-data-mongodb

        };

        processJobList(jobs, "successfulSequence.txt");
        processJobList(jobsFailed, "failedSequence.txt");
    }

    private void processJobList(String[][] jobs, String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("Job1;Job2;Length1;Length2;ActionLineIndex;ActionType;PosBefore;PosAfter;ContBefore;ContAfter\n");
        log.info("STATED FILE: " + filename);
        for(int i = 0; i < jobs.length; i++) {
            log.info("started with: " + i);
            String j1 = jobs[i][0];
            String j2 = jobs[i][1];
            double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
            double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
            BuildLogTree tree1 = travisService.getBuildLogTree(j1,true);
            BuildLogTree tree2 = travisService.getBuildLogTree(j2,true);
            EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
            List<LineAction> l = getLineActions(tree1, tree2, editTree);
            writeFile(sb, l, j1, j2, l1, l2);
            log.info("finished with: " + i);
        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", filename, sb.toString());
    }

    private void writeFile(StringBuilder sb, List<LineAction> lineActions, String j1, String j2, double l1, double l2){
        for(LineAction la: lineActions){
            sb.append(j1+";"+j2+";"+l1+";"+l2+";"+la.getLineIdx()+";"+la.getType()+";"+la.getPositionBefore()+";"+la.getPositionAfter()+";"+la.getContentBefore().replaceAll(";", ":")+";"+la.getContentAfter().replaceAll(";", ":")+"\n");
        }
    }

    private List<LineAction> getLineActions(BuildLogTree tree1,BuildLogTree tree2, EditTree editTree){
        List<LineAction> l = new ArrayList<>();
        for (EditAction ea: editTree.getChildrenActions()){
            l.addAll(getActionRec(tree1.getNodes().get(0),tree2.getNodes().get(0),ea));
        }
        return l;
    }

    private List<LineAction> getActionRec(BuildLogNode node1, BuildLogNode node2, EditAction editAction){
        BuildLogNode node = node1;
        if(node == null){
            node = node2;
        }
        if(editAction.getChildrenActions() == null || editAction.getChildrenActions().size() < 1){
            List<LineAction> l = new ArrayList<>();
            List<LineAction> lb = editAction.getLinesBeforeActions();
            List<LineAction> la = editAction.getLinesAfterActions();
            enrichIdx(node.getLinesBefore(), lb);
            enrichIdx(node.getLinesAfter(), la);
            l.addAll(lb);
            l.addAll(la);
            return l;
        }
        List<LineAction> l = new ArrayList<>();
        for(EditAction editAction1: editAction.getChildrenActions()){
            l.addAll(getActionRec(getNodeByName(editAction1.getNodeName(), node1), getNodeByName(editAction1.getNodeName(), node2), editAction1));
        }
        List<LineAction> lb = editAction.getLinesBeforeActions();
        List<LineAction> la = editAction.getLinesAfterActions();
        enrichIdx(node.getLinesBefore(), lb);
        enrichIdx(node.getLinesAfter(), la);
        l.addAll(lb);
        l.addAll(la);
        return l;

    }

    private BuildLogNode getNodeByName(String name, BuildLogNode node){
        log.info(name);
        if(node == null){
            return null;
        }
        return node.getLogNodes().stream().filter(buildLogNode -> buildLogNode.getNodeName().equals(name)).findFirst().orElse(null);
    }

    private void enrichIdx(List<LogLine> logLines, final List<LineAction> lineActions){
        if(logLines == null){
            return;
        }
        for(LineAction la: lineActions){
            int idx = 0;
            if(la.getPositionBefore() != 0){
                if(la.getPositionAfter() != 0){
                    idx = (la.getPositionBefore()+la.getPositionAfter()/2);
                }else{
                    idx = la.getPositionBefore();
                }
            }else{
                idx = la.getPositionAfter();
            }
            if(idx >= logLines.size()){
                idx = logLines.size() -1 ;
            }
            la.setLineIdx(logLines.get(idx).getLineIndex());
        }
    }

}
