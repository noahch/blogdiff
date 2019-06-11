package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.*;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Component
public class TravisParser implements Parser {

    @Override
    public BuildLogNode parse(LogLine[] linesBefore, LogLine[] linesAfter) {
       return BuildLogNode.builder()
               .linesBefore(subArrayToList(linesBefore, 0, linesBefore.length))
               .linesAfter(subArrayToList(linesAfter, 0, linesAfter.length))
               .nodeName("Travis")
               .build();
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
