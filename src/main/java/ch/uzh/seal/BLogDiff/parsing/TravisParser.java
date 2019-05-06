package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.Component;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;
import ch.uzh.seal.BLogDiff.model.parsing.MvnComponent;
import ch.uzh.seal.BLogDiff.model.parsing.TravisComponent;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class TravisParser implements Parser {

    @Override
    public Component parse(LogLine[] input) {
       return TravisComponent.builder().logLines(subArrayToList(input, 0, input.length)).build();
    }

    private List<LogLine> mapRaw(String[] lines, int start, int end){
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
