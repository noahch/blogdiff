package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.mapping.NodeLevelMapper;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.service.TravisService;
import ch.uzh.seal.BLogDiff.utils.EditTreeUtils;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Component
@RestController
public class PreprocessingAnalysis {


    @Autowired
    private TravisService travisService;

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @RequestMapping("/analysis/verifyPreprocessing")
    public void verifyPreprocessing(){
        String[][] jobs =  {
                            {"526313909","527008457"}, //1
                            {"526323134","527009122"}, //2
                            {"526898193","527009313"}, //3
                            {"526900824","526905479"}, //4
                            {"526910311","526911622"}, //5
                            {"526922964","526923460"}, //6
                            {"526918182","526919514"}, //7
                            {"526923320","526930055"}, //8
                            {"526931972","526937400"}, //9
                            {"526932987","527009658"}, //10
                            };

        StringBuilder sb = new StringBuilder();
        sb.append("Type;Number;Log\n");

        for(int i = 0; i < jobs.length; i++){
            String j1 = jobs[i][0];
            String j2 = jobs[i][1];
            log.info("Started with " +j1+"_"+j2+" no filter");
            double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
            double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
            double length = max(l1,l2);

            BuildLogTree tree1 = travisService.getBuildLogTree(j1,false);
            BuildLogTree tree2 = travisService.getBuildLogTree(j2,false);
            EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());

            double a = EditTreeUtils.getAdditions(editTree);
            double d = EditTreeUtils.getDeletions(editTree);
            double m = EditTreeUtils.getMoves(editTree);
            double u = EditTreeUtils.getUpdates(editTree);
            length += max(a,d);

            sb.append("AB;"+a/length+";"+j1+"_"+j2+"\n");
            sb.append("DB;"+d/length+";"+j1+"_"+j2+"\n");
            sb.append("MB;"+m/length+";"+j1+"_"+j2+"\n");
            sb.append("UB;"+u/length+";"+j1+"_"+j2+"\n");
            sb.append("SB;"+(a+d+m+u)/length+";"+j1+"_"+j2+"\n");


//        log.info("------------------------ no filter ---------------------");
//        log.info("A: "+ a);
//        log.info("D: "+ d);
//        log.info("M: "+ m);
//        log.info("U: "+ u);
//        log.info("SUM: " + (a+d+m+u));
            log.info("Started with " +j1+"_"+j2+" filter");
            tree1 = travisService.getBuildLogTree(j1,true);
            tree2 = travisService.getBuildLogTree(j2,true);
            editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
            a = EditTreeUtils.getAdditions(editTree);
            d = EditTreeUtils.getDeletions(editTree);
            m = EditTreeUtils.getMoves(editTree);
            u = EditTreeUtils.getUpdates(editTree);
//        log.info("------------------------ filter ---------------------");
//        log.info("A: "+ a);
//        log.info("D: "+ d);
//        log.info("M: "+ m);
//        log.info("U: "+ u);
//        log.info("SUM: " + (a+d+m+u));
            sb.append("AA;"+a/length+";"+j1+"_"+j2+"\n");
            sb.append("DA;"+d/length+";"+j1+"_"+j2+"\n");
            sb.append("MA;"+m/length+";"+j1+"_"+j2+"\n");
            sb.append("UA;"+u/length+";"+j1+"_"+j2+"\n");
            sb.append("SA;"+(a+d+m+u)/length+";"+j1+"_"+j2+"\n");

        }

        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\r\\", "verifyProcessing.txt", sb.toString());

    }

    @RequestMapping("/analysis/verifyThreshold")
    public void verifyThreshold(){

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
                {"543128552","543716155"}, // apache/struts
                {"499807007","499816460"}, // spring-projects/spring-data-jpa
                {"339309648","375182522"}, // Nukkit/Nukkit
                {"529851442","532916599"}, // google/compile-testing
                {"445185673","445783150"}, // tumblr/jumblr
                {"545469486","547020095"}  // google/auto
        };

        double[] thresholds = {0.5,0.4,0.3,0.2,0.1};
        StringBuilder sb = new StringBuilder();
        sb.append("Threshold;A;D;U;M;Total\n");
        for(int i = 0; i < jobs.length; i++) {

            for(int t = 0; t < thresholds.length; t++){
                String j1 = jobs[i][0];
                String j2 = jobs[i][1];
                log.info("Started:" + i);
                BuildLogTree tree1 = travisService.getBuildLogTree(j1,true);
                BuildLogTree tree2 = travisService.getBuildLogTree(j2,true);
                EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer(thresholds[t]));
                double a = EditTreeUtils.getAdditions(editTree);
                double d = EditTreeUtils.getDeletions(editTree);
                double m = EditTreeUtils.getMoves(editTree);
                double u = EditTreeUtils.getUpdates(editTree);
                sb.append(thresholds[t]+";"+a+";"+d+";"+u+";"+m+";"+(a+m+d+u)+"\n");

            }

        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "nm_thresholds.txt", sb.toString());


    }

    @RequestMapping("/analysis/series")
    public void verifySeries(){
        String[][] jobs = {
                                {"543716155","540589755","538213481","532276588","525944159"},
                                {"562735148","558129364","555936615","551622624","550999527"},
                                {"445783150","445185673","445153479","445152588","445071115"}
                            };

        StringBuilder sb = new StringBuilder();
        sb.append("Run;Dist;A;D;U;M;Total;Len\n");

        for(int i = 0; i < jobs.length; i++){
            for(int j = 1; j < jobs[i].length; j++){
                String j1 = jobs[i][0];
                String j2 = jobs[i][j];
                log.info("Started:" + i);
                double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
                double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
                double length = max(l1,l2);
                BuildLogTree tree1 = travisService.getBuildLogTree(j2,true);
                BuildLogTree tree2 = travisService.getBuildLogTree(j1,true);
                EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
                double a = EditTreeUtils.getAdditions(editTree);
                double d = EditTreeUtils.getDeletions(editTree);
                double m = EditTreeUtils.getMoves(editTree);
                double u = EditTreeUtils.getUpdates(editTree);
                sb.append(i+";"+j+";"+a+";"+d+";"+u+";"+m+";"+(a+m+d+u)+";"+length+"\n");
            }
        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "series.txt", sb.toString());


        sb = new StringBuilder();
        sb.append("Run;Dist;A;D;U;M;Total;Len\n");

        for(int i = 0; i < jobs.length; i++){
            for(int j = 1; j < jobs[i].length; j++){
                String j1 = jobs[i][j-1];
                String j2 = jobs[i][j];
                log.info("Started:" + i);
                double l1 = travisService.getLog(j1).getContent().split("(\\r?\\n)|\\r").length;
                double l2 = travisService.getLog(j2).getContent().split("(\\r?\\n)|\\r").length;
                double length = max(l1,l2);
                BuildLogTree tree1 = travisService.getBuildLogTree(j2,true);
                BuildLogTree tree2 = travisService.getBuildLogTree(j1,true);
                EditTree editTree = nodeLevelMapper.map(tree1, tree2, new LineDifferencer());
                double a = EditTreeUtils.getAdditions(editTree);
                double d = EditTreeUtils.getDeletions(editTree);
                double m = EditTreeUtils.getMoves(editTree);
                double u = EditTreeUtils.getUpdates(editTree);
                sb.append(i+";"+j+";"+a+";"+d+";"+u+";"+m+";"+(a+m+d+u)+";"+length+"\n");
            }
        }
        FileUtils.writeToSb("C:\\Users\\noahc\\Google Drive\\uzh_s06\\BA\\data\\", "seriesSuc.txt", sb.toString());
    }
