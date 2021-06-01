package com.test.service.NearestInsertion;

import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/22.
 */
public class ConvexHullAlogrithm {
    private int[][] dis;
    public int[] convexHulls;



    public List<UserAllocation> main(List<UserAllocation> userAllocations, int[][] dis) {
        //得到凸包
        JarvisMarch jarvisMarch = new JarvisMarch(userAllocations);
        this.dis = dis;
        List<UserAllocation> convexHull = jarvisMarch.getHull();
        setconvexHulls(convexHull);
        int currentOrder = setOrder(convexHull);
        userAllocations.removeAll(convexHull); //删除重复
        convexHull.add(convexHull.get(0));

        int order = 1;
        while (userAllocations.size() != 0) {
            order = getMinDis(userAllocations, convexHull, order);
        }
        return convexHull;
    }

    public void setconvexHulls(List<UserAllocation> convexHull) {
        convexHulls = new int[convexHull.size()];
        for (int i = 0; i < convexHull.size(); i++) {
            convexHulls[i] = convexHull.get(i).getAreaInnerId();
        }
    }



    public int setOrder(List<UserAllocation> userAllocations) {
        int i = 1;
        for (UserAllocation userAllocation : userAllocations) {
            userAllocation.setAddOrder(i);
        }
        return i;

    }


    public int getMinDis(List<UserAllocation> userAllocations, List<UserAllocation> hulls, int currentOrder) {
        List<packageInfo> packageInfos = new ArrayList<>();
        double minDis = Double.MAX_VALUE;
        int minFlag = 0;
        int insertFlag = 0; //插入的点下标
        for (int i1 = 0; i1 < userAllocations.size(); i1++) {
            //   if(userAllocations.get(i1).getAreaInnerId() == 13)
            //     System.out.println(1);
            int dis = Integer.MAX_VALUE;
            int flag = 0;
            for (int i = 0; i < hulls.size() - 1; i++) {
                int value = getThreePointDis(hulls.get(i).getAreaInnerId(),
                        hulls.get(i + 1).getAreaInnerId(), userAllocations.get(i1).getAreaInnerId());
                if (value < dis) {
                    dis = value;
                    flag = i;
                }
            }
            double value = getRatio(hulls.get(flag).getAreaInnerId(),
                    hulls.get(flag + 1).getAreaInnerId(), userAllocations.get(i1).getAreaInnerId());
            if (value < minDis) { //取出全局最小
                minDis = value;
                minFlag = flag;
                insertFlag = i1;
            }
        }
       return  insertRPoint(minFlag, hulls, userAllocations, userAllocations.get(insertFlag),currentOrder);

    }


    public int insertRPoint(int flag, List<UserAllocation> hulls, List<UserAllocation> userAllocations,
                            UserAllocation userAllocation,int order) {
      /*  System.out.println(userAllocation.getAreaInnerId() + "插入" + hulls.get(flag).getAreaInnerId() + "到"
                + hulls.get(flag + 1).getAreaInnerId());*/
        order++; //加入顺序
        userAllocation.setAddOrder(order);
        hulls.add(flag + 1, userAllocation);
        userAllocations.remove(userAllocation);
        return order;
    }



    public class packageInfo{
        public int dis;
        public int flag;

        public packageInfo(int dis, int flag) {
            this.dis = dis;
            this.flag = flag;
        }
    }

    /**
     * cir + crj - cij is minimal
     * @param id1
     * @param id2
     * @param insertId
     * @return
     */
    public int getThreePointDis(int id1, int id2, int insertId) {
        return dis[id1][insertId] + dis[insertId][id2] - dis[id1][id2];
    }

    /**
     * (cir + crj)/cij
     * @return
     */
    public double getRatio(int id1, int id2, int insertId){
        double D = Double.valueOf((dis[id1][insertId] + dis[insertId][id2]));
        double value = D / dis[id1][id2];
       // System.out.println(id1 + "与" + id2 + "插入" + insertId + "的结果" + value);
        return value;
    }




}
