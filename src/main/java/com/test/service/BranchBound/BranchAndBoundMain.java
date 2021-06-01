package com.test.service.BranchBound;

import com.test.model.UserAllocation;

import java.util.*;

/**
 * Created by ZXF on 2019-01-26.
 */
public class BranchAndBoundMain {
    private List<UserAllocation> userAllocations = new ArrayList<>();
    private int[][] dis;
    public double minDis;
    public List<UserAllocation> bestUserArray = new ArrayList<>();

    public  Queue<BranchNode> BranchNodes =  new PriorityQueue<>(idComparator);


    public BranchAndBoundMain(List<UserAllocation> userAllocations, int[][] dis) {
        this.dis = dis;
//        for (int i = 0; i < 4; i++) {
//            this.userAllocations.add(userAllocations.get(i));
//        }

         this.userAllocations =userAllocations;

        setMinDisAndBestUsers();
    }

    /**
     *  初始化最小值
     */
    public void setMinDisAndBestUsers(){
        List<UserAllocation> userAllocations = new ArrayList<>();
        userAllocations.addAll(this.userAllocations);
        userAllocations.add(this.userAllocations.get(0));
        this.minDis = caculateDis(userAllocations);
        this.bestUserArray = userAllocations;
    }


    /**
     * 计算路径
     * @param userAllocations
     * @return
     */
    public int caculateDis(List<UserAllocation> userAllocations) {
        int allDis = 0;
        for (int i = 0; i < userAllocations.size() - 1; i++) {
            allDis += dis[userAllocations.get(i).getAreaInnerId()][userAllocations.get(i + 1).getAreaInnerId()];
        }
       return allDis;
    }


    public void main() {
        BranchNode node = generateRootNode(this.userAllocations.get(0), userAllocations);//根节点
        BranchNodes.add(node); //优先队列添加根节点
        selectPathGTest();

        System.out.println("Branch min Dis" + minDis);
    }


//    public void selectPath(BranchNode root) {
//        List<BranchNode> childNodes = generateChild(root);
//        sortMinHeap(childNodes); //最小堆排序
//        for (BranchNode childNode : childNodes) {
//            if (childNode.getActiveNode().length == 1) {
//                int dis = getOneBranchDis(childNode);
//                if(compareDis(dis)){
//                    minDis = dis;
//                }
//                continue;
//            }
//            else if(compareDis(childNode.getParentValue())) {  //扩展节点
//                selectPath(childNode);
//            }
//        }
//
//    }


    public void selectPathGTest() {
        while (!BranchNodes.isEmpty() ||BranchNodes.size() !=0 ) {
            BranchNode childNode = BranchNodes.poll();

            if (childNode.getActiveNode().length == 1) {
                int dis = getOneBranchDis(childNode);
                if(compareDis(dis)){ //设置下界
                    print(childNode);
                   // System.out.println(dis);
                    minDis = dis;
                }
            } else if (compareDis(childNode.getParentValue())) { //扩展节点
                extendNode(childNode);
            }else {
                System.out.println("break circle");
                break;
            }

        }
        BranchNodes.clear();
        System.out.println(BranchNodes.size());
    }


    public boolean caculateDis(BranchNode branchNode) {
        int dis = branchNode.getParentValue();
        int index = branchNode.getNodeCode();
        HashSet<Integer> integers = copyIntArray(branchNode.getActiveNode());
        int paths = getMinDis(index, integers, dis, branchNode.rootIndex);
       return paths < this.minDis;
    }


    public int getMinDis(int index,  HashSet<Integer> acitveNode, int path,int rootIndex) {
        int minDis = Integer.MAX_VALUE;
        Integer flag = 0;
        if (acitveNode.size() == 0) {
            path += this.dis[index][rootIndex];
            return path;
        }
        for (Integer integer : acitveNode) {
            if (this.dis[index][integer] < minDis) {
                minDis = this.dis[index][integer];
                flag = integer;
            }
        }
        path += minDis;
        acitveNode.remove(flag);

        return getMinDis(flag, acitveNode, path,rootIndex);
    }


    /**
     * 复制数组
     * @param arrays
     * @return
     */
    public HashSet<Integer> copyIntArray(int[] arrays) {
        HashSet<Integer> arrayss = new HashSet<Integer>();
        for (int i = 0; i < arrays.length; i++) {
            arrayss.add(arrays[i]);
        }
        return arrayss;
    }



    public void extendNode(BranchNode node){ //扩展点
        generateChild(node);
    }


    public void print(BranchNode childNode) {

        int path = 0;
        path += dis[childNode.getActiveNode()[0]][0];
        path += dis[childNode.getNodeCode()][childNode.getActiveNode()[0]];


        System.out.println(childNode.printNodeCode(path, dis));

    }



    public boolean compareDis(int nodeParentValue) {

        return nodeParentValue < minDis;
    }


    public int getOneBranchDis(BranchNode node) {
        int next = node.getActiveNode()[0];
        return node.getParentValue() + dis[node.getNodeCode()][next] + dis[next][node.rootIndex];
    }



