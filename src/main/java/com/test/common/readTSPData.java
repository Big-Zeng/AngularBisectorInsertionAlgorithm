package com.test.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.sun.scenario.effect.impl.prism.PrImage;
import com.test.model.Point;
import com.test.model.TspData;
import com.test.model.UserAllocation;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.print.Doc;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZXF on 2018-12-08.
 */
public class readTSPData {


    public  static  void readATspData(String path){
        readFileByChars(path);
    }

    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    /**
     * 读取文件
     * @param file
     * @return
     */
    public static  String  readText(CommonsMultipartFile file){
        try {
            InputStream is=file.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            BufferedReader bf = new BufferedReader(isr);
            String content = "";
            StringBuilder sb = new StringBuilder();

            while((content = bf.readLine()) != null)
            {
                sb.append(content);
                sb.append("\n");
            }
            bf.close();
          return sb.toString();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return "";

    }


    public static  String  readText(File file){
        try {
            InputStream is= new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            BufferedReader bf = new BufferedReader(isr);
            String content = "";
            StringBuilder sb = new StringBuilder();

            while((content = bf.readLine()) != null)
            {
                sb.append(content);
                sb.append("\n");
            }
            bf.close();
            return sb.toString();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return "";


    }

    /**
     * 处理非对称点
     * @param text
     * @throws Exception
     */
    public static void divideATSP(String text) throws Exception {

        TspData tspData = initTspData(text);
        handleAtspDifEdgeWeightFormat(tspData);

    }


    /**
     *  构造TspData
     * @param text
     * @return
     */
    public static TspData initTspData(String text){
        String[] datas = text.split("\n");
        TspData tspData = new TspData();
        int valueStart = 0;
        int valueEnd = 0;
        for (int i = 0; i < datas.length; i++) {
            String[] values = datas[i].split(":");
            switch (values[0].trim()) {
                case "EDGE_WEIGHT_FORMAT":
                    tspData.EDGE_WEIGHT_FORMAT = values[1].trim();
                    break;
                case "DIMENSION":
                    tspData.DIMENSION = Integer.valueOf(values[1].trim());
                    break;
                case "NAME":
                    tspData.NAME = values[1].trim();
                    break;
                case "EDGE_WEIGHT_SECTION":
                    valueStart = i;
                    break;
                case "EDGE_WEIGHT_TYPE":
                    tspData.EDGE_WEIGHT_TYPE = values[1].trim();
                    break;
                case "BEST_RESULT":
                    tspData.BEST_RESULT = Integer.valueOf(values[1].trim());
                    break;
                case "END":
                    valueEnd = i;
                    break;
            }
        }
        tspData.setEDGE_WEIGHT_SECTION(valueStart, valueEnd, datas);
        return tspData;
    }

    /**
     * 分不同情况 详情见 tsplib网址
     * @param tspData
     */
    private static void handleAtspDifEdgeWeightFormat(TspData tspData) {

        switch (tspData.EDGE_WEIGHT_FORMAT) {
            case "FULL_MATRIX":
                handleFullMatrix(tspData.EDGE_WEIGHT_SECTION,tspData.DIMENSION);
                break;
            case "UPPER_ROW":
                break;

        }
    }

    /**
     * 全矩阵
     * @param value
     * @param DIMENSION
     * @return
     */
    public static int[][] handleFullMatrix(String value,int DIMENSION) {
        String[] distances = value.trim().split("\n");
        List<String> strings = new ArrayList<>();
        for (String distance : distances) {
            String[] diss = distance.trim().split(" ");
            for (String dis : diss) {
                strings.add(dis);
            }
        }

        List<String> datas = removeSpaceData(strings);
        int[][] dis = setDis(DIMENSION, datas);
        return dis;
    }

    /**
     * 上三角阵（行优先，没有对角线）
     * @param valueStart
     * @param valueEnd
     * @param content
     * @param DIMENSION
     * @return
     */
    public static int[][] handleUPPERRow(int valueStart,int valueEnd,String[] content, int DIMENSION) {
        int j = 0;
        int[][] dis = new int[DIMENSION][DIMENSION];
        for (int i = valueStart + 1; i < valueEnd; i++, j++) {
            getDisByDiagRow(content[i], DIMENSION, j, dis);
        }
        for (int i = 0; i < DIMENSION; i++) {
            if (i == j) {
                System.out.print(0);
                System.out.print(" ");
            } else if (dis[j][i] == 0 && dis[i][j] != 0) {
                System.out.print(dis[i][j]);
                System.out.print(" ");
            }
        }
        return dis;
    }

    private static String[] changeLOWER_DIAG_ROW(List<String> content,int DIMENSION) {
        String[] strings = new String[DIMENSION];
        int i = 0;
        StringBuffer sb = new StringBuffer();
        for (String s : content) {
            sb.append(s.trim());
            if (s.trim().equals("0")) {
                strings[i] = sb.toString();
                sb = new StringBuffer();
                i++;
                if (i == DIMENSION) {
                    break;
                }
            } else {
                sb.append(" ");
            }
        }
        return strings;
    }



    private static void getDisByDiagRow(String s, int DIMENSION, int j, int[][] dis) {
        String[] value = s.split(" ");
        List<String> datas = readTSPData.removeSpaceData(value);
        int k = 0;
        for (int i1 = 0; i1 < DIMENSION; i1++) {
            if (j == i1) {
                System.out.print("0");
                System.out.print(" ");
                dis[j][i1] = 0;
            } else {
                if (dis[i1][j] != 0) {
                    dis[j][i1] = dis[i1][j];
                    System.out.print(dis[i1][j]);
                    System.out.print(" ");
                } else {
                    dis[j][i1] = Integer.valueOf(datas.get(k));
                    System.out.print(dis[j][i1]);
                    System.out.print(" ");
                    k++;
                }
            }
        }
        System.out.print("\n");
    }

    public static int[][] handleLOWER_DIAG_ROW(int valueStart, int valueEnd, String[] content, int DIMENSION) {

        int[][] dis = new int[DIMENSION][DIMENSION];
        List<String> newContent = new ArrayList<>();
        for (int i = valueStart + 1; i < valueEnd; i++) {
            newContent.add(content[i]); //删除不必要的
        }
        int j = 0;
        content = changeLOWER_DIAG_ROW(newContent, DIMENSION); //合并成下三角


        for (int i = content.length - 1; i >= 0; i--,j++) {
            getDisByDiagRow(content[i], DIMENSION, j, dis);
        }

        for (int i = dis.length - 1; i >= 0; i--) {
            for (int i1 = dis[i].length - 1; i1 >= 0; i1--) {
                System.out.print(dis[i][i1]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }


        return dis;
    }





    /**
     * 设置点的距离
     * @param DIMENSION
     * @param datas
     * @return
     */
    private static int[][] setDis(int DIMENSION, List<String> datas) {
        int[][] dis = new int[DIMENSION][DIMENSION];
        int j = 0;
        int k =0;
        for (int i = 0; i < datas.size(); i++,k++) {
            if (i != 0 && i % DIMENSION == 0) {
                k=0;
                j++;
            }
            double value = Double.valueOf(datas.get(i));
            dis[j][k] = (int) value;
        }
        return dis;
    }

    /**
     * 去掉无用空格
     * @param distances
     * @return
     */
    public static List<String> removeSpaceData(String[] distances) {
        List<String> datas = new ArrayList<>();
        for (String distance : distances) {
            distance = distance.replace("\n", "");
            if (distance.trim().equals("")) {
                continue;
            }
            datas.add(distance);
        }
        return datas;
    }
    public static List<String> removeSpaceData(List<String> distances) {
        List<String> datas = new ArrayList<>();
        for (String distance : distances) {
            distance = distance.replace("\n", "");
            if (distance.trim().equals("")) {
                continue;
            }
            datas.add(distance);
        }
        return datas;
    }
    /**
     * 读取 ATSP OR TSP 数据
     * @param files
     * @return
     */
    public static Map<String, Object> readATspDir(CommonsMultipartFile[] files) {

        Map<String, Object> map = new HashMap<String, Object>();
        List<UserAllocation> userAllocations = new ArrayList<>();
        int[][] Dis = null;
        int size = 0;
        for (CommonsMultipartFile file : files) {
                if (file.getOriginalFilename().contains(".tsp")
                        ||file.getOriginalFilename().contains(".atsp")) {
                    String str = readText(file);
                    TspData tspData = initTspData(str);
                    size = tspData.DIMENSION;
                    map.put("tspData", tspData);
                }
        }
        for (CommonsMultipartFile file : files) {
            readOtherInfo(file, size, userAllocations, map, Dis);
        }

        return map;
    }


    private static void readOtherInfo(CommonsMultipartFile file, int size,
                                      List<UserAllocation> userAllocations,Map<String, Object> map,int[][] Dis) {
        if (file.getOriginalFilename().contains("_xy")) {
            userAllocations = evalXy(file);
            String clients = JSON.toJSONString(userAllocations);
            map.put("points", userAllocations);
            map.put("pointsJson", clients);
        } else {
            if (file.getOriginalFilename().contains("_d")) {
                String str = readText(file);
                Dis = handleFullMatrix(str, size);
                map.put("dis", Dis);
            }
            if (file.getOriginalFilename().contains("_opt")) { //最佳值
                String str = readText(file);
                String[] opts = str.split("\n");
                map.put("opts", opts);
            }
        }
    }


    public static void readOtherInfo(File file, int size,
                                      List<UserAllocation> userAllocations,Map<String, Object> map,int[][] Dis) {
        if (file.getName().contains("_xy")) {
            userAllocations = evalXy(file);
            String clients = JSON.toJSONString(userAllocations);
            map.put("points", userAllocations);
            map.put("pointsJson", clients);
        } else {
            if (file.getName().contains("_d")) {
                String str = readText(file);
                Dis = handleFullMatrix(str, size);
                map.put("dis", Dis);
            }
            if (file.getName().contains("_opt")) { //最佳值
                String str = readText(file);
                String[] opts = str.split("\n");
                map.put("opts", opts);
            }
        }
    }


    /**
     * 解析XY
     * @param file
     * @return
     */
    private static List<UserAllocation> evalXy(CommonsMultipartFile file) {
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        List<UserAllocation> userAllocations  = new ArrayList<>();
        try {
            InputStream IS = file.getInputStream();
            streamReader = new InputStreamReader(IS);
            bufferedReader = new BufferedReader(streamReader);
            String str = "";
            int i = 0;
            while ((str = bufferedReader.readLine()) != null) {
                String[] strings = str.split(" ");
                List<String> datas = removeSpaceData(strings);
                Point point = new Point(Double.valueOf(datas.get(0)), Double.valueOf(datas.get(1)));
                userAllocations.add(new UserAllocation(point, i));
                i++;
            }
            bufferedReader.close();
            streamReader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return userAllocations;
    }




    /**
     * 解析XY
     * @param file
     * @return
     */
    private static List<UserAllocation> evalXy(File file) {
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        List<UserAllocation> userAllocations  = new ArrayList<>();
        try {
            InputStream IS =  new FileInputStream(file);
            streamReader = new InputStreamReader(IS);
            bufferedReader = new BufferedReader(streamReader);
            String str = "";
            int i = 0;
            while ((str = bufferedReader.readLine()) != null) {
                String[] strings = str.split(" ");
                List<String> datas = removeSpaceData(strings);
                Point point = new Point(Double.valueOf(datas.get(0)), Double.valueOf(datas.get(1)));
                userAllocations.add(new UserAllocation(point, i));
                i++;
            }
            bufferedReader.close();
            streamReader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return userAllocations;
    }





}
