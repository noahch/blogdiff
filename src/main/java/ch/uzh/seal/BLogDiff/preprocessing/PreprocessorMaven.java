package ch.uzh.seal.BLogDiff.preprocessing;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessorMaven implements Preprocessor {

    public String process(String buildLog, String replacement) {
        String[] lines = buildLog.split("(\\r?\\n)|\\r");
        StringBuilder sb = new StringBuilder();
        String prevLine = "";
        for (int i = 0; i < lines.length; i++){
            String s = filterDownloadSpeed(lines[i], replacement);
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
        return line.replaceAll("([0-9]*\\.*[0-9]*\\s?[kKmMgG]*B/s)", replacement);
    }

    private String filterFinalMemeory(String line, String replacement) {
        return line.replaceAll("(Final Memory: \\d+\\w\\/\\d+\\w)","Final Memory: " + replacement);
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
        return line.replaceAll("([0-9]*.?[0-9]+.?(ms|s|sec|min)(\\s|\\W|$))", replacement);
    }
    private String filterTimestamp(String line, String replacement) {
        return line.replaceAll("((\\d{4}(-\\d{2}){2}T?)?(\\d{2}:)?\\d{2}:\\d{2}Z?((\\.\\d{1,3})|(\\+\\d{2}:\\d{2}))?(\\s|\\W|$))", replacement);
    }
}
