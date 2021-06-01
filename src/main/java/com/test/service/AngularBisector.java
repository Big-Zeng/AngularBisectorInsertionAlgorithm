package com.test.service;

import com.sun.org.apache.regexp.internal.RE;
import com.test.common.Constant;
import com.test.model.Point;
import com.test.model.Spindle;
import com.test.model.UserAllocation;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

/**
 * Created by ZXF on 2018-12-08.
 */
public class AngularBisector {
    private  int[][] distance;

    private  float ALeftVol,ArightVol,BLeftVol,BrightVol;
    private  Spindle spindle;

    public AngularBisector(Spindle spindle, int[][] dis){
        distance = dis;
        this .spindle = spindle;
        int direction = judgePointsDirection(spindle.getClients(),
                spindle.warehouse);
        setSpindlePoint(direction,spindle,distance);

    }

    public static void setSpindlePoint(int direction,Spindle spindle,int[][] dis) {
        Point left=new Point();
        Point right = new Point();
        double site = howToSplit(spindle,left, right
                ,direction,dis);
        // spindle.setKeyPoint(far);
        spindle.setLeftPoint(left);
        spindle.setRightPoint(right);
//        System.out.println("farKeyPoint"+far.x + "," + far.y + ";LeftPoint" + left.x + "," + left.y + ";RightPoint" + right.x + "," + right.y);

    }





    public enum QuadrantEnum{
        First,
        Second,
        Third,
        Four
    }


    /**
     *  判断所有点的方向
     * @param points
     * @param wareHouse
     * @return
     */
    public static int  judgePointsDirection(List<UserAllocation> points, UserAllocation wareHouse  ) {
        Point[] firstPoints = new Point[]{new Point(Double.MIN_VALUE),
                new Point(Double.MAX_VALUE)};
        Point[] secondPoints = new Point[]{new Point(Double.MAX_VALUE),
                new Point(Double.MIN_VALUE)};
        Point[] thirdPoints = new Point[]{new Point(Double.MIN_VALUE),
                new Point(Double.MAX_VALUE)};
        Point[] fourPoints = new Point[ ]{new Point(Double.MAX_VALUE),
                new Point(Double.MIN_VALUE)};
        int FirSize = 0, SSize = 0, TSize = 0, FouSize = 0;

        for (UserAllocation userAllocation : points) {
            if (userAllocation.getAreaInnerId() == wareHouse.getAreaInnerId()) {
                continue;
            }
            Point point = userAllocation.getUserLocation();
            switch (judgePointInWhichQuadrant(wareHouse.getUserLocation(), point)) {
                case First:
                    firstPoints[0].FirAndFourPoint(point, wareHouse.getUserLocation(), true);  // 0 为L  1为R  下同
                    firstPoints[1].FirAndFourPoint(point, wareHouse.getUserLocation(), false);
                    FirSize++;
                    //System.out.println(userAllocation.getAreaInnerId());
                    break;
                case Second:
                    secondPoints[0].SecAndTriPoint(point, wareHouse.getUserLocation(), true);
                    secondPoints[1].SecAndTriPoint(point, wareHouse.getUserLocation(), false);
                    SSize++;
                    break;
                case Third:
                    thirdPoints[0].SecAndTriPoint(point, wareHouse.getUserLocation(), false);
                    thirdPoints[1].SecAndTriPoint(point, wareHouse.getUserLocation(), true);
                    //  System.out.println(userAllocation.getAreaInnerId());
                    TSize++;
                    break;
                case Four:
                    fourPoints[0].FirAndFourPoint(point, wareHouse.getUserLocation(), false);
                    fourPoints[1].FirAndFourPoint(point, wareHouse.getUserLocation(), true);
                    FouSize++;
                    break;
            }
        }
        return  judgeDirection(FirSize, SSize, TSize, FouSize, wareHouse.getUserLocation(), firstPoints, secondPoints, thirdPoints, fourPoints);
    }





