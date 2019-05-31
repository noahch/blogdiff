package ch.uzh.seal.BLogDiff.mapping;

import ch.uzh.seal.BLogDiff.differencing.Differencer;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogNode;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.Tree;

public class GumTreeMapper implements Mapper {

    @Override
    public EditTree map(BuildLogTree buildLogTree1, BuildLogTree buildLogTree2, Differencer differencer) {
        ITree src = mapToGumTree(buildLogTree1);
        ITree dst = mapToGumTree(buildLogTree2);
        return null;
    }


    public Tree mapToGumTree(BuildLogTree buildLogTree){
        Tree t = new Tree(0, "Root");

        buildLogTree.getNodes().forEach(
                buildLogNode -> {
                    Tree t_x = new Tree(1, buildLogNode.getNodeName());
                    t_x.setDepth(1);
                    t_x.setHash(buildLogNode.getNodeName().hashCode());

                }
        );
        return null;
    }

    public Tree mapNode(BuildLogNode buildLogNode, int depth){
        //base case
        if(buildLogNode.getLogNodes() == null || buildLogNode.getLogNodes().size() < 1){
            Tree t = new Tree(depth, buildLogNode.getNodeName());
            t.setDepth(depth);
            t.setHeight(1);
            t.setHash(buildLogNode.getNodeName().hashCode());

            Tree linesBefore = new Tree(depth+1, "");

        }


        //set label

        //set hash

        //set height

        //set id

        //set depth

        //set parent and children

        //set size

        //set pos

        //set length
    return null;
    }

}
