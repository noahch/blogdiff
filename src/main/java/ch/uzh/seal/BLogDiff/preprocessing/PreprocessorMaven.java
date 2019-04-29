package ch.uzh.seal.BLogDiff.preprocessing;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessorMaven implements Preprocessor {

    public String process(String buildLog, String replacement) {
        String[] lines = buildLog.split("(\\r?\\n)|\\r");
        StringBuffer sb = new StringBuffer();
        String prevLine = "";
        for (int i = 0; i < lines.length; i++){
            String s = filterDownloadSpeed(lines[i], replacement);
            s = filterTotalTime(s, replacement);
            s = filterFinishedAt(s, replacement);
            s = filterFinalMemeory(s, replacement);
            s = filterProgress(s, prevLine, replacement);
            s = filterTime(s, replacement);
            s = filterTimestamp(s, replacement);
            prevLine = lines[i];
            if (!s.equals("")){
                s  += "\n";
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private String filterDownloadSpeed(String line, String replacement){
        return line.replaceAll("([0-9]*\\.*[0-9]*\\s[kKmMgG]*B/s)", replacement);
    }

    private String filterTotalTime(String line, String replacement) {
        if (line.startsWith("[INFO] Total time:")) {
            return "[INFO] Total time: " + replacement;
        }
        return line;
    }

    private String filterFinishedAt(String line, String replacement) {
        if (line.startsWith("[INFO] Finished at:")) {
            return "[INFO] Finished at: " + replacement;
        }
        return line;
    }

    private String filterFinalMemeory(String line, String replacement) {
        if (line.startsWith("[INFO] Final Memory:")) {
            return "[INFO] Final Memory: " + replacement;
        }
        return line;
    }

    private String filterProgress(String line, String prevLine, String replacement) {
        if(line.startsWith("Progress")){

            if (line.matches("(\\s*Progress \\(.\\):.*)") && !prevLine.matches("(\\s*Progress \\(.\\):.*)")) {
                return "Progress " + replacement;
            }else if (prevLine.matches("(\\s*Progress \\(.\\):.*)")){
                return "";
            }
        }
        return line;
    }

    private String filterTime(String line, String replacement) {
        return line.replaceAll("([0-9]*.?[0-9]+.?(ms|s|sec)(\\s|\\W|$))", replacement);
    }
    private String filterTimestamp(String line, String replacement) {
        return line.replaceAll("([0-9]{2}:){2}[0-9]{2}.[0-9]{1,3}\\s", replacement);
    }
}