    /**
     * 判断是否有大于90的角度
     * @param wareHouse
     * @param firstPoints
     * @param secondPoints
     * @param thirdPoints
     * @param fourPoints
     * @return
     */
    public static int caculateAngle(Point wareHouse, Point[] firstPoints, Point[] secondPoints, Point[] thirdPoints, Point[] fourPoints) {

        int angle = 90;
        double angleF_S = getAngle(Angle(wareHouse, firstPoints[0], secondPoints[1]));
        double angleF_F = getAngle(Angle(wareHouse, firstPoints[1], fourPoints[0]));
        double angleS_T = getAngle(Angle(wareHouse, secondPoints[0], thirdPoints[1]));
        double angleT_F = getAngle(Angle(wareHouse, thirdPoints[0], fourPoints[1]));
        if (angleF_S > angle) {
            return Constant.SOUTH;
        }
        if (angleF_F >  angle) {
            return Constant.WEST;
        }

        if (angleS_T > angle) {
            return Constant.EAST;
        }

        if (angleT_F > angle) {
            return Constant.NORTH;
        }

        return Constant.NONE; //跳过该点
    }
    //获取角度
    public static double getAngle(double site)

    {
        return site/ Math.PI * 180;
    }
    /**
     *  当象限没有东西
     * @param FirSize
     * @param SSize
     * @param TSize
     * @param FouSize
     * @param wareHouse
     * @param firstPoints
     * @param secondPoints
     * @param thirdPoints
     * @param fourPoints
     * @return
     */
    public static int judgeDirection(int FirSize, int SSize, int TSize, int FouSize,Point wareHouse, Point[] firstPoints, Point[] secondPoints, Point[] thirdPoints, Point[] fourPoints) {
        if (FirSize == 0 ){
            if (SSize == 0) {
                return Constant.SOUTH;
            } else
                return Constant.WEST;
        }

        if (SSize == 0) {
            if (FirSize + FouSize > FouSize + TSize) {
                return Constant.EAST;
            } else return Constant.SOUTH;
        }

        if (TSize == 0) {
            if (FouSize == 0) {
                return Constant.NORTH;
            } else {
                return Constant.EAST;
            }
        }
        if (FouSize == 0) {
            return Constant.NORTH;
        }
        return caculateAngle(wareHouse, firstPoints, secondPoints, thirdPoints, fourPoints);

    }














    public static QuadrantEnum judgePointInWhichQuadrant(Point wareHouse, Point point) {
        if (point.x > wareHouse.x) {
            if (point.y > wareHouse.y) { //第一象限
                return QuadrantEnum.First;
            } else {
                return QuadrantEnum.Four;
            }
        } else {
            if (point.y > wareHouse.y) {
                return QuadrantEnum.Second;
            } else {
                return QuadrantEnum.Third;
            }
        }
    }






    /**
     * 判定分裂方式，计算出四个关键点
     * @param spindle
     * @param left
     * @param right
     * @param direction
     * @param distance
     * @return
     */
    public  static double howToSplit(Spindle spindle, Point left, Point right,
                                     int direction,int[][] distance) {
        Point ws = new Point();
        Point wareHouse = spindle.getWarehouse().getUserLocation();

        switch (direction) {
            case Constant.NORTH:
                ws.x = wareHouse.x;
                ws.y = wareHouse.y + 100;
                break;
            case Constant.SOUTH:
                ws.x = wareHouse.x;
                ws.y = wareHouse.y - 100;
                break;
            case Constant.EAST:
                ws.x = wareHouse.x + 100;
                ws.y = wareHouse.y;
                break;
            case Constant.WEST:
                ws.x = wareHouse.x - 100;
                ws.y = wareHouse.y;
                break;
            default:
                break;
        }
        double sita = countLR(spindle.getClients(), wareHouse, left, right, ws, direction);
        countFN( spindle,wareHouse, left, right,distance,direction);
        return sita;
    }

