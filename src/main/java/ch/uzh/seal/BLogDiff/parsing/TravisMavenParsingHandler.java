package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.exception.ParseException;
import ch.uzh.seal.BLogDiff.model.parsing.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Component
public class TravisMavenParsingHandler implements ParsingHandler {

    @Autowired
    MavenParser mavenParser;

    @Autowired
    TravisParser travisParser;

    @Override
    public BuildLogTree parse(String buildLog, boolean isFiltered) {
        try {
            // Spilt the build log into travis and maven parts
            String[] splitArray = buildLog.split("(?<=travis_fold:end:git.checkout)");

            // Initialise nodeList for resulting BuildLogTree
            List<BuildLogNode> nodes = new ArrayList<>();

            // Initialise LogLine Lists
            List<LogLine> travisLinesBefore = new ArrayList<>();
            List<LogLine> travisLinesAfter = new ArrayList<>();
            List<LogLine> mavenLines = new ArrayList<>();

            // Travis Maven Split successful
            if(splitArray.length > 1){

                travisLinesBefore = mapLinesToList(splitArray[0].split("\n"), 1);

                String restLines = splitArray[1];

                String[] splitArray2 = restLines.split("\n");
                int splitIdx = getIndexOfLastMavenLine(splitArray2, isFiltered);

                // Travis lines after maven found
                if(splitIdx != -1){
                    mavenLines = mapLinesToList(ArrayUtils.subarray(splitArray2, 0, splitIdx+1), travisLinesBefore.size() + 1);
                    travisLinesAfter = mapLinesToList(ArrayUtils.subarray(splitArray2, splitIdx + 1, splitArray2.length), travisLinesBefore.size() + mavenLines.size() + 1);
                } else {
                    // No maven lines found
                    throw new ParseException("Something went wrong whilst parsing");
                }

            } else { // Travis Maven Split not successful
                travisLinesBefore = mapLinesToList(splitArray[0].split("\n"), 1);;
            }

            if(travisLinesBefore.size() > 0){

                // Parse Travis data
                BuildLogNode node = travisParser.parse(
                        travisLinesBefore.toArray(new LogLine[travisLinesBefore.size()]),
                        travisLinesAfter.toArray(new LogLine[travisLinesAfter.size()]));
                if(mavenLines.size() > 0) {
                    node.setLogNodes(Arrays.asList(mavenParser.parse(mavenLines.toArray(new LogLine[mavenLines.size()]), null)));
                } else {
                    node.setLogNodes(new ArrayList<>());
                }
                nodes.add(node);
                return BuildLogTree.builder().nodes(nodes).build();
            } else {
                throw new ParseException("Something went wrong whilst parsing");
            }

        } catch (Exception e){
            log.error(e.getMessage());
            log.error("Fallback parsing applied");
            List<BuildLogNode> nodes = new ArrayList<>();
            String[] lines = buildLog.split("\n");
            List<LogLine> logLines = new ArrayList<>();
            for(int i = 0; i < lines.length; i++){
                logLines.add(LogLine.builder().lineIndex(i).internalLineIndex(i).content(lines[i]).build());
            }
            nodes.add(BuildLogNode.builder().nodeName("UNKNOWN").linesBefore(logLines).build());
            return BuildLogTree.builder().nodes(nodes).build();
        }
    }
    private List<LogLine> mapLinesToList(String[] line, int lineNumberStartIndex){
        List<LogLine> list = new ArrayList<>();
        for(int i = 0; i < line.length; i++){
            list.add(LogLine.builder().content(line[i]).lineIndex(lineNumberStartIndex).build());
            lineNumberStartIndex++;
        }
        return list;
    }


    private int getIndexOfLastMavenLine(String[] lines, boolean isFiltered){
        for(int i = lines.length -1 ; i >= 0 ; i--){
           if(isFiltered && lines[i].matches("(\\[INFO\\]\\s*-*)")){
               return i;
           } else if(!isFiltered && lines[i].matches("(.*\\[.*INFO.*\\].*\\s*-*)")){
               return i;
           }
        }
        return -1;
    }

}
