package ch.uzh.seal.BLogDiff.parsing;

import ch.uzh.seal.BLogDiff.model.parsing.BuildLog;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.seal.BLogDiff.model.parsing.Component;

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
    public BuildLog parse(String buildLog) {
        String[] array = buildLog.split("travis_fold:end:git.checkout");

        String travis = array[0];
        List<LogLine> travisLines = new ArrayList<>();
        String[] travisArray = travis.split("\n");
        for(int i = 0; i < travisArray.length; i++){
            travisLines.add(mapToLogLine(travisArray[i], i+1));
        }

        String mvn = array[1];
        List<LogLine> mavenLines = new ArrayList<>();
        String[] mavenArray = mvn.split("\n");
        int mvnIdx = travisArray.length + 1;
        for(int i = 0; i < mavenArray.length; i++){
            mavenLines.add(mapToLogLine(mavenArray[i],  mvnIdx));
            mvnIdx++;
        }

        return BuildLog.builder()
                .buildComponent(mavenParser.parse(mavenLines.toArray(new LogLine[mavenLines.size()])))
                .ciComponent(travisParser.parse(travisLines.toArray(new LogLine[travisLines.size()])))
                .build();
    }

    private LogLine mapToLogLine(String line, int idx){
        return LogLine.builder().content(line).lineIndex(idx).build();
    }
}