    /**
     * 计算出角平分线上的最远客户点
     * 利用向量 计算角平分线
     * @author LSXY

     * @param wareHouse

     * @param left
     * @param right

     */
    public static void countFN(Spindle spindle, Point wareHouse, Point left, Point right,int[][] distance,int direction) {
        Point farPoint = getAngularKeyPoint(distance,spindle,direction);
        RealVector vector1 = new ArrayRealVector(new double[]{left.x - wareHouse.x, left.y - wareHouse.y});
        RealVector vector2 = new ArrayRealVector(new double[]{right.x - wareHouse.x, right.y - wareHouse.y});

        RealVector oneVector1 = vector1.mapDivide(vector1.getNorm());
        RealVector oneVector2 = vector2.mapDivide(vector2.getNorm());

        RealVector vector = oneVector1.add(oneVector2);

        //RealVector vector = vector1.add(vector2);
        // double[] vectorArray = vector.toArray();
        // double k = (vectorArray[1] ) / (vectorArray[0] );
        //Point focusPoint = getChuiPoint(k, farPoint, wareHouse, spindle);
        RealVector vector3 = vector.mapDivide(vector.getNorm()); //归一化
        RealVector focusPointVector = vector3.mapMultiply(getTwoPointDis(farPoint, wareHouse));

        Point focusPoint = new Point(wareHouse.x + focusPointVector.toArray()[0],
                wareHouse.y + focusPointVector.toArray()[1]); //
        spindle.setMidPoint(focusPoint);
        getNearFocusPoint(focusPoint,spindle);

    }

    public static void test(RealVector focusPointVector, RealVector vector1) {
        double value = focusPointVector.getNorm() * vector1.getNorm();
        double s = focusPointVector.dotProduct(vector1) / (value);
        System.out.println(s);
    }

    /**
     * 得到距离起始点最远点
     * @return
     */
    public static Point getAngularKeyPoint(int[][] distance, Spindle spindle,int direction) {
        int flag = spindle.warehouseFlag;
        int[] dis = distance[flag];
        int maxDis = Integer.MIN_VALUE;
        Point point = new Point();
        for (UserAllocation userAllocation : spindle.getClients()) {
            if(!isCorrectDir(spindle, direction, userAllocation))
                continue;
            if (dis[userAllocation.getAreaInnerId()] > maxDis && userAllocation.getAreaInnerId() != flag) {
                maxDis = dis[userAllocation.getAreaInnerId()];
                point = userAllocation.getUserLocation();
            }

        }
        return point;
    }


    /*private static void setMidPoint(double value,Point point,) {

    }
*/


    /**
     * 需要在正确的方向上
     * 不可以是反向
     * @param spindle
     * @param direction
     * @param userAllocation
     * @return
     */
    public static boolean  isCorrectDir(Spindle spindle, int direction, UserAllocation userAllocation) {
        switch (direction) { //东西南北
            case 0:
                if (userAllocation.getUserLocation().x > spindle.warehouse.getUserLocation().x)
                    return true;
                break;
            case 1:
                if (userAllocation.getUserLocation().x < spindle.warehouse.getUserLocation().x)
                    return true;
                break;
            case 2:
                if (userAllocation.getUserLocation().y < spindle.warehouse.getUserLocation().y) {
                    return true;
                }
                break;
            case 3:
                if (userAllocation.getUserLocation().y > spindle.warehouse.getUserLocation().y) {
                    return true;
                }
                break;
        }
        return false;
    }

    public static double Angle(Point cen, Point first, Point second)
    {
        double dx1, dx2, dy1, dy2;
        double angle;

        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;

        dx2 = second.x - cen.x;

        dy2 = second.y - cen.y;

        double c = (float)Math.sqrt(dx1 * dx1 + dy1 * dy1) * (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);

        if (c == 0) return -1;

        angle = (float)Math.acos((dx1 * dx2 + dy1 * dy2) / c);

        return angle;
    }

