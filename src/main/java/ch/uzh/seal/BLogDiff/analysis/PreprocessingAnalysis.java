package ch.uzh.seal.BLogDiff.analysis;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class PreprocessingAnalysis {
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
}
