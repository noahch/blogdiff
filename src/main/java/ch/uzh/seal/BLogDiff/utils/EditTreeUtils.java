package ch.uzh.seal.BLogDiff.utils;

import ch.uzh.seal.BLogDiff.model.parsing.EditAction;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import ch.uzh.seal.BLogDiff.model.parsing.LineActionType;

public class EditTreeUtils {

    public static long getAdditions(EditTree editTree){
        long childActionCount = 0;
        for(EditAction editAction1: editTree.getChildrenActions()){
            childActionCount += getAdditionsRec(editAction1, LineActionType.ADD);
        }
        return childActionCount;
    }

    public static long getDeletions(EditTree editTree){
        long childActionCount = 0;
        for(EditAction editAction1: editTree.getChildrenActions()){
            childActionCount += getAdditionsRec(editAction1, LineActionType.DELETE);
        }
        return childActionCount;
    }

    public static long getMoves(EditTree editTree){
        long childActionCount = 0;
        for(EditAction editAction1: editTree.getChildrenActions()){
            childActionCount += getAdditionsRec(editAction1,LineActionType.MOVE);
        }
        return childActionCount;
    }

    public static long getUpdates(EditTree editTree){
        long childActionCount = 0;
        for(EditAction editAction1: editTree.getChildrenActions()){
            childActionCount += getAdditionsRec(editAction1, LineActionType.UPDATE);
        }
        return childActionCount;
    }

    private static long getAdditionsRec(EditAction editAction, LineActionType type){
        if(editAction.getChildrenActions() == null || editAction.getChildrenActions().size() < 1){
            return editAction.getLinesBeforeActions().stream().filter(lineAction -> lineAction.getType() == type).count()
                    + editAction.getLinesAfterActions().stream().filter(lineAction -> lineAction.getType() == type).count();
        }
        long childActionCount = 0;
        for(EditAction editAction1: editAction.getChildrenActions()){
            childActionCount += getAdditionsRec(editAction1, type);
        }
        return editAction.getLinesBeforeActions().stream().filter(lineAction -> lineAction.getType() == type).count()
                + editAction.getLinesAfterActions().stream().filter(lineAction -> lineAction.getType() == type).count()
                + childActionCount;

    }
}