    /**
     * 距离仓库最远点投影在角平分线上的点
     * @param k
     * @param farPoint
     * @param warhousePoint
     */
    public static Point getChuiPoint(double k, Point farPoint,Point warhousePoint,Spindle spindle) {
        double k2 = -1.0D / k;
        Point focusPoint = new Point();

        if (Double.isNaN(k2)) {
            focusPoint = new Point(farPoint.x, warhousePoint.y);
        }else
            focusPoint = getFocusPoint(k, k2, warhousePoint.x, warhousePoint.y, farPoint.x, farPoint.y);

       /* RealVector vector1 =
                new ArrayRealVector(new double[]{focusPoint.x - warhousePoint.x, focusPoint.y - warhousePoint.y});

        RealVector vector2 = vector1.mapDivide(vector1.getNorm()); //归一化

        RealVector focusPointVector = vector2.mapMultiply(getTwoPointDis(farPoint, warhousePoint));
        focusPoint = new Point(warhousePoint.x + focusPointVector.toArray()[0],
                warhousePoint.y + focusPointVector.toArray()[1]); //*/

        spindle.setMidPoint(focusPoint);
        return focusPoint;
    }

    /**
     *  离投影点最近的客户点
     * @param focusPoint
     */
    private static void getNearFocusPoint(Point focusPoint,Spindle spindle) {

        double mark = Double.MAX_VALUE;
        int flag = 0;
        Point point = new Point();
        for (UserAllocation userAllocation : spindle.getClients()) {
            double value = getTwoPointDis(userAllocation.getUserLocation(), focusPoint);
            if (value < mark && userAllocation.getAreaInnerId() != spindle.warehouseFlag) {
                mark = value;
                flag = userAllocation.getAreaInnerId();
                point = userAllocation.getUserLocation();
            }
        }
        spindle.farPointFlag = flag;
        spindle.setKeyPoint(point);

    }
    /**
     * 得到两点距离
     * @param point1
     * @param point2
     * @return
     */
    public static double getTwoPointDis( Point point1,Point point2){

        return Math.sqrt((point1.y - point2.y)*(point1.y - point2.y) + (point1.x - point2.x)*(point1.x - point2.x));
    }

    /**
     * 两直线交点
     * @param k1
     * @param k2
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static Point getFocusPoint(double k1,double k2,double x1,double y1,double x2, double y2){

        double x = (y1-(k1*x1)-y2+(k2*x2))/(k2-k1);

        double y = k1*x + y1 - k1*x1;

        return new Point(x, y);

    }


    /*
 * 计算L以及R点
 */
    private  static double countLR(List<UserAllocation> clients,
                                   Point wareHouse, Point left, Point right, Point ws, int direction) {
        int Lmin = 0, Lmax = 0, Rmin = 0, Rmax = 0;
        double LminSita = Double.MAX_VALUE, LmaxSita = Double.MIN_VALUE;
        double RminSita = Double.MAX_VALUE, RmaxSita = Double.MIN_VALUE;

        boolean inLeft = false, inRight = false;

        double fact;

        for (int i = 0; i < clients.size(); i++) {
            double x = clients.get(i).getUserLocation().x;
            double y = clients.get(i).getUserLocation().y;

            if (direction == Constant.SOUTH || direction == Constant.NORTH)
                fact = x - wareHouse.x;
            else
                fact = y - wareHouse.y;

            if (fact < 0) {
                inLeft = true;
                double currentSita = countTwoLinesAngle(clients.get(i)
                        .getUserLocation(), ws, wareHouse);
                if (currentSita < LminSita) {
                    LminSita = currentSita;
                    Lmin = i;
                }
                if (currentSita > LmaxSita) {
                    LmaxSita = currentSita;
                    Lmax = i;
                }
            } else {
                inRight = true;
                double currentSita = countTwoLinesAngle(clients.get(i)
                        .getUserLocation(), ws, wareHouse);
                if (currentSita < RminSita) {
                    RminSita = currentSita;
                    Rmin = i;
                }
                if (currentSita > RmaxSita) {
                    RmaxSita = currentSita;
                    Rmax = i;
                }
            }

        }
        Point currentRight;
        Point currentLeft;
        double sita;
//		System.out.println("up: " + inLeft);
//		System.out.println("down:" + inRight);

        if (inLeft && inRight) {
            currentRight = clients.get(Rmax).getUserLocation();
            currentLeft = clients.get(Lmax).getUserLocation();
            sita = LmaxSita + RmaxSita;
        } else if (inLeft) {
            currentRight = clients.get(Lmin).getUserLocation();
            currentLeft = clients.get(Lmax).getUserLocation();
            sita = LmaxSita - LminSita;
        } else {
            currentRight = clients.get(Rmax).getUserLocation();
            currentLeft = clients.get(Rmin).getUserLocation();
            sita = RmaxSita - RminSita;
        }

        if (direction == Constant.NORTH || direction == Constant.WEST) {
            left.x = currentLeft.x;
            left.y = currentLeft.y;
            right.x = currentRight.x;
            right.y = currentRight.y;
        } else {
            right.x = currentLeft.x;
            right.y = currentLeft.y;
            left.x = currentRight.x;
            left.y = currentRight.y;
        }
        return sita;
    }
    /*
         * 计算两直线的距离
         */
    public  static double countTwoLinesAngle(Point left, Point right,
                                             Point wareHouse) {
        double L1, L2, L3;
        double x, y;

        x = Math.abs(left.x - wareHouse.x);
        y = Math.abs(left.y - wareHouse.y);
        L1 = Math.sqrt(x * x + y * y);

        x = Math.abs(right.x - wareHouse.x);
        y = Math.abs(right.y - wareHouse.y);
        L2 = Math.sqrt(x * x + y * y);

        x = Math.abs(right.x - left.x);
        y = Math.abs(right.y - left.y);
        L3 = Math.sqrt(x * x + y * y);

        double cosSita = (L1 * L1 + L2 * L2 - L3 * L3) / (2 * L1 * L2);
        double sita = Math.acos(cosSita);
        return sita;
    }

