package ch.uzh.seal.BLogDiff.preprocessing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessorTravisCI implements Preprocessor{


    public String process(String buildLog, String replacement) {
        String[] lines = buildLog.split("(\\r?\\n)|\\r");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < lines.length; i++){
            String s = filterHostname(lines[i], replacement);
            s = filterInstance(s, replacement);
            s = filterStartup(s, replacement);
            s = filterTravisBuildVersion(s, replacement);
            s = filterTravisTimeTags(s, replacement);
            s  += "\n";
            sb.append(s);
        }
        return sb.toString();
    }

    private String filterHostname(String line, String replacement) {
        if (line.startsWith("hostname:")) {
            return "hostname: " + replacement;
        }
        return line;
    }

    private String filterInstance(String line, String replacement) {
        if (line.startsWith("instance:")) {
            return "instance: " + replacement;
        }
        return line;
    }

    private String filterStartup(String line, String replacement) {
        if (line.startsWith("startup:")) {
            return "startup: " + replacement;
        }
        return line;
    }

    private String filterTravisBuildVersion(String line, String replacement) {
        if (line.startsWith("travis-build version:")) {
            return "travis-build version: " + replacement;
        }
        return line;
    }

    private String filterTravisTimeTags(String line, String replacement) {
        if (line.contains("travis_time:")) {
            return "travis_time: " + replacement;
        }
        return line;
    }

}
