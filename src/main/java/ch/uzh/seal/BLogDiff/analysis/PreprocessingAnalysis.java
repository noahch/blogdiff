package ch.uzh.seal.BLogDiff.analysis;

import ch.uzh.seal.BLogDiff.preprocessing.PreprocessorHandler;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

@Slf4j
@Component
public class PreprocessingAnalysis {





    private static PreprocessorHandler preprocessorHandler;

    @Autowired
    private PreprocessorHandler preprocessorHandler0;

    @PostConstruct
    private void initStaticPreprocessorHandler () {
        preprocessorHandler = this.preprocessorHandler0;
    }


    private static String inputDirGh;

    @Value("${analysis.inputDirGithub}")
    public void setInputDirGh(String input) {
        inputDirGh = input;
    }

    private static String inputDirTr;

    @Value("${analysis.inputDirTravis}")
    public void setInputDirTr(String input) {
        inputDirTr = input;
    }
    private static String outputDir;

    @Value("${analysis.outputDir}")
    public void setOutputDir(String output) {
        outputDir = output;
    }

    public static void analysePreprocessing(String buildLog1, String buildLog2, String buildLog1p, String buildLog2p){
       log.info("ANALYZING BASE");
       compareLogs(logToArray(buildLog1), logToArray(buildLog2));
       log.info("ANALYZING FILTERED");
       compareLogs(logToArray(buildLog1p), logToArray(buildLog2p));
    }

    public static String[] logToArray(String log){
        return log.split("(\\r?\\n)|\\r");
    }

    public static void compareLogs(String[] l1, String[] l2){
        if (l1.length > l2.length) {
            String[] tmp = l2;
            l2 = l1;
            l1 = tmp;
        }
        Arrays.sort(l1);
        Arrays.sort(l2);
        int differences = 0;
        for (int i = 0; i < l1.length; i++){
            if(!l1[i].equals(l2[i])){
                differences++;
            }
        }
        log.info("FOUND " + differences + " differences");
    }

    public static void analyseInputDir() {
        analyseDir(inputDirGh, 20);
        analyseDir(inputDirTr, 10);

    }

    private static void analyseDir(String directory, int iterations) {
        File dir = new File(directory);
        File[] files = dir.listFiles();

        for (int i = 0; i < iterations; i++){
            Random rand = new Random();
            File file = files[rand.nextInt(files.length)];
            if(file.isDirectory()){
                String currDir = dir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName();
                String newDir = outputDir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName().split("_")[1] + File.separator;
                String log = FileUtils.readFile(currDir + File.separator +  "passed.txt");
                String processedLog = preprocessorHandler.preprocessLog(log);
                FileUtils.writeFile(newDir, "log.txt", log);
                FileUtils.writeFile(newDir, "log_processed.txt", processedLog);
            }
            log.info(file.getName());
        }
    }

    public static void reAnalyseDir() {
        File dir = new File(outputDir);
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++){
            File file = files[i];
            if(file.isDirectory()){
                String fdir = outputDir + File.separator + file.getName() + File.separator + file.listFiles()[0].getName() + File.separator;
                String log = FileUtils.readFile(fdir + "log.txt");
                String processedLog = preprocessorHandler.preprocessLog(log);
                FileUtils.writeFile(fdir, "log_reProcessed.txt", processedLog);
            }
        }
    }
}