    /********************************************************************************************/


    /**
     * 构造一般情况下的线路
     * 进入主函数
     * @return
     */
    public double planPathWithCommon(List<UserAllocation> outData){

        //  initClient2WareHouseDis(spindle.getUserAllocation());

        int farPointID = getKeyPointInnerID(spindle.getClients(), spindle.getKeyPoint(), spindle.getLeftPoint(), spindle.getRightPoint());

        // logger.info("关键点为：\t"+spindle.getUserAllocation().get(farPointID).getUserId()+"\t"+spindle.getUserAllocation().get(farPointID).clientName);

        setDids(spindle.getClients(),
                spindle.getClients().get(farPointID).getAreaInnerId(),
                spindle.warehouseFlag);
        //ensureDistanceToMidline(spindle);
        Comparator<UserAllocation> d = new Comparator<UserAllocation>() {
            @Override
            public int compare(UserAllocation o1, UserAllocation o2) {
                if (o1.getDistanceToMidline() - o2.getDistanceToMidline() < 0)
                    return 1;
                else if (o1.getDistanceToMidline() - o2.getDistanceToMidline() > 0)
                    return -1;
                return 0;
            }
        };

        StringBuilder pathL = new StringBuilder("0,1");
        StringBuilder pathR = new StringBuilder("1,0");
        List<UserAllocation> lineA = buildPath(spindle.getClients(), spindle.getWarehouse().getUserLocation(), d, farPointID,
                pathL, pathR, Constant.left);

        pathL = new StringBuilder("1,0");
        pathR = new StringBuilder("0,1");

        List<UserAllocation> lineB = buildPath(spindle.getClients(), spindle.getWarehouse().getUserLocation(),d, farPointID,
                pathL, pathR, Constant.right);


        int Alength = pathLength(lineA, distance);
        int Blength = pathLength(lineB, distance);


        // chooseHowToDrive(Alength, Blength);

        //return lineA;
        return   AngularBisectorService.judgeShortAndReturn(lineA, lineB, outData, Alength, Blength);

    }


