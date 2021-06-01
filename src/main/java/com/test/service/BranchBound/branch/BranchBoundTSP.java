package com.test.service.BranchBound.branch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 根据已有的模型计算出分支定界算法对TSP的最优解
 * 1.从Map类中获取剩余城市节点的集合
 * 2.从Map类中获取最优出边和入边的集合
 * 3.按出入边的数据集合依次求取每个路径中的值并存储到map中
 * 求取策略为优先从出入边集合中获取，不存在则取子树集合的最小值那个BranchBound
 */
public class BranchBoundTSP {
    // 定义起始节点编码
    private final String initNodeNo = "CN1";
    // 存储所有候选最佳路径的集合
    private Map<String, List<BranchBound>> bestMap = new HashMap<>();
    // Map类
    private BranchBoundMap branchBoundMap;

    public BranchBoundTSP(BranchBoundMap branchBoundMap){
        super();
        this.branchBoundMap = branchBoundMap;
    }

    public void getBestPathOfTSP(){
        // 获取城市节点的集合
        List<BranchBound> bbList = branchBoundMap.getBbList();
        // 获取所有出边和入边的最优路径
        List<BranchBound> bestList = branchBoundMap.getBestList();
        // 剩余城市节点的集合，节点编号
        List<String> restNodes = branchBoundMap.getRestNodes();
        for (BranchBound branchBound : bestList) {
            // 存储当前节点前后追溯路径的集合
            List<BranchBound> tempList = new ArrayList<>();
            // 添加当前节点
            tempList.add(branchBound);
            // 用于后面的节点控制
            List<String> tempNodes = new ArrayList<>(restNodes);
            // 删除当前的城市节点编码
            tempNodes.remove(branchBound.getNodeNo());
            // 删除所要到达的城市节点编码
            tempNodes.remove(branchBound.getArrayNodeNo());
            System.out.println(branchBound.getNodeNo()+"-"+branchBound.getArrayNodeNo());
            if (initNodeNo.equals(branchBound.getNodeNo())){
                // 开始节点为当前节点只需到达节点往后追溯即可
                do {
                    arrayNodeTransToEndNode(branchBound, tempList, tempNodes, bestList, bbList);
                } while (initNodeNo.equals(branchBound.getArrayNodeNo()));
            } else if (initNodeNo.equals(branchBound.getArrayNodeNo())){
                // 开始节点为到达节点只需当前节点往前追溯即可
                do {
                    nowNodeTransToStartNode(branchBound, tempList, tempNodes, bestList, bbList);
                } while (initNodeNo.equals(branchBound.getNodeNo()));
            } else {
                // 定义一个初始的branchBound
                BranchBound branchMain = branchBound;
                // 当前节点往前追溯到开始节点
                do {
                    nowNodeTransToStartNode(branchBound, tempList, tempNodes, bestList, bbList);
                } while (initNodeNo.equals(branchBound.getNodeNo()));
                // 到达节点往后追溯到结束节点
                do {
                    arrayNodeTransToEndNode(branchMain, tempList, tempNodes, bestList, bbList);
                } while (initNodeNo.equals(branchBound.getArrayNodeNo()));
            }
            // 节点追溯完后存储到map中，key为该最小费用节点的”当前节点-到达节点“，值为候选最优的路径集合
            String keyPath = branchBound.getNodeNo()+"-"+branchBound.getArrayNodeNo();
            bestMap.put(keyPath, tempList);
        }
        // 全部路径都已收集完成，展示其最终成果
        showBestPathMap();
    }

    /**
     * 展示各个节点最小 出边和入边的路径集合，从而得出最优路径的集合
     */
    public void showBestPathMap() {
        String bestPath = "";
        Double bestFee = 0.0;
        for (String keyPath : bestMap.keySet()){
            System.out.println(keyPath);
            List<BranchBound> tempList = bestMap.get(keyPath);
            // 对list的第一个节点依次往后排序
            BranchBound temp;
            String nodeNo = initNodeNo;
            Double totalFee = 0.0;
            do {
                temp = getBranchBoundByNodeNo(tempList,nodeNo);
                nodeNo = temp.getArrayNodeNo();
                System.out.println("开始城市节点："+temp.getNodeNo()
                        +",结束城市节点："+temp.getArrayNodeNo()
                        +",费用："+temp.getArrayNodeFee());
                totalFee += temp.getArrayNodeFee();
            } while (temp != null && !temp.getArrayNodeNo().equals(initNodeNo));
            if (totalFee < bestFee || bestFee == 0){
                bestFee = totalFee;
                bestPath = keyPath;
            }
            System.out.println("候选最优路段："+keyPath+",总费用"+totalFee);
        }
        System.out.println("===========================================================");
        System.out.println("最优路段为："+bestPath+",总费用"+bestFee);
    }

    /**
     * 根据现有节点来求出路径节点
     * @param nodeNo
     * @return
     */
    public BranchBound getBranchBoundByNodeNo(List<BranchBound> pathList, String nodeNo){
        List<BranchBound> tempList = new ArrayList<>(pathList);
        return tempList.stream().filter(v->v.getNodeNo().equals(nodeNo)).collect(Collectors.toList()).get(0);
    }

