package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.differencing.ASTDifferencer;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.*;
import ch.uzh.seal.BLogDiff.model.rest.Log;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.EditTreeUtils;
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

        double a = EditTreeUtils.getAdditions(editTree);
        double d = EditTreeUtils.getDeletions(editTree);
        double m = EditTreeUtils.getMoves(editTree);
        double u = EditTreeUtils.getUpdates(editTree);
        log.info("EditTreeSize: " + (a+d+m+u));
        log.info("Time elapsed LD: " +  (te-ts));

        Gson gson = new Gson();
        String json = gson.toJson(editTree);
        FileUtils.writeToSb("C:\\Data\\BA\\", "etLD.txt", json);

        ts = System.nanoTime();
        editTree = nodeLevelMapper.map(t1, t2, new ASTDifferencer());
        te = System.nanoTime();
        log.info("Lines in node: " + t1.getNodes().get(0).getLinesBefore().size());
        a = EditTreeUtils.getAdditions(editTree);
        d = EditTreeUtils.getDeletions(editTree);
        m = EditTreeUtils.getMoves(editTree);
        u = EditTreeUtils.getUpdates(editTree);
        log.info("EditTreeSize: " + (a+d+m+u));
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
                {"549574901","549928009"}, // noahch/blogdiff
                {"559110346","559670786"}, // apache/accumulo
                {"563306214","563328513"}, // apache/accumulo
                {"558000323","559134087"}, // asciidocfx/AsciidocFX
                {"443199899","447124533"}, // asciidocfx/AsciidocFX
                {"535133332","535138722"}, // simpligility/android-maven-plugin
                {"530123093","530123093"}, // simpligility/android-maven-plugin
                {"432465252","435620282"}, // spotify/docker-client
                {"460130349","460137537"}, // spotify/docker-client
                {"496369595","499906077"}, // spring-projects/spring-data-elasticsearch
                {"407839576","407842660"}, // wstrange/GoogleAuth
                {"515753486","515767196"}, // wstrange/GoogleAuth
                {"556112100","557479328"}, // google/dagger
                {"421217921","421231392"}, // swagger-api/swagger-core
                {"543128552","543716155"}, // apache/struts
                {"499807007","499816460"}, // spring-projects/spring-data-jpa
                {"339309648","375182522"}, // Nukkit/Nukkit
                {"380991952","382538251"}, // Nukkit/Nukkit
                {"529851442","532916599"}, // google/compile-testing
                {"445185673","445783150"}, // tumblr/jumblr
                {"445069535","445069535"}, // tumblr/jumblr
                {"545469486","547020095"}, // google/auto
                {"544534517","545469486"}, // google/auto
                {"559927762","559940341"}, // zxing/zxing
                {"546401404","546407833"}, // zxing/zxing
                {"559362399","560338115"}, // jhy/jsoup
                {"554472010","554508997"}, // jhy/jsoup
                {"542459765","542536960"}, // twitter/distributedlog
                {"564966733","564969707"}, // apache/zookeper
                {"564866502","564896569"}, // apache/zookeper
                {"525327953","525322578"},
                {"564752286","564751025"},
                {"564861420","564846548"},
                {"564910577","564867089"},
                {"564082693","563724891"},
                {"564140899","564102794"},
                {"564998357","564942136"},
                {"563464587","562890235"},
                {"564924765","564913170"},
                {"564141017","564140951"},
                {"563063238","562439986"},
                {"564868958","564858893"},
                {"564968915","564933604"},
                {"460137537","460130349"},
                {"564958360","563179305"},
                {"459136146","458926403"},
                {"563838048","563781605"},
                {"561122106","561119997"},
                {"163676138","163675377"},
                {"556166624","554719922"},
                {"542754905","542754667"},
                {"564352242","563440873"},
                {"564994546","564910919"},
                {"564141871","564132039"},
                {"561677800","560791887"},
                {"555040848","554733157"},
                {"561183076","561103678"},
                {"562925498","562427271"},
                {"564812364","564547448"},
                {"543059026","541365362"},
                {"564993969","564802345"},
                {"541342682","539685861"},
                {"563953733","563927025"},
                {"540095970","535138722"},
                {"559558395","559546588"},
                {"562915402","562627374"},
                {"562666728","560902560"},
                {"542533310","541986813"},
                {"542895760","542890482"},
                {"547020095","546963913"},
                {"403399589","403374157"},
                {"560241572","560231909"},
                {"542479699","541157731"},
                {"564686003","564439410"},
                {"563880944","563591174"},
                {"560856449","560788294"},
                {"564698401","564685557"},
                {"509516393","507220804"},
                {"327017018","327015017"},
                {"560927877","557923739"},
                {"231887132","223274350"},
                {"564821552","564821425"},
                {"563812726","563810976"},
                {"267359118","267355354"},
                {"542545806","542388610"},
                {"563622048","563591271"},
                {"467860405","467841525"},
                {"562629358","562627156"},
                {"564264670","564249086"},
                {"562790570","562785217"},
                {"564433600","564394678"},
                {"563557613","563539890"},
        };

        String[][] jobsFailed =  {
                {"550199498","551284647"}, // noahch/blogdiff
                {"523258567","523287690"}, // apache/accumulo
                {"518111925","518116984"}, // simpligility/android-maven-plugin
                {"535138722","547530379"}, // simpligility/android-maven-plugin
                {"453979855","457391738"}, // spotify/docker-client
                {"453382568","453654641"}, // spotify/docker-client
                {"483985956","484041151"}, // spring-projects/spring-data-elasticsearch
                {"464887312","508475465"}, // wstrange/GoogleAuth
                {"464887312","508475465"}, // google/dagger
                {"499425907","500241945"}, // apache/commons-io
                {"508001259","510402053"}, // consulo/consulo
                {"338875329","338875329"}, // Nukkit/Nukkit
                {"499773807","499794043"}, // spring-projects/spring-data-redis
                {"501492127","501525864"}, // spring-projects/spring-data-mongodb
                {"508405100","509308436"},
                {"558101947","559733586"},
                {"564789129","564847870"},
                {"561216613","562384950"},
                {"360114095","364269933"},
                {"560610693","560703315"},
                {"564942136","564973549"},
                {"553376709","553410972"},
                {"560091601","564134317"},
                {"562425890","562439652"},
                {"560842432","560847015"},
                {"559558293","560623831"},
                {"561122106","562368875"},
                {"559317483","563414735"},
                {"527526738","527529298"},
                {"547066937","551138452"},
                {"555040848","555337344"},
                {"561103678","561177740"},
                {"562925498","563407664"},
                {"538151432","543060077"},
                {"525182926","526031817"},
                {"563953733","564982325"},
                {"540095970","547530379"},
                {"562915402","563468211"},
                {"562666728","562690713"},
                {"563880944","564455625"},
                {"535559446","544293343"},
                {"559592024","561092638"},
                {"509516393","511548141"},
                {"499994881","500009446"},
                {"557923739","560923677"},
                {"536402813","536403991"},
                {"557458007","562143090"},
                {"559609240","563580815"},
                {"557546551","562414287"},
                {"563865925","564249121"},
                {"562499511","562511107"},
                {"562970149","563122820"},
                {"563557613","563566904"},

        };

