package com.test.service.BranchBound;

import java.util.HashSet;
import java.util.List;

/**
 * Created by ZXF on 2019-01-28.
 */
public class BranchNode {


    private int nodeCode; //节点编号
    private BranchNode parentNode;
    private int parentValue;
    private int[] activeNode;
    public int rootIndex;
   // private int nodeValue;

//    public int getNodeValue() {
//        return nodeValue;
//    }


    public int[] getActiveNode() {
        return activeNode;
    }

    public int printNodeCode(int path,int[][] dis) {
        if(parentNode == null){
            return path;
        }
        path += dis[parentNode.nodeCode][nodeCode];

        return parentNode.printNodeCode(path,dis);
    }

    public void setActiveNode(int[] activeNode) {
        this.activeNode = activeNode;
    }

    public int getParentValue() {
        return parentValue;
    }

    public void setParentValue(int parentValue) {
        this.parentValue = parentValue;
    }

    public int getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(int nodeCode) {
        this.nodeCode = nodeCode;
    }

    public BranchNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(BranchNode parentNode) {
        this.parentNode = parentNode;
    }


//    public void setNodeValue(int[][] dis) {
//        nodeValue = parentValue + dis[parentNode.getNodeCode()][this.nodeCode];
//    }



    public BranchNode(int nodeCode, BranchNode parentNode, int parentValue, int[] activeNode) {
        this.nodeCode = nodeCode;
        this.parentNode = parentNode;
        this.parentValue = parentValue;
        this.activeNode = activeNode;
    }

    public BranchNode(int nodeCode, BranchNode parentNode, int parentValue, int[] activeNode,int rootIndex) {
        this.nodeCode = nodeCode;
        this.parentNode = parentNode;
        this.parentValue = parentValue;
        this.activeNode = activeNode;
        this.rootIndex = rootIndex;
    }

    public BranchNode() {
        super();
    }
}
