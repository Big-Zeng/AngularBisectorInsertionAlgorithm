package com.test.service.BranchBound.branch;

import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建分支界定法的MAP集合、该算法的难点不在于算法的本身，而在于如何构建完美二叉树和定界的问题
 * 如果城市之间的费用相差不多，那么将会导致该算法变为穷举法，每个分支都将会算到
 * 1.定义城市节点的总数量
 * 2.初始化城市节点信息以及所需的差旅费、剩余可用城市节点
 * 3.获取节点最少费用的上限值（求取思路：计算出所有节点的出边和入边最小的值，而后依据该城市节点总数量*2的边，进行扩展最终求出N*2条线，比较得出最少路径值）
 */
public class BranchBoundMap {
    // 城市数量
    private Integer nodeCount = 8;
    // 城市节点的集合
    private List<BranchBound> bbList = new ArrayList<>();
    // 存储所有出边和入边的最优路径
    private List<BranchBound> bestList = new ArrayList<>();
    // 剩余城市节点的集合，节点编号
    private List<String> restNodes = new ArrayList<>();

    public BranchBoundMap(int Count, List<UserAllocation> userAllocations,int[][] dis) {
        this.nodeCount = Count;
        for (int i = 0; i < Count; i++) {
            restNodes.add("CN"+ userAllocations.get(i).getAreaInnerId());
        }
        for (int i = 0; i < Count; i++){
            List<String> tempNodes = new ArrayList<>(restNodes);
            tempNodes.remove("CN"+ userAllocations.get(i).getAreaInnerId());


            for (int j = 0; j < Count; j++) {
                if (i == j) {
                    // 排除自到达
                    continue;
                }
                BranchBound branchBound = new BranchBound("CN"+ userAllocations.get(i).getAreaInnerId(),
                        "CNAME" + userAllocations.get(i).getAreaInnerId(),
                        "CN"+ userAllocations.get(j).getAreaInnerId(),
                        dis[userAllocations.get(i).getAreaInnerId()][userAllocations.get(j).getAreaInnerId()]);
                branchBound.setRestNodes(tempNodes);
                bbList.add(branchBound);
            }

        }
        for (int i = 0; i < Count; i++) {
            String nodeNo = "CN" + userAllocations.get(i).getAreaInnerId();
            // 添加节点编码为nodeNo
            bestList.add(bbList.stream().filter(v -> nodeNo.equals(v.getNodeNo())).min((b1, b2) -> b1.getArrayNodeFee() > b2.getArrayNodeFee() ? 1 : -1).get());
            // 添加到达节点编码为nodeNo
            bestList.add(bbList.stream().filter(v -> nodeNo.equals(v.getArrayNodeNo())).min((b1, b2) -> b1.getArrayNodeFee() > b2.getArrayNodeFee() ? 1 : -1).get());
        }
        // 去除重复节点
        bestList = bestList.stream().distinct().collect(Collectors.toList());


        // 初始化剩余可用城市节点
//        for (int i = 1; i <= nodeCount; i++) {
//            restNodes.add("CN" + i);
//        }
//        // 初始化城市节点信息以及所需的差旅费
//        for (int i = 1; i <= nodeCount; i++) {
//            List<String> tempNodes = new ArrayList<>(restNodes);
//            tempNodes.remove("CN" + i);
//            for (int j = 1; j <= nodeCount; j++) {
//                if (i == j) {
//                    // 排除自到达
//                    continue;
//                }
//                int nodeFee =(int) (Math.random() * 100);
//                BranchBound branchBound = new BranchBound("CN" + i, "CNAME" + i, "CN" + j, nodeFee);
//                branchBound.setRestNodes(tempNodes);
//                bbList.add(branchBound);
//            }
//        }
//        for (int i = 1;  i <= nodeCount; i++) {
//            String nodeNo = "CN" + i;
//            // 添加节点编码为nodeNo
//            bestList.add(bbList.stream().filter(v -> nodeNo.equals(v.getNodeNo())).min((b1, b2) -> b1.getArrayNodeFee() > b2.getArrayNodeFee() ? 1 : -1).get());
//            // 添加到达节点编码为nodeNo
//            bestList.add(bbList.stream().filter(v -> nodeNo.equals(v.getArrayNodeNo())).min((b1, b2) -> b1.getArrayNodeFee() > b2.getArrayNodeFee() ? 1 : -1).get());
//        }
//        // 去除重复节点
//        bestList = bestList.stream().distinct().collect(Collectors.toList());
        // 按节点编码再进行排序
        // Collections.sort(bestList, Comparator.comparing(BranchBound::getNodeNo));
        showBranchBoundMap();
    }

    /**
     * 打印输出起始所有城市节点信息
     */
    public void showBranchBoundMap(){
        bbList.stream().forEach(v -> {
            System.out.println("节点编码"+v.getNodeNo()+"，到达节点"+v.getArrayNodeNo()+"，节点费用"+v.getArrayNodeFee());
        });
        bestList.stream().forEach(v -> {
            System.out.println("节点编码"+v.getNodeNo()+"，到达节点"+v.getArrayNodeNo()+"，节点费用"+v.getArrayNodeFee());
        });
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public List<BranchBound> getBbList() {
        return bbList;
    }

    public List<BranchBound> getBestList() {
        return bestList;
    }

    public List<String> getRestNodes() {
        return restNodes;
    }
}