    /**
     * 当前节点，需到达节点往后追溯结束城市节点
     * 1.获取到达节点的编码，根据以该编码为开始节点到两城最优路线集合中查找最低的节点
     * 2.如果不存在则到节点集合中查询以该编码为开始节点，剩余未走的城市节点作为到达节点求取最小费用的那个节点
     * 3.如果存在则去该节点，依次递归该方法直到最终追溯到结束城市节点
     * @param branchBound 当前节点
     * @param pathList 存储当前节点前后追溯路径的集合
     * @param restNodes 剩余未走的城市节点
     * @param bestList 两城之前最优的城市节点
     * @param bbList 两城之前的城市节点集合
     */
    private void arrayNodeTransToEndNode(BranchBound branchBound, List<BranchBound> pathList, List<String> restNodes, List<BranchBound> bestList, List<BranchBound> bbList) {
        if (initNodeNo.equals(branchBound.getArrayNodeNo())){
            // 已追溯到结束城市节点，返回
            return;
        }
        Optional<BranchBound> minBBOpt = bestList.stream().filter(v-> {
            // 开始节点等于传入的到达节点 且到达节点在剩余节点内
            if (v.getNodeNo().equals(branchBound.getArrayNodeNo())&&restNodes.contains(v.getArrayNodeNo())){
                return true;
            } else if (v.getNodeNo().equals(branchBound.getArrayNodeNo())&&restNodes.size()==0&&v.getArrayNodeNo().equals(initNodeNo)){
                // 开始节点等于传入的到达节点且没有剩余节点和结束节点为结束节点
                return true;
            }
            return false;
        }).collect(Collectors.minBy(Comparator.comparingDouble(BranchBound::getArrayNodeFee)));
        if (minBBOpt != null && minBBOpt.isPresent()){
            BranchBound minBB = minBBOpt.get();
            System.out.println(minBB.getNodeNo()+"-"+minBB.getArrayNodeNo());
            pathList.add(minBB);
            restNodes.remove(minBB.getArrayNodeNo());
            arrayNodeTransToEndNode(minBB, pathList, restNodes, bestList, bbList);
        }else{
            // 从两城之前的城市节点集合中获取
            BranchBound minBB = bbList.stream().filter(v->{
                // 开始节点等于传入的到达节点 且到达节点在剩余节点内
                if (v.getNodeNo().equals(branchBound.getArrayNodeNo())&&restNodes.contains(v.getArrayNodeNo())){
                    return true;
                } else if (v.getNodeNo().equals(branchBound.getArrayNodeNo())&&restNodes.size()==0&&v.getArrayNodeNo().equals(initNodeNo)){
                    // 开始节点等于传入的到达节点且没有剩余节点和结束节点为结束节点
                    return true;
                }
                return false;
            }).collect(Collectors.minBy(Comparator.comparingDouble(BranchBound::getArrayNodeFee))).get();
            System.out.println(minBB.getNodeNo()+"-"+minBB.getArrayNodeNo());
            pathList.add(minBB);
            restNodes.remove(minBB.getArrayNodeNo());
            arrayNodeTransToEndNode(minBB, pathList, restNodes, bestList, bbList);
        }
    }

    /**
     * 到达节点，需当前节点往前追溯到开始城市节点
     * @param branchBound 当前节点
     * @param pathList 存储当前节点前后追溯路径的集合
     * @param restNodes 剩余未走的城市节点
     * @param bestList 两城之前最优的城市节点
     * @param bbList 两城之前的城市节点集合
     */
    private void nowNodeTransToStartNode(BranchBound branchBound, List<BranchBound> pathList, List<String> restNodes, List<BranchBound> bestList, List<BranchBound> bbList) {
        if (initNodeNo.equals(branchBound.getNodeNo())){
            // 已追溯到开始城市节点，返回
            return;
        }
        Optional<BranchBound> minBBOpt = bestList.stream().filter(v->{
            // 开始节点等于传入的到达节点 且到达节点在剩余节点内
            if (v.getArrayNodeNo().equals(branchBound.getNodeNo())&&restNodes.contains(v.getNodeNo())){
                return true;
            } else if (v.getArrayNodeNo().equals(branchBound.getNodeNo())&&restNodes.size()==0&&v.getNodeNo().equals(initNodeNo)){
                // 开始节点等于传入的到达节点且没有剩余节点和结束节点为结束节点
                return true;
            }
            return false;
        }).collect(Collectors.minBy(Comparator.comparingDouble(BranchBound::getArrayNodeFee)));
        if (minBBOpt != null && minBBOpt.isPresent()){
            BranchBound minBB = minBBOpt.get();
            System.out.println(minBB.getNodeNo()+"-"+minBB.getArrayNodeNo());
            pathList.add(minBB);
            restNodes.remove(minBB.getNodeNo());
            nowNodeTransToStartNode(minBB, pathList, restNodes, bestList, bbList);
        }else{
            // 从两城之前的城市节点集合中获取
            BranchBound minBB = bbList.stream().filter(v->{
                // 开始节点等于传入的到达节点 且到达节点在剩余节点内
                if (v.getArrayNodeNo().equals(branchBound.getNodeNo())&&restNodes.contains(v.getNodeNo())){
                    return true;
                } else if (v.getArrayNodeNo().equals(branchBound.getNodeNo())&&restNodes.size()==0&&v.getNodeNo().equals(initNodeNo)){
                    // 开始节点等于传入的到达节点且没有剩余节点和结束节点为结束节点
                    return true;
                }
                return false;
            }).collect(Collectors.minBy(Comparator.comparingDouble(BranchBound::getArrayNodeFee))).get();
            System.out.println(minBB.getNodeNo()+"-"+minBB.getArrayNodeNo());
            pathList.add(minBB);
            restNodes.remove(minBB.getNodeNo());
            nowNodeTransToStartNode(minBB, pathList, restNodes, bestList, bbList);
        }
        System.out.println("==================结束");
    }
}