    public void generateChild(BranchNode root) {
        for (Integer integer : root.getActiveNode()) {
            int[] integers = new int[root.getActiveNode().length - 1];
            int i =0;
            for(int location : root.getActiveNode()) {
                if(location == integer)
                    continue;
                integers[i] = location;
                i++;
            }
            int dis = root.getParentValue() + this.dis[root.getNodeCode()][integer]; //从起点到该点的全部距离
            BranchNode node = new BranchNode(integer, root, dis, integers, root.rootIndex);
            BranchNodes.add(node);
        }
    }


    //匿名Comparator实现
    public static Comparator<BranchNode> idComparator = new Comparator<BranchNode>(){
        @Override
        public int compare(BranchNode c1, BranchNode c2) {

            return (int) (c1.getParentValue() - c2.getParentValue());
        }
    };


    /**
     * 生成根节点
     * @param userAllocation
     * @param userAllocations
     * @return
     */
    public BranchNode generateRootNode(UserAllocation userAllocation, List<UserAllocation> userAllocations) {
        int[] activeNodes = new int[userAllocations.size() - 1];
        int i = 0;
        for (UserAllocation allocation : userAllocations) {
            if(allocation.getAreaInnerId() != userAllocation.getAreaInnerId()){
                activeNodes[i] = allocation.getAreaInnerId();
                i++;
            }
        }
        BranchNode node = new BranchNode(userAllocation.getAreaInnerId(), null, 0, activeNodes,
                userAllocation.getAreaInnerId());
        return node;
    }


    /**
     * 最小堆排序
     * 从小到大
     * @param nodes
     */
    public void sortMinHeap(List<BranchNode> nodes) {
        for (int i = nodes.size() / 2 - 1; i >= 0; i--) {
          //  nodes.get(i).setNodeValue(this.dis);
            shiftdown(nodes, i, nodes.size());
        }
    }

    /**
     * 最小堆排序
     * 向上调整
     * @param nodes
     * @param i
     * @param length
     */
    public void shiftdown(List<BranchNode> nodes, int i, int length) {
        while (2 * i + 1 < length) {
            int j = (i << 1) + 1;
            if (j + 1 < length && nodes.get(j).getParentValue() > nodes.get(j + 1).getParentValue()) {
                j = j + 1;
            }
            if (nodes.get(i).getParentValue() > nodes.get(j).getParentValue()) {
                Collections.swap(nodes, i, j);
            } else {
                break;
            }
            i = j;
        }
    }





    public List<UserAllocation> removeCurrentAllocation(List<UserAllocation> userAllocations, UserAllocation userAllocation) {
        List<UserAllocation> userAllocations1 = new ArrayList<>();
        for (UserAllocation allocation : userAllocations) {
            if(allocation.getAreaInnerId() != userAllocation.getAreaInnerId())
            userAllocations1.add(allocation);
        }

        return  userAllocations1;
    }

    /**
     * 添加终点
     * @param branches
     * @param endUser
     */
    public void branchAddEnd(List<Branch> branches, UserAllocation endUser) {
        for (Branch branch : branches) {
            branch.userAllocations.add(endUser);
            int dis = printBranch(branch);
            if (dis < minDis) {
                minDis = dis;
                bestUserArray = branch.userAllocations;
            }
        }
        branches.clear();
    }

   int mark = 0;
   // List<Branch> branches = new ArrayList<>();

    public void circle(Branch branch, List<Branch> branches, List<UserAllocation> userAllocations, int flag) {
        //flag++;
        mark++;
        System.out.println("次数" + mark);
        if(branch.currentAllocation!= null)
        System.out.println(branch.currentAllocation.getAreaInnerId());
        for (UserAllocation allocation : userAllocations) {
            if (branch.currentAllocation!=null && branch.currentAllocation.getAreaInnerId() == allocation.getAreaInnerId()) {
                return;
            }
            Branch branch1 = new Branch(branch.userAllocations);
            branch1.userAllocations.add(allocation);
            branch1.currentAllocation = allocation;
            if (!compareDis(branch1.userAllocations)) {
                List<UserAllocation> newUsers = removeCurrentAllocation(userAllocations,allocation);
                circle(branch1, branches, newUsers,flag);
            }
        }
        if (userAllocations.size() == 0) {
            branches.add(branch);
        }
    }

    /**
     * 查看是否超过下界  超过就删除分支
     * @param userAllocations
     * @return
     */
    public boolean compareDis(List<UserAllocation> userAllocations) {
        int dis = caculateDis(userAllocations);
        return dis > minDis;
    }


    public List<UserAllocation> removeFirstFromArray(List<UserAllocation> userAllocations) {
        List<UserAllocation> userAllocations1 = new ArrayList<>();
        if (userAllocations.size() <= 1) {
            return userAllocations1;
        }
        for (int i = 1; i < userAllocations.size(); i++) {
            userAllocations1.add(userAllocations.get(i));
        }
        return userAllocations1;
    }



    public int printBranch(Branch branch) {
    //    System.out.println("branch User Array");

//        for (UserAllocation userAllocation : branch.userAllocations) {
//            System.out.print(userAllocation.getAreaInnerId());
//            System.out.println("\t");
//        }

        int dis  = caculateDis(branch.userAllocations);
      //  System.out.println("branch dis = " + dis);
        return  dis;
    }








}
