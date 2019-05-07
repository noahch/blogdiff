package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class TravisParser implements Parser {

    @Override
    public BuildLogNode parse(LogLine[] input) {
       return BuildLogNode.builder().linesBefore(subArrayToList(input, 0, input.length)).nodeName("Travis").build();
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
