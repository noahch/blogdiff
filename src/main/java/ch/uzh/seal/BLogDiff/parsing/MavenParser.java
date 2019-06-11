package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Component
@Slf4j
public class MavenParser implements Parser {

    private List<String> moduleNames;

    @Override
    public BuildLogNode parse(LogLine[] linesBefore, LogLine[] linesAfter) {
        moduleNames = new ArrayList<>();
        int firstModule = getIndexOfNextModule(linesBefore,0);
        // No module found
        if(firstModule == -1){
            return BuildLogNode.builder()
                    .linesBefore(subArrayToList(linesBefore, 0, linesBefore.length))
                    .logNodes(null)
                    .nodeName("Maven")
                    .build();
        }
        return BuildLogNode.builder()
                .linesBefore(subArrayToList(linesBefore, 0, firstModule))
                .logNodes(mapModules(linesBefore, firstModule, linesBefore.length))
                .nodeName("Maven")
                .build();
    }

    private List<BuildLogNode> mapModules(LogLine[] lines, int start, int end ) {
        int processingIndex = start;
        List<BuildLogNode> modules = new ArrayList<>();
        while (getIndexOfNextModule(lines, processingIndex) != -1) {
            processingIndex = getIndexOfNextModule(lines, processingIndex);
            int nextIndex = getIndexOfNextModule(lines, processingIndex+1);
            if (nextIndex == -1){
                nextIndex = lines.length;
            }
            modules.add(mapModule(lines, processingIndex, nextIndex));
            processingIndex++;
        }
        return modules;
    }


    private BuildLogNode mapModule(LogLine[] lines, int start, int end) {
        return BuildLogNode.builder()
                .linesBefore(subArrayToList(lines, start, getIndexOfNextGoal(lines,start, end)))
                .logNodes(mapGoals(lines, start, end))
                .nodeName(extractModuleName(lines[start+1].getContent()))
                .build();
    }

    private String extractModuleName(String logLine){
        Pattern pattern = Pattern.compile("(Building ((\\w|\\d|\\.|\\-)*(\\s|\\.|\\-)*)*)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find())
        {
           String moduleName = matcher.group(1).replace("Building ", "");
           if(moduleNames.contains(moduleName)){
               moduleName = moduleName + " 1";
           }
           moduleNames.add(moduleName);
           return moduleName;

        }
        log.warn("Module name could not be extracted.");
        return logLine;
    }

    private int getIndexOfNextModule(LogLine[] lines, int start){
        for(int i = start; i < lines.length - 2; i++){
            if(lines[i].getContent().matches("(.*\\-+\\W\\-+.*)") &&
                    lines[i+1].getContent().matches("(.*Building.*)") &&
                    lines[i+2].getContent().matches("(.*\\-+\\W\\-+.*)")) {
                return i;
            }
        }
        return -1;
    }

    private List<BuildLogNode> mapGoals(LogLine[] lines, int start, int end ) {
        int processingIndex = start;
        List<BuildLogNode> goals = new ArrayList<>();
        while (getIndexOfNextGoal(lines, processingIndex, end) != -1) {
            processingIndex = getIndexOfNextGoal(lines, processingIndex, end);
            int nextIndex = getIndexOfNextGoal(lines, processingIndex+1, end);
            if (nextIndex == -1){
                nextIndex = end;
            }
            goals.add(mapGoal(lines, processingIndex, nextIndex));
            processingIndex++;
        }
        return goals;
    }

    private BuildLogNode mapGoal(LogLine[] lines, int start, int end) {
        return BuildLogNode.builder().nodeName(extractPlugin(lines[start].getContent()) + " " + extractGoal(lines[start].getContent()))
                .linesBefore(subArrayToList(lines,start,end)).build();
    }

    private String extractPlugin(String logLine){

        Pattern pattern = Pattern.compile("([a-zA-Z-_]*:(\\d\\.?)*.*:)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find())
        {
            return matcher.group(1).substring(0, matcher.group(1).length()-1);
        }
        log.warn("Plugin name could not be extracted.");
        return logLine;
    }

    private String extractGoal(String logLine){

        Pattern pattern = Pattern.compile("(:[a-zA-Z-_]+)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find())
        {
            return matcher.group(1).substring(1);
        }
        log.warn("Plugin goal could not be extracted.");
        return logLine;
    }

    private int getIndexOfNextGoal(LogLine[] lines, int start, int end){
        for(int i = start; i < end; i++){
            if(lines[i].getContent().matches("(.*---\\s[a-zA-Z-_]*:(\\d\\.?)*.*:[a-zA-Z-_]*\\s*.*@\\s*[a-zA-Z-_]*\\s*---.*)")){
                return i;
            }
        }
        return -1;
    }


    private List<LogLine> subArrayToList(LogLine[] lines, int start,  int end){
        List<LogLine> logLines = new ArrayList<>();
        int internalIdx = 1;
        for(int i = start; i < end; i++){
            lines[i].setInternalLineIndex(internalIdx);
            logLines.add(lines[i]);
            internalIdx++;
        }
        return logLines;
    }


}