    private static void addUser(List<UserAllocation> outData, List<UserAllocation> Circle) {
        outData.clear();
        for (UserAllocation userAllocation : Circle) {
            outData.add(userAllocation);
        }
    }
    /**
     * 计算路径长度
     * @author LSXY
     * @param line
     * @param dis
     * @return
     */
    public  int  pathLength(List<UserAllocation> line,int[][] dis) {
        int length = 0;

        for (int i = 0; i < line.size()-2; i++)
            length += dis[line.get(i).getAreaInnerId()][line.get(i+1).getAreaInnerId()];

        return length;
    }

    /**
     * 输出信息函数，测试用。
     * @author LSXY
     * @param Alength
     * @param Blength
     * @return 0 indicates choose Aline,and the same, 1 indicates choose Bline
     */
    private  void chooseHowToDrive(int Alength,int Blength){

        System.out.println("A 线：");
        System.out.println("线路总长：" + Alength + "  左边体积：" + ALeftVol + " 右边体积：" + ArightVol);
        System.out.println("B 线：");

        System.out.println("线路总长：" + Blength + "  左边体积：" + BLeftVol + " 右边体积：" + BrightVol);

    }


    /**
     * 建立一条可用派车路径
     * @param userAllocationList
     * @param wareHouse
     * @param distance
     * @param farPointID
     * @param pathL
     * @param pathR
     * @param dir
     * @return
     */
    private  List<UserAllocation> buildPath(List<UserAllocation> userAllocationList, Point wareHouse, Comparator<UserAllocation> distance
            , int farPointID, StringBuilder pathL, StringBuilder pathR, int dir){

        PriorityQueue<UserAllocation> dusQueue = new PriorityQueue<>(11,
                distance);
        for (int i = 0; i < userAllocationList.size(); i++)
            dusQueue.add(userAllocationList.get(i));

        List<UserAllocation> lineL = new ArrayList<>();
        List<UserAllocation> lineR = new ArrayList<>();

        UserAllocation ware = new UserAllocation(wareHouse);
        int location = spindle.warehouseFlag;
        ware.setAreaInnerId(location);

        lineL.add(ware);
        lineL.add(userAllocationList.get(farPointID));

        lineR.add(ware);
        lineR.add(userAllocationList.get(farPointID));

        while (dusQueue.size() != 0) {
            UserAllocation currentLocation = dusQueue.poll();

            if (currentLocation.getAreaInnerId() != userAllocationList.get(farPointID).getAreaInnerId() && currentLocation.getAreaInnerId() != spindle.warehouseFlag) {
                if (isInLeft(lineL, lineR, currentLocation, pathL, pathR)) {
                    System.out.println(currentLocation.getDistanceToMidline() + "\t" + currentLocation.getAreaInnerId() + "\t" + "加入左线");
                    lineL.add(currentLocation);
                } else {
                    System.out.println(currentLocation.getDistanceToMidline() + "\t" + currentLocation.getAreaInnerId() + "\t" + "加入右线");
                    lineR.add(currentLocation);
                }
            }

        }

        float lvol,rvol;
        lvol = totalGoods(lineL, location,
                1,userAllocationList.get(farPointID).getAreaInnerId());
        rvol = totalGoods(lineR, location, 1,
                userAllocationList.get(farPointID).getAreaInnerId());

        if (dir == Constant. left){
            ALeftVol = lvol;
            ArightVol = rvol;
        }else {
            BLeftVol = lvol;
            BrightVol = rvol;
        }

        return creatOrder(lineL, lineR,pathL,pathR,dir);

    }
    /**
     * 根据左右线路以及方向dir合成一条可用最终线路
     * @author LSXY
     * @param startLine
     * @param returnLine
     * @param pathL
     * @param pathR
     * @param dir
     * @return
     */
    private static   List<UserAllocation> creatOrder(List<UserAllocation> startLine,
                                                     List<UserAllocation> returnLine,
                                                     StringBuilder pathL,StringBuilder pathR,int dir) {
        //System.out.println("path: "+pathL);

        List<UserAllocation> line = null;
        List<UserAllocation> second = null;

        if(dir == Constant.left){
            line = pathTurnToList(pathL, startLine);
            second = pathTurnToList(pathR, returnLine);
        }else {
            line = pathTurnToList(pathR, returnLine);
            second = pathTurnToList(pathL, startLine);
        }

        if(line.get(line.size()-1).getAreaInnerId() ==
                line.get(line.size()-2).getAreaInnerId())
            line.remove(line.size()-1);
        if(second.get(0).getAreaInnerId() ==
                second.get(1).getAreaInnerId())
            second.remove(0);

        for (int i = 1; i < second.size(); i++)
            line.add(second.get(i));


        line=reOrderByDis2WareHouse(line);
        return line;

    }

