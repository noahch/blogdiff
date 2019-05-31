package ch.uzh.seal.BLogDiff.mapping;

import ch.uzh.seal.BLogDiff.differencing.Differencer;
import ch.uzh.seal.BLogDiff.model.parsing.BuildLogTree;
import ch.uzh.seal.BLogDiff.model.parsing.EditTree;

public interface Mapper {
    EditTree map(BuildLogTree buildLogTree1, BuildLogTree buildLogTree2, Differencer differencer);
}
