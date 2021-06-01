/**
 * @author zxf2015年8月3日
 */
package com.test.model;



import java.util.ArrayList;
import java.util.List;


/**
 * @author zxf2015年8月3日
 */
public class Spindle {
    private List<UserAllocation> clients = new ArrayList<UserAllocation>();
    public  UserAllocation warehouse;

    private Point leftPoint;//左边界点
    private Point rightPoint;//右边界点
    private Point keyPoint;
    private Point midPoint ; //
    public  int warehouseFlag;
    public int farPointFlag;
    public double disLength; //路程长

    public Point getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(Point midPoint) {
        this.midPoint = midPoint;
    }

    private int[] opts;

    public int[] getOpts() {
        return opts;
    }

    public void setOpts(String[] opts) {
        int[] optI = new int[opts.length];
        for (int i = 0; i < opts.length; i++) {
            optI[i] = Integer.valueOf(opts[i].trim()) - 1;//-1
        }
        this.opts = optI;
    }




    public UserAllocation getWarehouse() {

        return warehouse;
    }

    public Spindle(){

    }

    public Spindle(List<UserAllocation> userAllocations, double disLength) {
        this.clients = userAllocations;
        this.disLength = disLength;
    }

    public Spindle(List<UserAllocation> userAllocations, int warehouse) {
        this.clients = userAllocations;
        this.warehouseFlag = warehouse;
        this.warehouse = com.test.common.Constant.getUserByAreaInnerId(userAllocations, warehouse);
   //     userAllocations.remove(warehouse);
    }

    public List<Point> getKeyPoints(){
        List<Point> points = new ArrayList<>();
        /*keyPoint.name = "key";
        leftPoint.name = "left";
        rightPoint.name = "right";
        midPoint.name = "mid";
        warehouse.getUserLocation().name = "start";*/
       /* System.out.println(getRotateAngle(leftPoint.x - warehouse.x,
                leftPoint.y - warehouse.y, midPoint.x - warehouse.x,
                midPoint.y - warehouse.y));*/
        /*System.out.println(getRotateAngle(rightPoint.x - warehouse.x,
                rightPoint.y - warehouse.y, midPoint.x - warehouse.x,
                midPoint.y - warehouse.y));*/
      /*  points.add(warehouse.getUserLocation());
        points.add(midPoint);
        points.add(keyPoint);
        points.add(leftPoint);
        points.add(rightPoint);*/
        return points;
    }



   /* public  void setPointFlag(List<Point> points){
        for (Point point : points) {
            for (UserAllocation client : this.clients) {
                if (point.x == client.getUserLocation().x && point.y == client.getUserLocation().y) {
                  //  point.flag = client.getUserLocation()
                }
            }
        }
    }
*/
    static double getRotateAngle(double x1, double y1, double x2, double y2)
    {
        double epsilon = 1.0e-6;
        double nyPI = Math.acos(-1.0);
        double dist, dot, degree, angle;

        // normalize
        dist = Math.sqrt( x1 * x1 + y1 * y1 );
        x1 /= dist;
        y1 /= dist;
        dist = Math.sqrt( x2 * x2 + y2 * y2 );
        x2 /= dist;
        y2 /= dist;
        // dot product
        dot = x1 * x2 + y1 * y2;
        if ( Math.abs(dot-1.0) <= epsilon )
            angle = 0.0;
        else if ( Math.abs(dot+1.0) <= epsilon )
            angle = nyPI;
        else {
            double cross;

            angle = Math.acos(dot);
            //cross product
            cross = x1 * y2 - x2 * y1;
            // vector p2 is clockwise from vector p1
            // with respect to the origin (0.0)
            if (cross < 0 ) {
                angle = 2 * nyPI - angle;
            }
        }
        degree = angle *  180.0 / nyPI;
        return degree;
    }

    public void setWarehouse(UserAllocation warehouse) {
        this.warehouse = warehouse;
    }

    public Point getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(Point leftPoint) {
        this.leftPoint = leftPoint;
    }

    public Point getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(Point rightPoint) {
        this.rightPoint = rightPoint;
    }

    public Point getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(Point keyPoint) {
        this.keyPoint = keyPoint;
    }

    public List<UserAllocation> getClients() {
        return clients;
    }

    public void setClients(List<UserAllocation> clients) {
        this.clients = clients;
    }
}