    /**
     * 根据到实际仓库点的距离重新确定顺序
     * @param userAllocationList
     */
    public static List<UserAllocation>  reOrderByDis2WareHouse(List<UserAllocation> userAllocationList){
        List<UserAllocation> newUserAlllocationList=new ArrayList<>(userAllocationList.size());
        double nearestDis=Double.MAX_VALUE;
        int nearestIndex=0;
        for(int i=1;i<userAllocationList.size()-1;i++){
            if(userAllocationList.get(i).getDis2WareHouse()<nearestDis){
                nearestDis=userAllocationList.get(i).getDis2WareHouse();
                nearestIndex=i;
            }
        }
        // logger.info("距离仓库点最近的客户为：\t"+userAllocationList.get(nearestIndex).clientName+"\t距离为:\t"+userAllocationList.get(nearestIndex).getDis2WareHouse());
        for(int j=nearestIndex;j<userAllocationList.size()-1;j++){
            newUserAlllocationList.add(userAllocationList.get(j));
        }
        for(int j=1;j<nearestIndex;j++){
            newUserAlllocationList.add(userAllocationList.get(j));
        }
        newUserAlllocationList.add(0,userAllocationList.get(0));
        newUserAlllocationList.add(userAllocationList.size()-1,userAllocationList.get(userAllocationList.size()-1));
        return newUserAlllocationList;

    }

    /**
     * 将路径信息转换为实体路径列表
     * @author LSXY
     * @param path
     * @param line
     * @return
     */
    private static   List<UserAllocation> pathTurnToList(StringBuilder path,
                                                         List<UserAllocation> line) {
        List<UserAllocation> fianlLine = new ArrayList<>();
        String[] current  = path.toString().split(",");
        for (int i = 0; i < current.length; i++){
            int k = Integer.parseInt(current[i]);
            fianlLine.add(line.get(k));
        }
        return fianlLine;
    }
    /**
     * 计算货物总重量
     * @author LSXY
     * @param line
     * @param wareId
     * @param farListId
     * @param farId
     * @return
     */
    private  float totalGoods(List<UserAllocation> line,int wareId,
                              int farListId,int farId) {
        float total = 0f;
        for (int i = 0; i < line.size(); i++)
            if(line.get(i).getAreaInnerId()!=wareId &&
                    line.get(i).getAreaInnerId()!=farId)
                total += line.get(i).getClientVolume();

        total += line.get(farListId).getClientVolume();

        return total;
    }

    /**
     * 设置每个客户点到中垂线的距离
     * @param clients
     * @param farPointID
     * @param wareHouseID
     */
    public  void setDids(List<UserAllocation> clients,int farPointID,int wareHouseID) {
        for (int i = 0; i < clients.size(); i++) {
            UserAllocation user = clients.get(i);
            user.setDistanceToMidline(
                    getMindis_Of_OneclientAddtoLine(
                            farPointID, user.getAreaInnerId(), wareHouseID));
        }
    }

    /**
     * 判断key用户加入左线是否合适，若合适返回true
     * 同时更新路径信息
     * @author LSXY
     * @param lineL
     * @param lineR
     * @param key
     * @param pathL
     * @param pathR
     * @return
     */

