package com.test.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.common.Constant;
import com.test.model.Point;
import com.test.model.Spindle;
import com.test.model.UserAllocation;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.test.controller.TSPTestController.formatDouble1;

/**
 * Created by ZXF on 2018-12-08.
 */
@Service
public class ATspService {


    /**
     * 判断两者距离
     * @param dis
     * @param spindle
     * @param afterUsers
     */
    public static  List<UserAllocation> judgeDis(int[][] dis, Spindle spindle, List<UserAllocation> afterUsers,double value) {

        int dis1 = readAllDis(afterUsers, dis);
        List<UserAllocation> bastData = new ArrayList<>();
        if (spindle.getOpts() != null && spindle.getOpts().length != 0) {
            int dis2 = readAllDis(spindle.getOpts(), dis);
            double bilv = ((Double.valueOf(dis1) / dis2) - 1)*100;
            //System.out.println("AD" + "长度：" + formatDouble1(dis1) + "+(" + formatDouble1(bilv) + "%)");
         //   System.out.println("AD算法距离：" + formatDouble1(dis1) + ";该组合最优解：" + dis2 + "误差比" + formatDouble1(bilv * 100) + "%");
            bastData = initBastPoint(spindle);
           // System.out.println(readAllDis(bastData, dis)); //给出的最优解

        }else{
            double bilv = ((Double.valueOf(dis1) / value) - 1)*100;
            //System.out.println("AD算法距离：" + formatDouble1(dis1) + "+(" + formatDouble1(bilv) + "%)");
        }
        return bastData;
    }


    private static List<String> evalXy() {
       String  path="E:\\Paper\\data\\elion50\\eilon50_opt.txt";
        File file  = new File(path);
        BufferedReader bufferedReader = null;
        List<String> points = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String str = "";
            int i = 0;
            while ((str = bufferedReader.readLine()) != null) {
                String data = str.trim();
                points.add(data);
                // String[] strings = str.trim().split(" ");
                // points.add(new Point(Double.valueOf(strings[0].trim()), Double.valueOf(strings[1].trim()))); //x=lat,y =lon
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return points;
    }
    /**
     * 取出最优解
     * @param spindle
     * @return
     */
    public static List<UserAllocation>  initBastPoint(Spindle spindle) {
        int[] opts = spindle.getOpts();
        List<UserAllocation> userAllocations = new ArrayList<>();
        for (int i = 0; i < opts.length; i++) {
            int flag = Integer.valueOf(opts[i]) ;
            userAllocations.add(spindle.getClients().get(flag));
        }
        return userAllocations;
    }


    public static void getAvageDis(int[][] dis) {
        int disSum = 0;
        double j = dis.length * (dis.length - 1);
        for (int[] di : dis) {
            for (int i : di) {
                disSum += i;
            }
        }
        System.out.println("平均距离：" + disSum / j);

    }


    public static void printInfo(double avgTime, double minDis, double avgDis, double optDis, String name) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name + "平均用时：" + formatDouble1(avgTime) + "(ms)");
        stringBuilder.append("\n");
        double bilv = ((Double.valueOf(avgDis) / optDis) - 1) * 100;
        stringBuilder.append(name + "平均长度：" + formatDouble1(avgDis) + "+(" + formatDouble1(bilv) + "%)");
        stringBuilder.append("\n");
        bilv = ((Double.valueOf(minDis) / optDis) - 1) * 100;
        stringBuilder.append(name + "最短" + formatDouble1(minDis) + "+(" + formatDouble1(bilv) + "%)");


