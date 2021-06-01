package com.test.service.NearestInsertion;

import com.test.model.Point;
import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ZXF on 2019-02-03.
 */
public class myNearestInsertion {

    private int[][] dis;
    private HashSet<String> userAllocations;
    private String start;


    public myNearestInsertion(int[][] dis, HashSet<String> userAllocations,String start) {
        this.dis = dis;
        this.userAllocations = userAllocations;
        this.start = start;
        removePoint(start);

    }


    public void removeOtherPoint(List<String> otherValue) {
        for (String s : otherValue) {
            removePoint(s);
        }
    }

    public int main(List<String> otherValue) {
        List<String> paths = new ArrayList<>();

        paths.add(start);

        for (String s : otherValue) {
            paths.add(s);
        }
        removeOtherPoint(otherValue);
        paths.add(start); //结束也是起点

        while (userAllocations.size() != 0) {
            String point = findMaxDis(paths);
            insertPosition(point, paths);
            removePoint(point);
        }
        //System.out.println(paths.toString());
        return sumDis(paths);
    }


    public void setInitialPoint(HashSet<String> userAllocations) {
        String point1 = "";
        String point2 = "";
        for (int i = 0; i < userAllocations.size(); i++) {


        }
    }


    public int sumDis(List<String> paths) {
        int dis = 0;
        for (int i = 0; i < paths.size() - 1; i++) {
            int point1 = Integer.valueOf(paths.get(i));
            int point2 = Integer.valueOf(paths.get(i + 1));
            dis += this.dis[point1][point2];
        }
        return dis;
    }

    /**
     *  最远插入法
     *  //TODO 两个点
     * @param paths
     * @return
     */
    public String  findAllDisMinDis(List<String> paths) {
        PointAndDis pointAndDis = new PointAndDis(0, 0);
        for (int i = 0; i < paths.size() - 1; i++) {
            pointAndDis = findMinDisPointFromPoint(Integer.valueOf(paths.get(i)), pointAndDis);
        }

      //  return findAllMaxDis(paths);

       return String.valueOf(pointAndDis.pointId);
    }


    /**
     *  (Selection step)
     *  Given a sub-tour, find node r not in the sub-tour farthest from any node in the sub-tour;
     *  i.e. with maximal Crj
     * @param paths
     * @return
     */
    public String findAllMaxDis(List<String> paths) {
        int maxDis = 0;
        String userId = "0";
            for (String userAllocation : userAllocations) {
                int point2 = Integer.valueOf(userAllocation);
                int oneMaxDis = 0;
                for (int i = 0; i < paths.size() - 1; i++) {
                    int pointId = Integer.valueOf(paths.get(i));
                    if (point2 != pointId) {
                        oneMaxDis += point2;
                    }

                }
                if (oneMaxDis > maxDis) {
                    maxDis = oneMaxDis;
                    userId = userAllocation;
                }
        }
        return userId;
    }
    /**
     * 找该点最近的点
     * @param pointId
     * @return
     */
    public PointAndDis findMinDisPointFromPoint(int pointId,PointAndDis pointAndDis) {
        int bestDis = Integer.MAX_VALUE;
        int flag = 0;
        for (String userAllocation : userAllocations) {
            int point2 = Integer.valueOf(userAllocation);
            if (point2 != pointId && dis[pointId][point2] < bestDis) {
                bestDis = dis[pointId][point2];
                flag = point2;
            }
        }

        return pointAndDis.pointMinDis > bestDis ? pointAndDis : new PointAndDis(flag, bestDis);
    }

    public String findMaxDis(List<String> paths) {
        int maxDis = 0;
        String user = "";
        for (String userAllocation : userAllocations) {
            int point2 = Integer.valueOf(userAllocation);
            int dis = 0;
            for (int i = 0; i < paths.size() - 1; i++) {
                int point1 = Integer.valueOf(paths.get(i));
                dis += this.dis[point1][point2];
            }

            if (dis > maxDis) {
                maxDis = dis;
                user = userAllocation;
            }
        }
       // System.out.println("最远点" + user);
        return user;
    }



    class PointAndDis {
        public PointAndDis(int pointId,int pointMinDis){
            this.pointMinDis = pointMinDis;
            this.pointId = pointId;
        }
        public PointAndDis(){

        }
        public int pointId;
        public int pointMinDis;
    }


    public void insertPosition(String point, List<String> paths) {
        int pointI = Integer.valueOf(point);
        int dis = Integer.MAX_VALUE;
        int insertFlag = 0;
        for (int i = 0; i < paths.size() - 1; i++) {
            int point1 = Integer.valueOf(paths.get(i));
            int point2 = Integer.valueOf(paths.get(i + 1));
            int oneDis = this.dis[point1][pointI] +
                    this.dis[pointI][point2] - this.dis[point1][point2];
            if (oneDis < dis) {
                dis = oneDis;
                insertFlag = i;
            }
        }
        paths.add(insertFlag + 1, point);
    }






    public void removePoint(String point) {
        this.userAllocations.remove(point);
    }













}