//        processJobList(jobs, "successfulSequenceBIG.txt");
        processJobList(jobsFailed, "failedSequenceBIG.txt");
    }

    private void processJobList(String[][] jobs, String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("Job1;Job2;Length1;Length2;ActionLineIndex;ActionType;PosBefore;PosAfter;ContBefore;ContAfter\n");
        log.info("STATED FILE: " + filename);
        for(int i = 0; i < jobs.length; i++) {
            try{
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
            }catch (Exception e){
                log.info("oops");
            }

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
//        BuildLogNode node = node1;
//        if(node == null){
//            node = node2;
//        }
        if(editAction.getChildrenActions() == null || editAction.getChildrenActions().size() < 1){
            List<LineAction> l = new ArrayList<>();
            List<LineAction> lb = editAction.getLinesBeforeActions();
            List<LineAction> la = editAction.getLinesAfterActions();
            List<LogLine> n1lb = node1 != null ? node1.getLinesBefore() : new ArrayList<>();
            List<LogLine> n2lb = node2 != null ? node2.getLinesBefore() : new ArrayList<>();
            List<LogLine> n1la = node1 != null ? node1.getLinesAfter() : new ArrayList<>();
            List<LogLine> n2la = node2 != null ? node2.getLinesAfter() : new ArrayList<>();
            enrichIdx(n1lb, n2lb, lb);
            enrichIdx(n1la, n2la, la);
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
        List<LogLine> n1lb = node1 != null ? node1.getLinesBefore() : new ArrayList<>();
        List<LogLine> n2lb = node2 != null ? node2.getLinesBefore() : new ArrayList<>();
        List<LogLine> n1la = node1 != null ? node1.getLinesAfter() : new ArrayList<>();
        List<LogLine> n2la = node2 != null ? node2.getLinesAfter() : new ArrayList<>();
        enrichIdx(n1lb, n2lb, lb);
        enrichIdx(n1la, n2la, la);
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

    private void enrichIdx(List<LogLine> node1LogLines, List<LogLine> node2LogLines, final List<LineAction> lineActions){
//        if(logLines == null){
//            return;
//        }

        for(LineAction la: lineActions){
            int idx = 0;
            if(la.getType() == LineActionType.ADD){
                idx = la.getPositionAfter() - 1;
                if(node2LogLines != null && node2LogLines.size() >= idx){
                    la.setLineIdx(node2LogLines.get(idx).getLineIndex());
                }
            }
            if(la.getType() == LineActionType.DELETE){
                idx = la.getPositionBefore() -1;
                if(node1LogLines != null && node1LogLines.size() >= idx){
                    la.setLineIdx(node1LogLines.get(idx).getLineIndex());
                }
            }
            if(la.getType() == LineActionType.UPDATE){
                idx = la.getPositionAfter() -1;
                if(node2LogLines != null && node2LogLines.size() >= idx){
                    la.setLineIdx(node2LogLines.get(idx).getLineIndex());
                }
            }
            if(la.getType() == LineActionType.MOVE){
                idx = la.getPositionAfter() -1;
                if(node2LogLines != null && node2LogLines.size() >= idx){
                    la.setLineIdx(node2LogLines.get(idx).getLineIndex());
                }
            }


//            if(la.getPositionBefore() != 0){
//                if(la.getPositionAfter() != 0){
//                    idx = (la.getPositionBefore()+la.getPositionAfter()/2);
//                }else{
//                    idx = la.getPositionBefore();
//                }
//            }else{
//                idx = la.getPositionAfter();
//            }
//            if(idx >= logLines.size()){
//                idx = logLines.size() -1 ;
//            }
//            if(idx >= 0 && logLines.size() > 0){
//                la.setLineIdx(logLines.get(idx).getLineIndex());
//            }

        }
    }

}