    private  boolean isInLeft(List<UserAllocation> lineL,
                              List<UserAllocation> lineR, UserAllocation key,
                              StringBuilder pathL,StringBuilder pathR) {

        int minL=0,minR=0;
        double countL,countR;
        countL= countR = Double.MAX_VALUE;

        String[] pl = pathL.toString().split(",");
        String[] pr = pathR.toString().split(",");

        for (int i = 0; i < pl.length-1; i++) {
            int k1 = Integer.parseInt(pl[i]);
            int k2 = Integer.parseInt(pl[i+1]);
            UserAllocation user1 = lineL.get(k1);
            UserAllocation user2 = lineL.get(k2);
            double currentDis = getTwoClientsDistance(user1, key,distance)+
                    getTwoClientsDistance(key, user2,distance)-
                    getTwoClientsDistance(user1, user2,distance);
            if(currentDis <countL ){
                countL = currentDis;
                minL = i;
            }
        }

        for (int i = 0; i < pr.length-1; i++) {
            int k1 = Integer.parseInt(pr[i]);
            int k2 = Integer.parseInt(pr[i+1]);
            UserAllocation user1 = lineR.get(k1);
            UserAllocation user2 = lineR.get(k2);
            double currentDis = getTwoClientsDistance(user1, key,distance)+
                    getTwoClientsDistance(key, user2,distance)-
                    getTwoClientsDistance(user1, user2,distance);
            if(currentDis <countR ){
                countR = currentDis;
                minR = i;
            }
        }

        if(countL <= countR){
            //			System.out.println("befor update:"+pathL);
            updatePath(pathL, minL, lineL.size());
            //			System.out.println("after update:"+pathL);
            return true;
        }
        else {
            updatePath(pathR, minR, lineR.size());
            return false;
        }
    }


    /**
     * 更新路径信息
     * @author LSXY
     * @param path
     * @param left
     * @param value
     */
    private  void updatePath(StringBuilder path,int left,int value) {
        String[] current = path.toString().split(",");
        int cnt = 0;
        for (int i = 0; i < left+1; i++)
            cnt += (current[i].length() + 1);
        path.insert(cnt, value+",");

    }
    /**
     * 获取两个用户间的实际距离
     * @author LSXY
     * @param user1
     * @param user2
     * @return
     */
    public static   int getTwoClientsDistance(UserAllocation user1,
                                              UserAllocation user2,int[][] distance) {

        return distance[user1.getAreaInnerId()][user2.getAreaInnerId()];

    }
    /**
     * 判断将点加入去程还是回程
     * @param farPointID
     * @param keyID
     * @param wareHouseID
     * @return
     */
    public  int getMindis_Of_OneclientAddtoLine(
            int farPointID,int keyID,int wareHouseID) {
        int go = distance[wareHouseID][keyID]+distance[keyID][farPointID]
                - distance[wareHouseID][farPointID];
        int back = distance[farPointID][keyID]+distance[keyID][wareHouseID]
                - distance[farPointID][wareHouseID];
        return go>back? back:go;
    }

    /**
     * 获取纺锤体中的关键点id
     * @param clients
     * @param keyPoint
     * @param leftPoint
     * @param rightPoint
     * @return
     */
    public  int getKeyPointInnerID(List<UserAllocation> clients, Point keyPoint, Point leftPoint, Point rightPoint){

        int keyPointId=0;
        double farDis=Double.MAX_VALUE;
        UserAllocation userAllocation=null;
        for(int i=0,size=clients.size();i<size;i++){
            if(((clients.get(i).getUserLocation().x==leftPoint.x)&&(clients.get(i).getUserLocation().y==leftPoint.y))||(((clients.get(i).getUserLocation().x==rightPoint.x)&&(clients.get(i).getUserLocation().y==rightPoint.y)))){
                continue;
            }
            userAllocation=clients.get(i);
            double farx=userAllocation.getUserLocation().x-keyPoint.x;
            double fary=userAllocation.getUserLocation().y-keyPoint.y;
            if(Math.sqrt(farx*farx+fary*fary)<farDis){
                farDis=Math.sqrt(farx*farx+fary*fary);
                keyPointId=i;
            }
        }
        return keyPointId;
    }

    /*************************************************ZXF************************************************************/







}
