package com.test.service.NearestInsertion;

import com.test.model.Point;
import com.test.model.UserAllocation;
import org.apache.poi.hssf.record.formula.functions.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/21.
 */
public class JarvisMarch {

    private List<UserAllocation> points;
    private List<UserAllocation> hull;
    private static int MAX_ANGLE = 4;
    private double currentMinAngle = 0;

    public JarvisMarch(List<UserAllocation> points)  {
        this.points = points;
        this.hull = new ArrayList<UserAllocation>();

        this.calculate();
    }

    private void calculate()  {
        int firstIndex = getFirstPointIndex(this.points);
        this.hull.clear();
        this.hull.add(this.points.get(firstIndex));//向list(hull)中添加第一个点
        currentMinAngle = 0;
        for (int i = nextIndex(firstIndex, this.points); i != firstIndex; i = nextIndex(
                i, this.points)) {
            this.hull.add(this.points.get(i));
        }//向list(hull)中添加其他的点，这些点将构成一个convex hull
    }

    public void remove(UserAllocation item)  {

        if (!hull.contains(item)) {
            points.remove(item);
            return;
        }
        points.remove(item);
        // TODO
        calculate();
    }

    public void remove(List<UserAllocation> items){
        points.removeAll(items);
        calculate();
    }


    public void add(UserAllocation item) {
        points.add(item);

        List<UserAllocation> tmplist = new ArrayList<UserAllocation>();

        tmplist.addAll(hull);
        tmplist.add(item);

        List<UserAllocation> tmphull = new ArrayList<UserAllocation>();
        int firstIndex = getFirstPointIndex(tmplist);
        tmphull.add(tmplist.get(firstIndex));
        currentMinAngle = 0;
        for (int i = nextIndex(firstIndex, tmplist); i != firstIndex; i = nextIndex(
                i, tmplist)) {
            tmphull.add(tmplist.get(i));
        }

        this.hull = tmphull;
    }

    public void add(List<UserAllocation> items) {
        points.addAll(items);
        List<UserAllocation> tmplist = new ArrayList<UserAllocation>();

        tmplist.addAll(hull);
        tmplist.addAll(items);

        List<UserAllocation> tmphull = new ArrayList<UserAllocation>();
        int firstIndex = getFirstPointIndex(tmplist);
        tmphull.add(tmplist.get(firstIndex));
        currentMinAngle = 0;
        for (int i = nextIndex(firstIndex, tmplist); i != firstIndex; i = nextIndex(
                i, tmplist)) {
            tmphull.add(tmplist.get(i));
        }

        this.hull = tmphull;
    }

    public List<UserAllocation> getHull() {
        return this.hull;
    }

    private int nextIndex(int currentIndex, List<UserAllocation> points) {
        double minAngle = MAX_ANGLE;
        double pseudoAngle;
        int minIndex = 0;
        for (int i = 0; i < points.size(); i++) {
            if (i != currentIndex) {
                pseudoAngle = getPseudoAngle(
                        points.get(i).getUserLocation().x- points.get(currentIndex).getUserLocation().x,
                        points.get(i).getUserLocation().y - points.get(currentIndex).getUserLocation().y);
                if (pseudoAngle >= currentMinAngle && pseudoAngle < minAngle) {
                    minAngle = pseudoAngle;
                    minIndex = i;
                } else if (pseudoAngle == minAngle) {
                    if ((Math.abs(points.get(i).getUserLocation().x
                            - points.get(currentIndex).getUserLocation().x) > Math.abs(points
                            .get(minIndex).getUserLocation().x - points.get(currentIndex).getUserLocation().x))
                            || (Math.abs(points.get(i).getUserLocation().y
                            - points.get(currentIndex).getUserLocation().y) > Math
                            .abs(points.get(minIndex).getUserLocation().y
                                    - points.get(currentIndex).getUserLocation().y))) {
                        minIndex = i;
                    }
                }
            }

        }
        currentMinAngle = minAngle;
        return minIndex;
    }

    //获得起始点
    private int getFirstPointIndex(List<UserAllocation> points) {
        int minIndex = 0;
        for (int i = 1; i < points.size(); i++) {
            if (points.get(i).getUserLocation().y < points.get(minIndex).getUserLocation().y) {
                minIndex = i;
            } else if ((points.get(i).getUserLocation().y == points.get(minIndex).getUserLocation().y)
                    && (points.get(i).getUserLocation().x < points.get(minIndex).getUserLocation().x)) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private double getPseudoAngle(double dx, double dy) {
        if (dx > 0 && dy >= 0)
            return dy / (dx + dy);
        if (dx <= 0 && dy > 0)
            return 1 + (Math.abs(dx) / (Math.abs(dx) + dy));
        if (dx < 0 && dy <= 0)
            return 2 + (dy / (dx + dy));
        if (dx >= 0 && dy < 0)
            return 3 + (dx / (dx + Math.abs(dy)));
        throw new Error("Impossible");
    }



}
