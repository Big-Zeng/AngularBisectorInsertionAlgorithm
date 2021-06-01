package com.test.service;

import com.test.model.Point;
import com.test.model.Spindle;
import com.test.model.UserAllocation;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.analysis.function.Log1p;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;

/**
 * Created by Administrator on 2018/12/11.
 */
public class AngularBisectorService {
    private int[][] distances;
    private Spindle spindle;
    private List<UserAllocation> onePartClients=new ArrayList<>() ;
    private List<UserAllocation> otherPartClients = new ArrayList();
    private final int LEFT = 1;
    private final int RIGHT = -1;






    public AngularBisectorService(int[][] distances, Spindle spindle,int direction) {
        this.distances = distances;
        this.spindle = spindle;
        AngularBisector.setSpindlePoint(direction, spindle, distances);
    }

    public AngularBisectorService(int[][] distances, Spindle spindle) {


    }


    private void PrintPoint() {
        System.out.println("warehouse" + spindle.warehouseFlag + "farPoint:" + spindle.farPointFlag);
        System.out.println("MID" + spindle.getMidPoint().x + ";" + spindle.getMidPoint().y);
    }


    //TODO
    public AngularBisectorService(int[][] distances, Spindle spindle,  Point leftPoint, Point rightPoint,int direction) {
        this.distances = distances;
        this.spindle = spindle;
        spindle.setLeftPoint(leftPoint);
        spindle.setRightPoint(rightPoint);
        AngularBisector.countFN(spindle, spindle.getWarehouse().getUserLocation(), leftPoint, rightPoint, distances,direction);
    }


   /* public void testA() {
        int[] dis = distances[spindle.warehouseFlag];
        int maxDis = Integer.MIN_VALUE;

        for (int i = 0; i < dis.length; i++) {
            if (dis[i] > maxDis && i != spindle.warehouseFlag) {
                maxDis = dis[i];
                spindle.farPointFlag = i;
            }
        }
        SortClients();
    }

    private void SortClients() {
        for (UserAllocation userAllocation : spindle.getClients()) {
            int userInnerId = userAllocation.getAreaInnerId();
            if (userInnerId != spindle.warehouseFlag && userInnerId != spindle.farPointFlag) {
                int dis = distances[userInnerId][spindle.warehouseFlag] +
                        distances[userInnerId][spindle.farPointFlag];
                userAllocation.setDistanceToMidline(dis);
            }
        }
    }*/


    /**
     * 从角平分线分开点
     */
    public void divideClientPoint() {
        Point point =new Point(spindle.getMidPoint().x - spindle.getWarehouse().getUserLocation().x,
                spindle.getMidPoint().y - spindle.getWarehouse().getUserLocation().y);
        //  System.out.println("输出在角平分线的一侧 START");
        for (UserAllocation userAllocation : spindle.getClients()) {
            Point point2 = new Point(userAllocation.getUserLocation().x - spindle.getWarehouse().getUserLocation().x,
                    userAllocation.getUserLocation().y - spindle.getWarehouse().getUserLocation().y);
            double data = point.x * point2.y - point2.x * point.y;
            // otherPartClients.add(userAllocation);
            if (data < 0) { // Point 在 point2 的逆时针
                onePartClients.add(userAllocation);
            } else {
                otherPartClients.add(userAllocation);
            }
        }
        //排列
        Collections.sort(onePartClients, comparator);
        Collections.sort(otherPartClients, comparator);
    }







    public double planPathWithCommon(List<UserAllocation> outData,int returnLineFlag) {
        setDids(spindle.getClients(), spindle.getMidPoint(), spindle.getWarehouse().getUserLocation());

        divideClientPoint();

        List<UserAllocation> oneCircle = createOneCircle(spindle.warehouseFlag, spindle.farPointFlag, LEFT);
        List<UserAllocation> otherCircle = createOneCircle(spindle.farPointFlag, spindle.warehouseFlag, RIGHT);

        double oneCircleDis = ATspService.readAllDis(oneCircle, distances);
        double otherCircleDis = ATspService.readAllDis(otherCircle, distances);
       /* if (oneCircleDis != otherCircleDis) {
            System.out.println(oneCircle);
        }*/
        // return oneCircle;
        switch (returnLineFlag) {
            case 0:
                return judgeShortAndReturn(outData, oneCircle, otherCircle, oneCircleDis, otherCircleDis);
            case 1:
                addUser(outData, oneCircle);
                return oneCircleDis;
            case -1:
                addUser(outData, otherCircle);
                return otherCircleDis;
        }
        return 0;
    }

