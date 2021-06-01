package com.test.service.BranchBound.branch;

import java.io.Serializable;
import java.util.List;

/**
 * 分支定界法的实体bean 假设来回的差旅费用不一样
 * 实体包含：城市节点编号、城市节点名称、城市节点所属层级、到达城市节点编号、到达城市节点所需费用
 */
public class BranchBound implements Serializable {
    // 城市节点编号
    private String nodeNo;
    // 城市节点名称
    private String nodeName;
    // 城市节点所属层级
    private Integer nodeLevel;
    // 到达城市节点编号
    private String arrayNodeNo;
    // 到达城市节点所需费用
    private int arrayNodeFee;
    // 剩余没去的城市节点，节点编号
    private List<String> restNodes;

    public BranchBound(String nodeNo, String nodeName, String arrayNodeNo, int arrayNodeFee) {
        super();
        this.nodeNo = nodeNo;
        this.nodeName = nodeName;
        this.arrayNodeNo = arrayNodeNo;
        this.arrayNodeFee = arrayNodeFee;
    }

    public String getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(String nodeNo) {
        this.nodeNo = nodeNo;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
    }

    public String getArrayNodeNo() {
        return arrayNodeNo;
    }

    public void setArrayNodeNo(String arrayNodeNo) {
        this.arrayNodeNo = arrayNodeNo;
    }

    public int getArrayNodeFee() {
        return arrayNodeFee;
    }

    public void setArrayNodeFee(int arrayNodeFee) {
        this.arrayNodeFee = arrayNodeFee;
    }

    public List<String> getRestNodes() {
        return restNodes;
    }

    public void setRestNodes(List<String> restNodes) {
        this.restNodes = restNodes;
    }
}