//
//    private static String inputDirGh;
//
//    @Value("${analysis.inputDirGithub}")
//    public void setInputDirGh(String input) {
//        inputDirGh = input;
//    }
//
//    private static String inputDirTr;
//
//    @Value("${analysis.inputDirTravis}")
//    public void setInputDirTr(String input) {
//        inputDirTr = input;
//    }
//    private static String outputDir;
//
//    @Value("${analysis.outputDir}")
//    public void setOutputDir(String output) {
//        outputDir = output;
//    }
//
//    public static void analysePreprocessing(String buildLog1, String buildLog2, String buildLog1p, String buildLog2p){
//       log.info("ANALYZING BASE");
//       compareLogs(logToArray(buildLog1), logToArray(buildLog2));
//       log.info("ANALYZING FILTERED");
//       compareLogs(logToArray(buildLog1p), logToArray(buildLog2p));
//    }
//
//    public static String[] logToArray(String log){
//        return log.split("(\\r?\\n)|\\r");
//    }
//
//    public static void compareLogs(String[] l1, String[] l2){
//        if (l1.length > l2.length) {
//            String[] tmp = l2;
//            l2 = l1;
//            l1 = tmp;
//        }
//        Arrays.sort(l1);
//        Arrays.sort(l2);
//        int differences = 0;
//        for (int i = 0; i < l1.length; i++){
//            if(!l1[i].equals(l2[i])){
//                differences++;
//            }
//        }
//        log.info("FOUND " + differences + " differences");
//    }
//
//    public static void analyseInputDir() {
//        analyseDir(inputDirGh, 20);
//        analyseDir(inputDirTr, 10);
//
//    }
//
//    private static void analyseDir(String directory, int iterations) {
//        File dir = new File(directory);
//        File[] files = dir.listFiles();
//
//        for (int i = 0; i < iterations; i++){
//            Random rand = new Random();
//            File file = files[rand.nextInt(files.length)];
//            if(file.isDirectory()){
//                String currDir = dir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName();
//                String newDir = outputDir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName().split("_")[1] + File.separator;
//                String log = FileUtils.readFile(currDir + File.separator +  "passed.txt");
//                String processedLog = preprocessorHandler.preprocessLog(log);
//                FileUtils.writeToSb(newDir, "log.txt", log);
//                FileUtils.writeToSb(newDir, "log_processed.txt", processedLog);
//            }
//            log.info(file.getName());
//        }
//    }
//
//    public static void reAnalyseDir() {
//        File dir = new File(outputDir);
//        File[] files = dir.listFiles();
//
//        for (int i = 0; i < files.length; i++){
//            File file = files[i];
//            if(file.isDirectory()){
//                String fdir = outputDir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName() + File.separator;
//                String log = FileUtils.readFile(fdir + "log.txt");
//                String processedLog = preprocessorHandler.preprocessLog(log);
//                FileUtils.writeToSb(fdir, "log_reProcessed.txt", processedLog);
//            }
//        }
//    }
    public static double max(double a, double b){
        if(a < b){
            return b;
        }
        return a;
    }
}