    public static double judgeShortAndReturn(List<UserAllocation> outData, List<UserAllocation> oneCircle, List<UserAllocation> otherCircle, double oneCircleDis, double otherCircleDis) {
        if (oneCircleDis < otherCircleDis) {
            // outData = oneCircle;
            addUser(outData, oneCircle);
            return oneCircleDis;
        } else {
            //  outData = otherCircle;
            addUser(outData, otherCircle);
            return otherCircleDis;
        }
    }

    private static void addUser(List<UserAllocation> outData, List<UserAllocation> Circle) {
        outData.clear();
        for (UserAllocation userAllocation : Circle) {
            outData.add(userAllocation);
        }
    }



    /**
     * 创建一个方向上的点
     * @param flag1
     * @param flag2
     * @param dir
     * @return
     */
    private List<UserAllocation> createOneCircle(int flag1,int flag2,int dir){

        StringBuilder leftPath = buildOriginPath(flag1, flag2);
        StringBuilder rightPath = buildOriginPath(flag2, flag1);
        if (onePartClients.size() > otherPartClients.size())  //根据长度不同循环
            circlePartClient(onePartClients, otherPartClients, leftPath, rightPath);
        else
            circlePartClient(otherPartClients, onePartClients, leftPath, rightPath);

        return createOrder(leftPath.toString(), rightPath.toString(), dir);
    }


    private void printtest(List<UserAllocation> lineL, List<UserAllocation> lineR) {
        System.out.println("点的循序____START");
        for (UserAllocation userAllocation : lineL) {
            System.out.println(userAllocation.getAreaInnerId());
        }
        for (UserAllocation userAllocation : lineR) {
            System.out.println(userAllocation.getAreaInnerId());
        }
        System.out.println("点的循序____END");
    }

    private List<UserAllocation> createOrder(String pathL, String pathR,int dir) {
        // System.out.println(pathL);
        //System.out.println(pathR);
        List<UserAllocation> userAllocations1 = pathTurnToList(pathL);
        List<UserAllocation> userAllocations2 = pathTurnToList(pathR);
        return compareOrders(userAllocations1, userAllocations2, dir);
        //  printtest(userAllocations1,userAllocations2);
    }

    /**
     * 比较大小合并客户点信息
     * @param user1
     * @param user2
     * @param dir
     * @return
     */
    private  List<UserAllocation> compareOrders(List<UserAllocation> user1, List<UserAllocation> user2, int dir) {
        return dir == LEFT ? mergeClients(user2, user1) : mergeClients(user1, user2);
    }

    /**
     * 根据方向合并客户点
     * @param user1
     * @param user2
     */
    private  List<UserAllocation> mergeClients(List<UserAllocation> user1, List<UserAllocation> user2) {
        user1.remove(0);
        user2.addAll(user1);
        return user2;
    }


    /**
     * 将路径信息转换为实体路径列表
     * @author LSXY
     * @return
     */
    private  List<UserAllocation> pathTurnToList(String pathL ) {
        List<UserAllocation> fianlLine = new ArrayList<>();
        String[] current  = pathL.split(",");
        for (int i = 0; i < current.length; i++){
            int k = Integer.parseInt(current[i]);
            fianlLine.add(com.test.common.Constant.getUserByAreaInnerId(spindle.getClients(), k));
        }
        return fianlLine;
    }

    /**
     * 设置初始路径
     * @param start
     * @param end
     * @return
     */
    private StringBuilder buildOriginPath(int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        sb.append(",");
        sb.append(end);
        return sb;
    }


    /**
     * 设置每个客户点到中垂线的距离
     * @param clients
     * @param midPoint
     * @param warehousePoint
     */
    public  void setDids(List<UserAllocation> clients,Point midPoint,Point warehousePoint) {
        for (int i = 0; i < clients.size(); i++) {
            UserAllocation user = clients.get(i);
            double k = (midPoint.y - warehousePoint.y) / (midPoint.x - warehousePoint.x);
            double b = warehousePoint.y - k * warehousePoint.x;
            user.setDistanceToMidline(getDisOfPointToLine(user.getUserLocation(), k, b));
        }
    }


    /**
     *  获取点到直线的距离
     * @param key
     * @param k
     * @param b
     * @return
     */
    private static double getDisOfPointToLine(Point key,  double k, double b) {
        double value = Math.abs((key.y - k * key.x - b) / Math.sqrt(1 + k * k));
      /*  if (Double.isNaN(value)) {
            System.out.println("error");
        }*/
        return Math.abs((key.y - k * key.x - b) / Math.sqrt(1 + k * k));
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
        int go = distances[wareHouseID][keyID]+distances[keyID][farPointID]
                - distances[wareHouseID][farPointID];
        int back = distances[farPointID][keyID]+distances[keyID][wareHouseID]
                - distances[farPointID][wareHouseID];
        return go>back? back:go;
    }

