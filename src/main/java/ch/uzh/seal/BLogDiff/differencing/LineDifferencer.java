package ch.uzh.seal.BLogDiff.differencing;

import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.model.parsing.LineAction;
import ch.uzh.seal.BLogDiff.model.parsing.LineActionType;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LineDifferencer implements Differencer {

    public List<LineAction> diffLogLines(List<LogLine> lines1, List<LogLine> lines2) {
        String[] l1 = lines1 == null ? new String[]{} : lines1.stream().map(logLine -> {return logLine.getContent();}).collect(Collectors.toList()).toArray(new String[0]);
        String[] l2 = lines2 == null ? new String[]{} : lines2.stream().map(logLine -> {return logLine.getContent();}).collect(Collectors.toList()).toArray(new String[0]);
        return diff(l1, l2);
    }

    private List<LineAction> diff(String[] lines1, String[] lines2) {
        List<LineAction> actions = new ArrayList<>();
        int i = 0;
        int j = 0;
        outer: while(i < lines1.length){
            while(j < lines2.length) {
                if(lines1[i].equals(lines2[j])){
                    log.info(String.format("Index %d %s and %d %s matched", i,lines1[i] ,j ,lines2[j]));
                    i++;
                    j++;
                    continue outer;
                }else{
                    // Check similarity --> Update
                    if(similarity(lines1[i], lines2[j]) <= 0.15){
                        log.info(String.format("Index %d %s and %d %s matched as update", i,lines1[i] ,j ,lines2[j]));
                        actions.add(LineAction.builder()
                                .contentBefore(lines1[i])
                                .contentAfter(lines2[j])
                                .positionBefore(i+1)
                                .positionAfter(j+1)
                                .type(LineActionType.UPDATE).build());
                        i++;
                        j++;
                        continue outer;
                    }

                    // Search in lines2 for a match -> no match --> DELETE
                    boolean found2 = false;
                    for(int x = j; x < lines2.length; x++){
                        if(lines1[i].equals(lines2[x])){ //|| similarity(lines1[i], lines2[x]) <= 0.25){
                            found2 = true;
                            break;
                        }
                    }

                    // Search in lines1 for a match -> no match --> ADD
                    boolean found1 = false;
                    if(!found2){
                        for(int x = i; x < lines1.length; x++){
                            if(lines2[j].equals(lines1[x])){ //||similarity(lines1[x], lines2[j]) <= 0.25){
                                found1 = true;
                                break;
                            }
                        }
                    }


                    if(!found2 && found1){
                        log.info(String.format("DELETE %s", lines1[i]));
                        actions.add(LineAction.builder()
                                .contentBefore(lines1[i])
                                .contentAfter("")
                                .positionBefore(i+1)
                                .positionAfter(0)
                                .type(LineActionType.DELETE).build());
                        i++;
                        continue outer;
                    }else if (found2 && !found1) {
                        log.info(String.format("ADD %s", lines2[j]));
                        actions.add(LineAction.builder()
                                .contentBefore("")
                                .contentAfter(lines2[j])
                                .positionBefore(0)
                                .positionAfter(j+1)
                                .type(LineActionType.ADD).build());
                        j++;
                        continue;
                    }else if (!found1 && !found2){
                        log.info(String.format("xDELETE %s", lines1[i]));
                        log.info(String.format("xADD %s", lines2[j]));
                        actions.add(LineAction.builder()
                                .contentBefore(lines1[i])
                                .contentAfter("")
                                .positionBefore(i+1)
                                .positionAfter(0)
                                .type(LineActionType.DELETE).build());
                        actions.add(LineAction.builder()
                                .contentBefore("")
                                .contentAfter(lines2[j])
                                .positionBefore(0)
                                .positionAfter(j+1)
                                .type(LineActionType.ADD).build());
                        i++;
                        j++;
                        continue outer;

                    }
                }
                j++;
            }
            if(j < lines2.length){
                i++;
            }else{
                break;
            }
        }
        while(j < lines2.length){
            log.info("ADD");
            actions.add(LineAction.builder()
                    .contentBefore("")
                    .contentAfter(lines2[j])
                    .positionBefore(0)
                    .positionAfter(j+1)
                    .type(LineActionType.ADD).build());
            j++;
        }
        while(i < lines1.length){
            log.info("DELETE");
            actions.add(LineAction.builder()
                    .contentBefore(lines1[i])
                    .contentAfter("")
                    .positionBefore(i+1)
                    .positionAfter(0)
                    .type(LineActionType.DELETE).build());
            i++;
        }

        List<LineAction> alreadyMapped = new ArrayList<>();
        List<LineAction> newActions = new ArrayList<>();

        actions.forEach(lineAction -> {
            if(lineAction.getType() == LineActionType.ADD && !alreadyMapped.contains(lineAction)){
                LineAction action = actions.stream().filter(lineAction2 -> {
                    return lineAction2.getType() == LineActionType.DELETE
                            && lineAction.getContentAfter().equals(lineAction2.getContentBefore())
                            && !alreadyMapped.contains(lineAction2);
                }).findFirst().orElse(null);
                if(action != null){
                    alreadyMapped.add(lineAction);
                    alreadyMapped.add(action);
                    newActions.add(LineAction.builder()
                            .type(LineActionType.MOVE)
                            .positionBefore(action.getPositionBefore())
                            .positionAfter(lineAction.getPositionAfter())
                            .contentBefore(lineAction.getContentAfter())
                            .contentAfter(action.getContentBefore())
                            .build());
                }
            }
            if(lineAction.getType() == LineActionType.DELETE && !alreadyMapped.contains(lineAction)){
                LineAction action = actions.stream().filter(lineAction2 -> {
                    return lineAction2.getType() == LineActionType.ADD
                            && lineAction.getContentBefore().equals(lineAction2.getContentAfter())
                            && !alreadyMapped.contains(lineAction2);
                }).findFirst().orElse(null);
                if(action != null){
                    alreadyMapped.add(lineAction);
                    alreadyMapped.add(action);
                    newActions.add(LineAction.builder()
                            .type(LineActionType.MOVE)
                            .positionBefore(action.getPositionBefore())
                            .positionAfter(lineAction.getPositionAfter())
                            .contentBefore(lineAction.getContentBefore())
                            .contentAfter(action.getContentAfter())
                            .build());
                }
            }

        });

        alreadyMapped.forEach(lineAction -> {
            actions.remove(lineAction);
        });
        newActions.forEach(lineAction -> {
            actions.add(lineAction);
        });


        return actions;
    }

    static double similarity(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        int len = dp[x.length()][y.length()];

        return (double)len/((x.length()+y.length())/2);
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
