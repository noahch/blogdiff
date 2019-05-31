package ch.uzh.seal.BLogDiff.mapping;

import ch.uzh.seal.BLogDiff.differencing.Differencer;
import ch.uzh.seal.BLogDiff.differencing.LineDifferencer;
import ch.uzh.seal.BLogDiff.model.parsing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NodeLevelMapper implements Mapper {

    @Override
    public EditTree map(BuildLogTree buildLogTree1, BuildLogTree buildLogTree2, Differencer differencer) {
        List<NodeAction> nodeActions = getNodeAction(buildLogTree1.getNodes(), buildLogTree2.getNodes());
        return EditTree.builder()
                .nodeActions(nodeActions)
                .childrenActions(mapNodes(buildLogTree1.getNodes(), buildLogTree2.getNodes(), nodeActions, differencer))
                .build();

    }

    public List<EditAction> mapNodes(List<BuildLogNode> nodes1, List<BuildLogNode> nodes2, List<NodeAction> nodeActions, Differencer differencer){
        List<EditAction> action = new ArrayList<>();
        nodes1.forEach(buildLogNode -> {
            NodeAction nodeAction = nodeActions.stream().filter(n -> n.getNodeName().equals(buildLogNode.getNodeName())).findFirst().orElse(null);
            if(nodeAction != null){
                switch (nodeAction.getType()){
                    case DELETE:
                        action.add(mapNode(buildLogNode, null, differencer));
                        break;
                    case MOVE:
                        action.add(mapNode(buildLogNode, getNodeByName(nodes2, buildLogNode.getNodeName()), differencer));
                        break;
                }
            }else{
                action.add(mapNode(buildLogNode, getNodeByName(nodes2, buildLogNode.getNodeName()), differencer));
            }
        });
        nodeActions.forEach(nodeAction -> {
            if(nodeAction.getType() == NodeActionType.ADD){
                action.add(mapNode(null, getNodeByName(nodes2, nodeAction.getNodeName()), differencer));
            }
        });

        return action;
    }

    public EditAction mapNode(BuildLogNode node1, BuildLogNode node2, Differencer differencer){
        if(node1 == null){
            node1 = BuildLogNode.builder()
                    .linesBefore(new ArrayList<LogLine>())
                    .linesAfter(new ArrayList<LogLine>())
                    .logNodes(new ArrayList<BuildLogNode>())
                    .nodeName("")
                    .build();
        }
        if(node2 == null){
            node2 = BuildLogNode.builder()
                    .linesBefore(new ArrayList<LogLine>())
                    .linesAfter(new ArrayList<LogLine>())
                    .logNodes(new ArrayList<BuildLogNode>())
                    .nodeName("")
                    .build();
        }

        if((node1.getLogNodes() != null && node1.getLogNodes().size() > 0) ||
                (node2.getLogNodes() != null && node2.getLogNodes().size() > 0)) {
            List<NodeAction> nodeActions = getNodeAction(node1.getLogNodes(), node2.getLogNodes());
            return EditAction.builder()
                    .nodeName(node1.getNodeName())
                    .linesBeforeActions(differencer.diffLogLines(node1.getLinesBefore(), node2.getLinesBefore()))
                    .linesAfterActions(differencer.diffLogLines(node1.getLinesAfter(), node2.getLinesAfter()))
                    .nodeActions(nodeActions)
                    .childrenActions(mapNodes(node1.getLogNodes(), node2.getLogNodes(), nodeActions, differencer))
                    .build();
        }else{
            return EditAction.builder()
                    .nodeName(node1.getNodeName())
                    .linesBeforeActions(differencer.diffLogLines(node1.getLinesBefore(), node2.getLinesBefore()))
                    .linesAfterActions(differencer.diffLogLines(node1.getLinesAfter(), node2.getLinesAfter()))
                    .nodeActions(new ArrayList<>())
                    .childrenActions(new ArrayList<>())
                    .build();
        }

    }

    public List<NodeAction> getNodeAction(List<BuildLogNode> nodes1, List<BuildLogNode> nodes2){
        List<NodeAction> nodeActions = new ArrayList<>();
        List<String> nodeNames1 = nodes1.stream().map(buildLogNode -> buildLogNode.getNodeName()).collect(Collectors.toList());
        List<String> nodeNames2 = nodes2.stream().map(buildLogNode -> buildLogNode.getNodeName()).collect(Collectors.toList());

        // ASSUME THAT NODE NAME IS UNIQUE

        List<String> deletes = nodeNames1.stream().filter(n1 -> {
            return !nodeNames2.contains(n1);
        }).collect(Collectors.toList());
        List<String> adds = nodeNames2.stream().filter(n2 -> {
            return !nodeNames1.contains(n2);
        }).collect(Collectors.toList());
        List<String> moves = nodeNames1.stream().filter(n1 -> {
            return nodeNames2.contains(n1) && nodeNames1.indexOf(n1) != nodeNames2.indexOf(n1);
        }).collect(Collectors.toList());

        deletes.forEach(delete -> {
            nodeActions.add(NodeAction.builder()
                    .nodeName(delete)
                    .positionBefore(nodeNames1.indexOf(delete)+1)
                    .positionAfter(0)
                    .type(NodeActionType.DELETE).build());
        });
        adds.forEach(add -> {
            nodeActions.add(NodeAction.builder()
                    .nodeName(add)
                    .positionBefore(0)
                    .positionAfter(nodeNames2.indexOf(add)+1)
                    .type(NodeActionType.ADD).build());
        });
        moves.forEach(move -> {
            nodeActions.add(NodeAction.builder()
                    .nodeName(move)
                    .positionBefore(nodeNames1.indexOf(move)+1)
                    .positionAfter(nodeNames2.indexOf(move)+1)
                    .type(NodeActionType.MOVE).build());
        });
        return nodeActions;

    }

    private BuildLogNode getNodeByName(List<BuildLogNode> nodes, String name){
        return nodes.stream().filter(buildLogNode -> buildLogNode.getNodeName().equals(name)).findFirst().orElse(null);
    }

}
