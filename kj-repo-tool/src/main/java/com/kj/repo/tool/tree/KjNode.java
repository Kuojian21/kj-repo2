package com.kj.repo.tool.tree;

public interface KjNode {

    <T extends KjNode> T[] getChilds();

    NodeType type();

    enum NodeType {
        LEAF, COMP
    }

}
