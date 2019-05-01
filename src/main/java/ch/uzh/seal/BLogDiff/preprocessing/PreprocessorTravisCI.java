package ch.uzh.seal.BLogDiff.preprocessing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreprocessorTravisCI implements Preprocessor{


    public String process(String buildLog, String replacement) {
        String[] lines = buildLog.split("(\\r?\\n)|\\r");
        StringBuilder sb = new StringBuilder();
        String prevLine = "";
        for (int i = 0; i < lines.length; i++){
            String s = filterHostname(lines[i], replacement);
            s = filterInstance(s, replacement);
            s = filterStartup(s, replacement);
            s = filterGitDownload(s, prevLine, replacement);
            s = filterDownloadProgress(s, prevLine, replacement);
            s = filterTravisBuildVersion(s, replacement);
            s = filterDownload(s, replacement);
            s = filterTravisTimeTags(s, replacement);
            prevLine = lines[i];
            if (!s.equals("")){
                s  += "\n";
            }
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

    private String filterGitDownload(String line, String prevLine, String replacement) {
        if(line.startsWith("remote: Counting objects:")){
            if(prevLine.startsWith("remote: Counting objects:")){
                return "";
            }else{
                return "remote: Counting objects: " + replacement;
            }
        }
        if(line.startsWith("remote: Compressing objects:")){
            if(prevLine.startsWith("remote: Compressing objects:")){
                return "";
            }else{
                return "remote: Compressing objects: " + replacement;
            }
        }
        if(line.startsWith("Receiving objects:")){
            if(prevLine.startsWith("Receiving objects:")){
                return "";
            }else{
                return "Receiving objects: " + replacement;
            }
        }
        if(line.startsWith("Resolving deltas:")){
            if(prevLine.startsWith("Resolving deltas:")){
                return "";
            }else{
                return "Resolving deltas: " + replacement;
            }
        }
        if(line.startsWith("(Reading database ...")){
            if(prevLine.startsWith("(Reading database ...")){
                return "";
            }else{
                return "(Reading database ... " + replacement;
            }
        }
        if(line.startsWith("Reading package lists...")){
            if(prevLine.startsWith("Reading package lists...")){
                return "";
            }else{
                return "Reading package lists... " + replacement;
            }
        }
        if(line.startsWith("Building dependency tree...")){
            if(prevLine.startsWith("Building dependency tree...")){
                return "";
            }else{
                return "Building dependency tree... " + replacement;
            }
        }
        if(line.startsWith("Reading state information...")){
            if(prevLine.startsWith("Reading state information...")){
                return "";
            }else{
                return "Reading state information... " + replacement;
            }
        }
        if(line.startsWith("Unpacking objects:")){
            if(prevLine.startsWith("Unpacking objects:")){
                return "";
            }else{
                return "Unpacking objects: " + replacement;
            }
        }
        return line;
    }

    private String filterDownloadProgress(String line, String prevLine, String replacement) {
            if (line.matches("(\\s*\\d+K(\\s.+)+\\s*\\d+%\\s*\\d+\\.?\\d*(M|K).*)") && !prevLine.matches("(\\s*\\d+K(\\s.+)+\\s*\\d+%\\s*\\d+\\.?\\d*(M|K).*)")) {
                return "Downloading " + replacement;
            }else if (prevLine.matches("(\\s*\\d+K(\\s.+)+\\s*\\d+%\\s*\\d+\\.?\\d*(M|K).*)")){
                return "";
            }
        return line;
    }

    private String filterDownload(String line, String replacement){
        return line.replaceAll("(\\s*\\d+%\\s*\\[.*\\d+(\\.\\d+|\\,\\d+)?\\s(B|kB|MB)\\/\\d+(\\.\\d+|\\,\\d+)?\\s(B|kB|MB)\\s*\\d+%\\])", replacement);
    }

}
