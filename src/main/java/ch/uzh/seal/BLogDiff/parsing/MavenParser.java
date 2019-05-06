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

    @Override
    public Component parse(LogLine[] input) {
        int firstModule = getIndexOfNextModule(input,0);
        return MvnComponent.builder()
                .linesBeforeModules(subArrayToList(input, 0, firstModule))
                .modules(mapModules(input, firstModule, input.length))
                .build();
    }

    private List<Module> mapModules(LogLine[] lines, int start, int end ) {
        int processingIndex = start;
        List<Module> modules = new ArrayList<>();
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


    private Module mapModule(LogLine[] lines, int start, int end) {
        return Module.builder()
                .linesBeforeGoals(subArrayToList(lines, start, getIndexOfNextGoal(lines,start, end)))
                .goals(mapGoals(lines, start, end))
                .name(extractModuleName(lines[start+1].getContent()))
                .build();
    }

    private String extractModuleName(String logLine){
        Pattern pattern = Pattern.compile("(Building ((\\w|\\d|\\.|\\-)*(\\s|\\.|\\-)*)*)");
        Matcher matcher = pattern.matcher(logLine);
        if (matcher.find())
        {
           return matcher.group(1).replace("Building ", "");
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

    private List<Goal> mapGoals(LogLine[] lines, int start, int end ) {
        int processingIndex = start;
        List<Goal> goals = new ArrayList<>();
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

    private Goal mapGoal(LogLine[] lines, int start, int end) {
        return Goal.builder().plugin(extractPlugin(lines[start].getContent())).goal(extractGoal(lines[start].getContent())).logLines(subArrayToList(lines,start,end)).build();
    }

    private String extractPlugin(String logLine){

        Pattern pattern = Pattern.compile("([a-zA-Z-_]*:(\\d\\.?)*:)");
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
            if(lines[i].getContent().matches("(.*---\\s[a-zA-Z-_]*:(\\d\\.?)*:[a-zA-Z-_]*\\s*.*@\\s*[a-zA-Z-_]*\\s*---.*)")){
                return i;
            }
        }
        return -1;
    }

    private List<LogLine> mapRaw(String[] lines, int start,  int end){
        List<LogLine> logLines = new ArrayList<>();
        for(int i = start; i < end; i++){
            logLines.add(LogLine.builder().content(lines[i]).build());
        }
        return logLines;
    }

    private List<LogLine> subArrayToList(LogLine[] lines, int start,  int end){
        List<LogLine> logLines = new ArrayList<>();
        for(int i = start; i < end; i++){
            logLines.add(lines[i]);
        }
        return logLines;
    }


}
