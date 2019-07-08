package ch.uzh.seal.BLogDiff.differencing;

import ch.uzh.seal.BLogDiff.model.parsing.LineAction;
import ch.uzh.seal.BLogDiff.model.parsing.LineActionType;
import ch.uzh.seal.BLogDiff.model.parsing.LogLine;
import ch.uzh.seal.BLogDiff.utils.FileUtils;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTDifferencer implements Differencer {

    @Override
    public List<LineAction> diffLogLines(List<LogLine> lines1, List<LogLine> lines2) {
        lines1 = lines1 == null ? new ArrayList<>() : lines1;
        lines2 = lines2 == null ? new ArrayList<>() : lines2;

//        List<String> l1 = FileUtils.readFileAsList("C:\\Data\\BA\\t1.txt");
//        lines1 = new ArrayList<>();
//        int i = 1;
//        for(String s : l1) {
//            lines1.add(LogLine.builder().content(s).internalLineIndex(i).build());
//            i++;
//        }
//        List<String> l2 = FileUtils.readFileAsList("C:\\Data\\BA\\t2.txt");
//        lines2 = new ArrayList<>();
//        i = 1;
//        for(String s : l2) {
//            lines2.add(LogLine.builder().content(s).internalLineIndex(i).build());
//            i++;
//        }

        Tree src = createTree(lines1);
        Tree dst = createTree(lines2);



        Matcher matcher = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
        matcher.match();
        matcher.getMappings(); // return the mapping store
        ActionGenerator actionGenerator = new ActionGenerator(src, dst, matcher.getMappings());
        actionGenerator.generate();
        List<Action> actions = actionGenerator.getActions(); // return the actions
        List<LineAction> lineActions = parseActions(actions);
        return lineActions;
    }

    private Tree createTree(List<LogLine> lines) {
        Tree parent = new Tree(0, "Root");
        for (LogLine line : lines) {
            Tree t = new Tree(0, line.getContent());
            t.setHeight(0);
            t.setDepth(1);
            t.setSize(1);
            t.setPos(line.getInternalLineIndex());
            t.setLength(line.getContent().length());
            t.setHash(line.getContent().hashCode());
            t.setId(line.getInternalLineIndex());
            t.setParentAndUpdateChildren(parent);
        }
        parent.setHeight(1);
        parent.setDepth(0);
        parent.setSize(lines.size()+1);
        parent.setPos(0);
        parent.setLength(4);
        parent.setId(0);
        parent.setHash("Root".hashCode());
        parent.setParent(null);
        return parent;
    }

    private List<LineAction> parseActions(List<Action> actions){
        List<LineAction> lineActionList = new ArrayList<>();
        for(Action action : actions){
            if (action instanceof Update){
                lineActionList.add(parseUpdateAction((Update)action));
            }else if(action instanceof Insert) {
                lineActionList.add(parseInsertAction((Insert)action));
            }else if(action instanceof Delete) {
                lineActionList.add(parseDeleteAction((Delete)action));
            }else if(action instanceof Move) {
                lineActionList.add(parseMoveAction((Move)action));
            }
        }
    return lineActionList;
    }

    private LineAction parseUpdateAction(Update action){
        return LineAction.builder()
                .contentBefore(action.getNode().getLabel())
                .contentAfter(action.getValue())
                .positionBefore(action.getNode().getPos())
                .positionAfter(action.getNode().getPos())
                .type(LineActionType.UPDATE)
                .build();
    }

    private LineAction parseInsertAction(Insert action){
        return LineAction.builder()
                .contentBefore("")
                .contentAfter(action.getNode().getLabel())
                .positionBefore(0)
                .positionAfter(action.getPosition())
                .type(LineActionType.ADD)
                .build();
    }

    private LineAction parseDeleteAction(Delete action){
        return LineAction.builder()
                .contentBefore(action.getNode().getLabel())
                .contentAfter("")
                .positionBefore(action.getNode().getPos())
                .positionAfter(0)
                .type(LineActionType.DELETE)
                .build();
    }

    private LineAction parseMoveAction(Move action){
        return LineAction.builder()
                .contentBefore(action.getNode().getLabel())
                .contentAfter("")
                .positionBefore(action.getNode().getPos())
                .positionAfter(0)
                .type(LineActionType.MOVE)
                .build();
    }
}