    /**
     * 循环用户点
     * @param longPartClient
     * @param shortPartClient
     * @param PathL
     * @param PathR
     */
    public void circlePartClient(List<UserAllocation> longPartClient,
                                 List<UserAllocation> shortPartClient, StringBuilder PathL, StringBuilder PathR
    ) {
        int order = 3;
        for (int i = 0; i < longPartClient.size(); i++) {
            UserAllocation currentLocation = longPartClient.get(i);
            order = insertOneClientToPath(PathL, PathR, currentLocation, order);
            // System.out.println(PathL + "and " + PathR);
            if (i < shortPartClient.size()) {
                order = insertOneClientToPath(PathL, PathR, shortPartClient.get(i), order);
            }
        }
    }


    public int setAddOrder(int order, UserAllocation currentLocation) {
        currentLocation.setAddOrder(order);
        order++;
        return order;
    }


    /**
     * 插入一个点到路径去
     *
     * @param PathL
     * @param PathR
     * @param currentLocation
     */
    private int insertOneClientToPath(StringBuilder PathL, StringBuilder PathR, UserAllocation currentLocation, int order) {
        int innerId = currentLocation.getAreaInnerId();

        if (innerId == spindle.warehouseFlag) {
            currentLocation.setAddOrder(1);
        } else if (innerId == spindle.farPointFlag) {
            currentLocation.setAddOrder(2);
        } else {
            order = setAddOrder(order, currentLocation);
            isInLeft(currentLocation, PathL, PathR);
        }
        return order;
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
    final String MIN_VALUE = "minValue";
    final String MIN_VALUE_FLAG = "minValueFlag";
    private  boolean
    isInLeft(UserAllocation currentUser,
             StringBuilder pathL,StringBuilder pathR) {
        String[] pl = pathL.toString().split(",");
        String[] pr = pathR.toString().split(",");

        Map<String, Object> mapL = judgeWhichPath(pl,  currentUser);
        Map<String, Object> mapR = judgeWhichPath(pr,  currentUser);

        if((double)mapL.get(MIN_VALUE) <= (double) mapR.get(MIN_VALUE)){
            //	System.out.println("befor update:"+pathL);
            updatePath(pathL, (int)mapL.get(MIN_VALUE_FLAG), currentUser.getAreaInnerId());
            // 	System.out.println("after update:"+pathL);
            return true;
        }
        else {
            updatePath(pathR, (int)mapR.get(MIN_VALUE_FLAG), currentUser.getAreaInnerId());
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


    private Map<String, Object> judgeWhichPath(String[] areaInnerIds,UserAllocation currentUser) {
        Map<String, Object> map = new HashMap<>();
        double mark = Double.MAX_VALUE;
        int flag = 0;
        for (int i = 0; i < areaInnerIds.length - 1; i++) {
            int k1 = Integer.parseInt(areaInnerIds[i]);
            int k2 = Integer.parseInt(areaInnerIds[i + 1]);
            UserAllocation user1 = com.test.common.Constant.getUserByAreaInnerId(spindle.getClients(), k1);
            UserAllocation user2 = com.test.common.Constant.getUserByAreaInnerId(spindle.getClients(),k2);
            double currentDis = getTwoClientsDistance(user1, currentUser) +
                    getTwoClientsDistance(currentUser, user2) -
                    getTwoClientsDistance(user1, user2);
            if (currentDis < mark) {
                mark = currentDis;
                flag = i;
            }
        }
        map.put(MIN_VALUE, mark);
        map.put(MIN_VALUE_FLAG, flag);
        return map;
    }



    /**
     * 获取两个用户间的实际距离
     * @author LSXY
     * @param user1
     * @param user2
     * @return
     */
    private  int getTwoClientsDistance(UserAllocation user1,
                                       UserAllocation user2) {
        return distances[user1.getAreaInnerId()][user2.getAreaInnerId()];

    }

    Comparator<UserAllocation> comparator = new Comparator<UserAllocation>() {
        @Override
        public int compare(UserAllocation o1, UserAllocation o2) {
            if (o1.getDistanceToMidline() - o2.getDistanceToMidline() < 0)
                return 1;
            else if (o1.getDistanceToMidline() - o2.getDistanceToMidline() > 0)
                return -1;
            return 0;
        }
    };









    //获取弧度
    public double getRadian(double y,double x)
    {
        return Math.atan2(y, x);
    }

    //获取角度
    public double getAngle(double site)

    {
        return site/ Math.PI * 180;
    }






}