        System.out.println(stringBuilder.toString());

    }



    public static int readAllDis(List<UserAllocation> userAllocations,int[][] distances) {


        int disSum = 0;
      //  System.out.println("算法---------------------开始");
        /*List<String> datya =evalXy();
        for (String s : datya) {
            int i = Integer.valueOf(s) - 1;
            System.out.println(userAllocations.get(i).getAreaInnerId() + 1);

        }*/
        for (int i = 0; i < userAllocations.size() - 1; i++) {

            //System.out.println("di "+(i+1)+" "+userAllocations.get(i).getAreaInnerId());
            int areaId1 = userAllocations.get(i).getAreaInnerId();
            int areaId2 = userAllocations.get(i + 1).getAreaInnerId();
            disSum += distances[areaId1][areaId2];
        }
       // System.out.println("总里程：" + disSum);
        //System.out.println("算法---------------------结束" );
        return disSum;
    }


    public static int readAllDis(int[] userAllocations,int[][] distances) {
        int disSum = 0;
        for (int i = 0; i < userAllocations.length - 1; i++) {

            int areaId1 = Integer.valueOf(userAllocations[i]) ;

            int areaId2 = Integer.valueOf(userAllocations[i + 1]) ;
            disSum += distances[areaId1][areaId2];
        }
        return disSum;
    }

    /**
     * 分割客户点
     * @param oneClients
     * @param otherPartClients
     * @param start  类似仓库点
     * @param end  中点
     * @param allocations
     */
    public static void divideClientPoint(List<UserAllocation> oneClients,
                                         List<UserAllocation> otherPartClients, Point start,
                                         Point end, List<UserAllocation> allocations) {
        Point point = new Point(end.x - start.x,
                end.y - start.y);
        for (UserAllocation userAllocation : allocations) {
            Point point2 = new Point(userAllocation.getUserLocation().x - start.x,
                    userAllocation.getUserLocation().y - start.y);
            double data = point.x * point2.y - point2.x * point.y;
            if (data < 0) { // Point 在 point2 的逆时针
                oneClients.add(userAllocation);
            } else {
                otherPartClients.add(userAllocation);
            }
        }
    }


    public static  List<UserAllocation> divideArea(Map<String, Object> pointMap, List<UserAllocation> clients,
                                                   int[][] dis,String direction) {
        List<Point> MidPoints = (List<Point>) pointMap.get("Mid");
        Point leftPoint = (Point) pointMap.get("left");
        Point rightPoint = (Point) pointMap.get("right");
      //  int dir = Integer.valueOf(direction);
        UserAllocation wareHouse = clients.get(0); //仓库
        clients.remove(clients.size() - 1);
        List<Spindle> users = new ArrayList<>();
        List<UserAllocation> circleUsers = new ArrayList<>(clients);

        int directionI = Integer.valueOf(direction);
        for (int i = 0; i < MidPoints.size(); i++) {
            List<UserAllocation> oneClients = new ArrayList<>();
            List<UserAllocation> otherPartClients = new ArrayList<>();
            divideClientPoint(otherPartClients, oneClients, wareHouse.getUserLocation(), MidPoints.get(i), circleUsers);
            users.add(getOneAngularBisector(dis, leftPoint, wareHouse, MidPoints.get(i), oneClients,Constant.left,directionI));
            leftPoint = MidPoints.get(i);
            circleUsers = otherPartClients;
            if (MidPoints.size() - 1 == i) {
                users.add(getOneAngularBisector(dis, leftPoint, wareHouse, rightPoint, otherPartClients,Constant.right,directionI));
            }
        }
     //   return users.get(1).getClients();
     return  mergeManyAngularBisector(users.get(0), users.get( 1), dis, wareHouse);
       /* for (int i = 0; i < users.size() - 1; i++) {
            System.out.println(readAllDis(mergeManyAngularBisector(users.get(i), users.get(i + 1), dis, wareHouse), dis));
        }
        return users.get(0).getClients();*/
    }



    private static void TEST(List<List<UserAllocation>> users) {
        for (List<UserAllocation> user : users) {
            for (UserAllocation userAllocation : user) {
                System.out.println(userAllocation.getAreaInnerId());
            }
        }

    }

    private static List<UserAllocation> mergeManyAngularBisector(List<List<UserAllocation>> users,int[][] dis) {
        List<UserAllocation> userAllocations = new ArrayList<>();
        /*users.get(1).remove(users.get(1).size() - 1);
        users.get(0).remove(0);
        users.get(1).addAll(users.get(0));
        System.out.println(readAllDis(users.get(1), dis));
        users.remove(0);*/
        Collections.reverse(users.get(1));
        users.get(0).remove(users.get(1).size() - 1);
        users.get(1).remove(0);
        users.get(0).addAll(users.get(1));
        System.out.println(readAllDis(users.get(0), dis));
        users.remove(1);
      return userAllocations;

    }


    /**
     * 合并两个客户点集合
     * @param users1
     * @param users2
     * @param distances
     * @return
     */
    private static List<UserAllocation> mergeManyAngularBisector(List<UserAllocation> users1,
                                                                 List<UserAllocation> users2, int[][] distances) {
        List<UserAllocation> oneUsers = new ArrayList<>();

        oneUsers.add(users1.get(1)); //0
        oneUsers.add(users1.get(users1.size() - 2)); //1

        List<UserAllocation> otherUsers = new ArrayList<>();
        otherUsers.add(users2.get(1)); //0
        otherUsers.add(users2.get(users2.size() - 2));//1
        double value = Double.MAX_VALUE;
        int oneFlag = 0, otherFlag = 0;
        for (int i = 0; i < oneUsers.size(); i++) {
            for (int i1 = 0; i1 < otherUsers.size(); i1++) {
                double curValue = judgeTwoClientDis(oneUsers.get(i), otherUsers.get(i1), value, distances);
                if (curValue != -1) {
                    oneFlag = i;
                    otherFlag = i1;
                    value = curValue;
                }
            }
        }
        return  judgeMergeWhichPath(oneFlag, otherFlag, users1, users2);
    }

    private static List<UserAllocation> mergeManyAngularBisector(Spindle spindle1,
                                                                 Spindle spindle2, int[][] distances,UserAllocation wareHouse) {

        int flag = 3;
        List<UserAllocation> oneClients = new ArrayList<>();
      //  int leftdis = readAllDis(spindle1.getClients(), distances);

        //断开仓库
        spindle1.getClients().remove(spindle1.getClients().size() - 1);
        spindle2.getClients().remove(0);

       // oneClients.add(spindle1.getClients().get(spindle1.getClients().size() - 1));
        for (int i = spindle1.getClients().size() - 1; i > spindle1.getClients().size() - 1 - flag; i--) {
            oneClients.add(spindle1.getClients().get(i));
        }
        List<UserAllocation> otherClients = new ArrayList<>();
        for (int i = 0; i < flag; i++) {
            otherClients.add(spindle2.getClients().get(i));
        }


     return    judgeWhichConnectType
                (oneClients,otherClients,distances,spindle1.getClients(),spindle2.getClients());

    }


    private static List<UserAllocation> judgeWhichConnectType(List<UserAllocation> oneClients, List<UserAllocation> otherClients,
                                               int[][] distances,
                                              List<UserAllocation> leftSpindleClient, List<UserAllocation> rightSpindleClient) {

        List<UserAllocation> others = new ArrayList<>();
        List<UserAllocation> ones = new ArrayList<>();
        double maxValue = Double.MAX_VALUE;
        List<UserAllocation> finnal = new ArrayList<>();
        for (UserAllocation oneClient : oneClients) {
            ones.add(oneClient);

            List<UserAllocation> temleftClient = new ArrayList<>();
            double LtemDis = getTemDis(distances, leftSpindleClient, ones, temleftClient);

            for (UserAllocation otherClient : otherClients) {
              double  connectDis =  distances[oneClient.getAreaInnerId()][otherClient.getAreaInnerId()];
                others.add(otherClient);
                List<UserAllocation> temRightClient = new ArrayList<>();
                double RtemDis = getTemDis(distances, rightSpindleClient, others, temRightClient);
                double sumDis = LtemDis + connectDis + RtemDis;
                if (sumDis < maxValue) {
                    maxValue = sumDis;
                    System.out.println("L用户" + oneClient.getAreaInnerId() + "R用户" + otherClient.getAreaInnerId() + "SUM" + sumDis);
                    finnal = new ArrayList<>();
                    finnal.addAll(temleftClient);
                    finnal.addAll(temRightClient);
                }
            }
        }
        return finnal;
    }

    private static double getTemDis(int[][] distances, List<UserAllocation> rightSpindleClient, List<UserAllocation> others, List<UserAllocation> temRightClient) {


        temRightClient.addAll(rightSpindleClient);
        return getOneDis(others, distances, temRightClient);
    }


    private static double getOneDis( List<UserAllocation> other,
                                    int[][] distances,
                                     List<UserAllocation> rightTemClients) {

      //  UserAllocation comOthers = other.get(other.size() - 1);
       // leftoneDis += distances[one.getAreaInnerId()][comOthers.getAreaInnerId()]; //连起来了

       // rightTemClients.add(comOthers);
        judgeWhichLocaction(other, rightTemClients, distances);
        int rightDis = readAllDis(rightTemClients, distances);
        return rightDis;
       /* double temDis = leftoneDis + rightDis; //临时总距离
        return temDis;*/

    }

    /**
     * 判断释放点所插入的位置
     * @param other
     * @param spindleClient
     * @param dis
     */
    private static void judgeWhichLocaction(List<UserAllocation> other, List<UserAllocation> spindleClient,int[][] dis) {
        for (int i = 0; i < other.size() - 1; i++) {
            spindleClient.remove(other.get(i));
            int compareValue = Integer.MAX_VALUE;
            int addFlag = 0;
            for (int i1 = 0; i1 < spindleClient.size() - 1; i1++) {
                int getDis = compareDis(spindleClient.get(i1).getAreaInnerId(),
                        other.get(i).getAreaInnerId(), spindleClient.get(i1 + 1).getAreaInnerId(),
                        dis, compareValue);
                if (getDis < compareValue) {
                    compareValue = getDis;
                    addFlag = i1 + 1;
                }
            }
            spindleClient.add(addFlag, other.get(i));
        }
    }


    /**
     * 返回点到点之间的距离
     * @param innerId1
     * @param innerId2
     * @param innerId3
     * @param distances
     * @param compareValue
     * @return
     */
    private static int compareDis(int innerId1, int innerId2, int innerId3, int[][] distances, int compareValue) {
        return  distances[innerId1][innerId2] + distances[innerId2][innerId3];
    }



    private static int getRemoveDis(List<UserAllocation> other, int[][] distances, int wareHouseFlag) {
        int leftOther = 0;
        leftOther += distances[wareHouseFlag][other.get(0).getAreaInnerId()];
        for (int i = 0; i < other.size() - 1; i++) {
            leftOther += distances[other.get(i).getAreaInnerId()][other.get(i + 1).getAreaInnerId()];
        }
        return leftOther;
    }


    /**
     *  判断按照哪种方式合并
     * @param oneFlag
     * @param otherFlag
     * @param oneClients
     * @param otherClients
     * @return
     */
    private static List<UserAllocation> judgeMergeWhichPath(int oneFlag, int otherFlag,
                                            List<UserAllocation> oneClients, List<UserAllocation> otherClients) {

        switch (oneFlag) {
            case 0: //TODO
                if (otherFlag == 0) {
                    Collections.reverse(otherClients);
                    removeClients(otherClients, oneClients);
                    return mergeClients(otherClients, oneClients);
                } else {

                    removeClients(otherClients, oneClients);
                    return mergeClients(otherClients, oneClients);
                }
            case 1:
                if (otherFlag == 0) {

                    removeClients(oneClients, otherClients);
                    return mergeClients(oneClients, otherClients);
                } else {
                    Collections.reverse(otherClients);
                    removeClients(oneClients, otherClients);
                    return mergeClients(oneClients, otherClients);

                }
        }
        return new ArrayList<>();

    }

    private static void removeClients(List<UserAllocation> oneClients, List<UserAllocation> otherClients) {
        oneClients.remove(oneClients.size() - 1);
        otherClients.remove(0);
    }

    private static double judgeTwoClientDis(UserAllocation user_1, UserAllocation user_3, double value,int[][] distances) {
        int oneDIs = AngularBisector.getTwoClientsDistance(user_1, user_3, distances);
        return oneDIs < value ? oneDIs : -1;

    }


    private static List<UserAllocation> mergeClients(List<UserAllocation> oneClients, List<UserAllocation> otherClients){
        oneClients.addAll(otherClients);
        otherClients.clear();
        otherClients.addAll(oneClients);
        return oneClients;
    }



    /**
     * 获取单个扇区内容
     * @param dis
     * @param leftPoint
     * @param wareHouse
     * @param midPoint
     * @param oneClients
     * @return
     */
    private static Spindle getOneAngularBisector(int[][] dis, Point leftPoint, UserAllocation wareHouse,
                                                 Point midPoint, List<UserAllocation> oneClients,int circleType,int direction ) {
        if (!oneClients.contains(wareHouse)) {
            oneClients.add(wareHouse);
        }
        System.out.println(oneClients.size());
        int wareHouseFlag = addWareHouse(oneClients, wareHouse);
        Spindle spindle = new Spindle(oneClients, wareHouseFlag);
        AngularBisectorService angularBisectorService = new AngularBisectorService(dis, spindle, leftPoint, midPoint,direction);
        List<UserAllocation> outData = new ArrayList<>();
        double clientsdis = angularBisectorService.planPathWithCommon(outData,circleType);
        return new Spindle(outData,clientsdis);
    }


    /**
     * 添加仓库
     * @param userAllocations
     * @param wareHouse
     * @return
     */
    private static int  addWareHouse(List<UserAllocation> userAllocations, UserAllocation wareHouse) {
        for (int i = 0; i < userAllocations.size(); i++) {
            if (userAllocations.get(i).getAreaInnerId() == wareHouse.getAreaInnerId()) {
                return wareHouse.getAreaInnerId();
            }
        }
        userAllocations.add(wareHouse);
        return wareHouse.getAreaInnerId();
    }




    /**
     * 、解析手动创建的点
     * @param Lpoint
     * @param Rpoint
     * @param Mpoint  请按照从左到右排列
     * @return
     */
    public static Map<String, Object> setreturnPoints( String Lpoint,String Rpoint,String Mpoint) {
        JSONArray array = JSON.parseArray(Mpoint);
        Map<String, Object> map = new HashMap<>();
        List<Point> midPoints = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            Point point = evalPoint(object);
            midPoints.add(point);
        }
        map.put("left", evalPoint(Lpoint));
        map.put("right", evalPoint(Rpoint));
        map.put("Mid", midPoints);
        return map;
    }

    private static Point evalPoint(String str) {
        JSONObject object = JSON.parseObject(str);
        return evalPoint(object);
    }

    private static Point evalPoint( JSONObject object) {
        JSONObject endPoint = object.getJSONObject("end");
        return new Point(endPoint.getDouble("lng"), endPoint.getDouble("lat"));
    }






}
