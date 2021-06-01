package com.test.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.encoding.ContentType;
import com.test.common.Constant;
import com.test.common.readTSPData;
import com.test.model.*;
import com.test.service.ACO.*;
import com.test.service.ACO.Timer;
import com.test.service.ATspService;
import com.test.service.AngularBisector;
import com.test.service.AngularBisectorService;
import com.test.service.BranchBound.BranchAndBoundMain;
import com.test.service.BranchBound.branch.BranchBoundMap;
import com.test.service.BranchBound.branch.BranchBoundTSP;
import com.test.service.BranchBound.branch1.*;
import com.test.service.BranchBound.branch3.BranchBound;
import com.test.service.GA.GA.Preset;
import com.test.service.GA.GA2.GA;
import com.test.service.GA.GeneticAlgorithm;
import com.test.service.GA.GeneticAlgorithmMethod;
import com.test.service.GA.GeneticTest;
import com.test.service.NearestInsertion.*;
import com.test.service.TSPLibService;
import com.test.service.otherACO.Graph;
import com.test.service.otherACO.Import;
import com.test.service.otherACO.TravelingSalesman;
import org.apache.commons.lang.ArrayUtils;
import org.jgap.*;
import org.jgap.event.EventManager;
import org.jgap.impl.*;
import org.jgap.impl.salesman.Salesman;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.test.common.readTSPData.readATspDir;
import static com.test.service.ACO.AcoTsp.*;
import static com.test.service.ACO.InOut.set_default_parameters;
import static com.test.service.ACO.Tsp.compute_nn_lists;
import static com.test.service.ATspService.printInfo;
import static com.test.service.ATspService.readAllDis;
import static com.test.service.BranchBound.branch3.BranchBound.INF;
import static com.test.service.GA.GA1.TSP.initMain;


/**
 * Created by ZXF on 2018-12-08.
 */
@Controller
@RequestMapping("TSPTest")
public class TSPTestController {

    @RequestMapping("ATSP")
    public String toAtsp(HttpServletRequest request, Model model){
        return "TSP";
    }


    @RequestMapping("readATSP")
    public String readTsp(@RequestParam("uploadFile") CommonsMultipartFile file) {
        //  readTSPData.readFileByChars(path);
        String str = readTSPData.readText(file);
        try {
            readTSPData.divideATSP(str);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "TSP";
    }


    private int[][] distances;
    TspData tspData; //便于测试


    /**
     * 点击上传文件
     *

     * @param model
     * @return
     */
    @RequestMapping("readATSPDir")
    public String readTspDir(@RequestParam("file") CommonsMultipartFile[] filess, ModelMap model) {
        ABAtestDatas.clear();
        FIA2testDatas.clear();

         List<CommonsMultipartFile[]> lists = getAllFiles(filess);
        for(CommonsMultipartFile[] list : lists) {
        Map<String, Object> maps = readATspDir(list);
        List<UserAllocation> points = (List<UserAllocation>) maps.get("points");
        distances = (int[][]) maps.get("dis");
        String[] opts = (String[]) maps.get("opts");
        tspData = (TspData) maps.get("tspData");

        Map<String, Object> map = getMaxBastData(points, distances, opts, tspData);
        List<UserAllocation> bastUsers = (List<UserAllocation>) map.get("bastUsers");
        Spindle bastSpindle = (Spindle) map.get("bastSPindle");

        //List<UserAllocation> bastData = ATspService.judgeDis(distances, bastSpindle, bastUsers, tspData.BEST_RESULT);
        model.addAttribute("keyPoints", JSON.toJSONString(bastSpindle.getKeyPoints()));
        model.addAttribute("item", maps.get("pointsJson"));
        model.addAttribute("result", JSON.toJSONString(bastUsers));
         }
        TestDataModel.sortPrint(ABAtestDatas);
        System.out.println("        ");
        // TestDataModel.sortPrint(FIA2testDatas);
        // System.out.println("偏差" + s);
        return "ShowTsp";
    }


    /**
     * 文件夹内文件读取
     * @param commonsMultipartFiles
     * @return
     */
    public List<CommonsMultipartFile[]> getAllFiles(CommonsMultipartFile[] commonsMultipartFiles) {
        File dir = new File("F:\\论文文档\\Paper\\data\\TSP");
        List<CommonsMultipartFile[]> list = new ArrayList<>();
        for (File file : dir.listFiles()) {
            System.out.println("fileName" + file.getName());
            if (file.isDirectory()) {
                CommonsMultipartFile[] commonsMultipartFiles1 = new CommonsMultipartFile[file.listFiles().length];
                for (int i = 0; i < file.listFiles().length; i++) {
                    for (CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles) {
                        if (file.listFiles()[i].getName().contains(commonsMultipartFile.getOriginalFilename())) {
                            commonsMultipartFiles1[i] = commonsMultipartFile;
                            break;
                        }
                    }
                }
                list.add(commonsMultipartFiles1);
            }
        }
        return list;
    }
    /**
     * 算法开始 并比较
     * @param points
     * @param dis
     * @param opts
     * @param tspData
     * @return
     */
    private Map<String, Object> getMaxBastData( List<UserAllocation> points,
                                                int[][] dis, String[] opts, TspData tspData) {


        List<UserAllocation> bastUsers = new ArrayList<>();
        int bestdis = 0;
        if (opts != null) {
            int[] optI = setOptValue(opts);
            bestdis = readAllDis(optI, dis);
        }else
            bestdis = tspData.BEST_RESULT;
        //test(dis);
        //caculateBranchTime(points, dis);
        int[] data = null;
        /*Map<String, Object> convexHullMap = new HashMap();
        convexHullMap = caculateConvexHull(points, dis, bestdis);
        int[] hull =(int[]) convexHullMap.get("hulls");
        bastUsers = (List<UserAllocation>) convexHullMap.get("users");

        List<UserAllocation> userAllocations = new ArrayList<>();
        for (UserAllocation bastUser : bastUsers) {
            userAllocations.add(bastUser.clone());
        }*/

         //caculateNearestNeighbor(points, dis, bestdis, tspData.NAME, tspData.DIMENSION);
        //caculateACOTime(dis, tspData.DIMENSION, points, bestdis);
        // JGAPTSP(dis, points);
        //testConvergent(dis, points, bestdis);

        /*GeneticAlgorithmMethod GM = new GeneticAlgorithmMethod();
        System.out.println("GA：" + GM.geneticAlg(points, dis));*/
        System.out.println("Name:" + tspData.NAME);
        caculateFasterInsertion(points,bestdis,dis);
       // Map<String, Object> map = new HashMap<>();
         Map<String, Object> map = testOneStartPoint(points, opts, dis, bestdis);


      /*  bastUsers = caculateGATime(dis, points, bestdis, hull);
        List<UserAllocation> GApoints = new ArrayList<>();
        for (UserAllocation bastUser : bastUsers) {
            GApoints.add(bastUser.clone());
        }


         map.replace("bastUsers", GApoints);*/
        // map.replace("bastUsers", userAllocations);
        return  map;

        //System.out.println("AD平均总用时:" + formatDouble1(sumTime / 1) + "(ms)");

    }




    List<TestDataModel> ABAtestDatas = new ArrayList<>();
    List<TestDataModel> FIA2testDatas = new ArrayList<>();

    private  double  printPianChaAndAvgDis(double sum, double times, double bestDis) {
        double value = sum / times;
        return  (value - bestDis) / bestDis * 100;
    }


    /**
     * ABIA Main method
     *
     * @param points
     * @param dis
     * @param bestdis
     */
    public Map<String, Object> ABATest(List<UserAllocation> points, String[] opts, int[][] dis, int bestdis) {
        Map<String, Object> map = new HashMap<>();
        long sumTime = 0;
        double times = 1;
        double maxValue = Double.MAX_VALUE;
        List<UserAllocation> bastUsers = new ArrayList<>();
        Spindle bastSPindle = new Spindle();
        long start = System.currentTimeMillis();
        for (int i = 0; i < tspData.DIMENSION; i++) {
            double oneMinDis = Double.MAX_VALUE;
            Spindle sPindle = new Spindle(points, i);
            for (int z = 0; z < 4; z++) {
                //  AngularBisector angularBisector =new AngularBisector(sPindle,dis ,32);
                AngularBisectorService angularBisectorService = new AngularBisectorService(dis, sPindle, z);

                List<UserAllocation> clients = new ArrayList<>();
                double value = angularBisectorService.planPathWithCommon(clients, Constant.NONE);
                if (value <= maxValue && value != 0) {
                    bastSPindle = sPindle;
                    maxValue = value;
                    bastUsers = clients;
                }
            }
        }
        long end = System.currentTimeMillis();
        sumTime += (end - start);
        printInfo(sumTime / times, maxValue, maxValue, Double.valueOf(bestdis), "ABA");
        if (opts != null)
            bastSPindle.setOpts(opts);
        map.put("bastUsers", bastUsers);
        map.put("bastSPindle", bastSPindle);
        return map;
    }

    /**
     * 测试一个起点与全部点的
     * @param points
     * @param opts
     * @param dis
     * @param bestdis
     * @return
     */
    public Map<String, Object> testOneStartPoint(List<UserAllocation> points, String[] opts, int[][] dis, int bestdis) {
        Map<String, Object> map = new HashMap<>();
        long sumTime = 0;
        double maxValue = Double.MAX_VALUE;
        List<UserAllocation> bastUsers = new ArrayList<>();
        Spindle bastSPindle = new Spindle();
        double worseDis = Double.MIN_VALUE;
        double averageDis = 0;
        for (int i = 0; i < tspData.DIMENSION; i++) {
            long start = System.currentTimeMillis();
            Spindle sPindle = new Spindle(points, i);
            List<UserAllocation> clients = new ArrayList<>();
            double value = getOneABIADis(sPindle, dis, clients);
            long end = System.currentTimeMillis();
            sumTime += (end - start);
            if (value <= maxValue && value != 0) {
                bastSPindle = sPindle;
                maxValue = value;
                bastUsers = clients;
            }
            if (value > worseDis) {
                worseDis = value;
            }
            averageDis += value;

        }

        int avgDis = (int) (averageDis / tspData.DIMENSION);
        DecimalFormat df = new DecimalFormat("######0.00");

        String bilv1 = df.format(((Double.valueOf(avgDis) / bestdis) - 1) * 100)+"%";

        String bilv2 = df.format(((Double.valueOf(maxValue) / bestdis) - 1) * 100)+"%";
        String bilv3 = df.format(((Double.valueOf(worseDis) / bestdis) - 1) * 100)+"%";


      //  System.out.println("平均距离：" + avgDis + "," + "最好距离:" + maxValue + ",最差距离:" + worseDis);
       // System.out.println("平均偏差:" + bilv1 + "最好偏差:" + bilv2 + "最差偏差:" + bilv3);
        //System.out.println("总时间:" + sumTime + ",平均时间" + (double) (sumTime / tspData.DIMENSION));

        if (opts != null)
            bastSPindle.setOpts(opts);
        map.put("bastUsers", bastUsers);
        map.put("bastSPindle", bastSPindle);
        return map;
    }


    private double getOneABIADis(Spindle sPindle, int[][] dis, List<UserAllocation> clients) {
        double bestDis = Double.MAX_VALUE;
        for (int z = 0; z < 4; z++) {
            AngularBisectorService angularBisectorService = new AngularBisectorService(dis, sPindle, z);
            double value = angularBisectorService.planPathWithCommon(clients, Constant.NONE);
            if (value <= bestDis && value != 0) {
                bestDis = value;
            }
        }
        return bestDis;
    }








    public void testDis(int[][] distances) {
        for (int i = 0; i < distances.length ; i++) {
            for (int j = i+1; j < distances.length; j++) {
                if (distances[i][j] == distances[j][i]) {
                    distances[j][i] = distances[j][i] + 1;
                }
            }
        }
        testDis1(distances);

    }
    public void testDis1(int[][] distances) {
        for (int i = 0; i < distances.length ; i++) {
            for (int j = 0; j < distances.length; j++) {
                if (distances[i][j] == distances[j][i] && i!=j) {
                    System.out.println("1");
                }
            }
        }

    }

    public  int[]  setOptValue(String[] opts) {
        int[] optI = new int[opts.length];
        for (int i = 0; i < opts.length; i++) {
            optI[i] = Integer.valueOf(opts[i].trim()) - 1;//-1
        }
        return optI;
    }

    /**
     * 测试遗传算法收敛的终止代数
     * @param dis
     * @param userAllocations
     * @param bestDis
     */
    public void testConvergent(int[][] dis, List<UserAllocation> userAllocations, int bestDis) {
        int times = 10;
        double deviations = 0;
        double convergenceRate = 0;
        double stds = 0;
        double time = 0;
        double avgDeviaions = 0;
        for (int i = 0; i < times; i++) {
            //double[] values = caculateGATime(dis, userAllocations, bestDis);
            double[] values = new double[4];

            deviations += values[0];
            convergenceRate += values[1];
            stds += values[2];
            time += values[3];
            avgDeviaions += values[4];
        }
        System.out.println("最好值偏差：" + deviations / times);
        System.out.println("平均值偏差:" + avgDeviaions / times);
        System.out.println("收敛率：" + convergenceRate / times);
        System.out.println("标准差：" + stds / times);
        System.out.println("用时:" + time / times);



    }


    private List<UserAllocation> caculateGATime(int[][] dis,
                                                List<UserAllocation> userAllocations, int bestDis,int[] hull) {
        //initMain(userAllocations, dis);


        double sumTime = 0;
        double sumLength = 0;
        double minLength = Double.MAX_VALUE;
        int times = 20;
        double[] allArray = new double[times];
        int soulianSize = 0;

        List<UserAllocation> bestUserAllocations = new ArrayList<>();
        for (int j = 0; j < times; j++) {
            long start = System.currentTimeMillis();

            List<UserAllocation> userAllocations1 = UserGA(dis, userAllocations);
            double value = ATspService.readAllDis(userAllocations1, dis);
            if (value <= bestDis) {
                compareLocation(hull,dis, userAllocations1);
                bestUserAllocations = userAllocations1;
                /*System.out.println("最优解  开始");
                for (UserAllocation userAllocation : userAllocations1) {
                    System.out.println(userAllocation.getAreaInnerId());
                }
                System.out.println("最优解  end");
                bestUserAllocations = userAllocations1;*/
                // System.out.println("存在最优解：" + bestDis);
                soulianSize++;
            }
            allArray[j] = value;
            sumLength += value;
            if (value < minLength) {
                minLength = value;
            }

            long end = System.currentTimeMillis();
            long time = (end - start);
            sumTime += time;
        }
        double deviation = (minLength - bestDis) / bestDis * 100;
        double convergenceRate = (double) soulianSize / times * 100;
        double std = testStand(allArray, sumLength, times);
        double avgDeviation = (sumLength / times - bestDis) / bestDis * 100;


        //System.out.println("偏差 " + (double) (minLength - bestDis)/ bestDis * 100 + "%");

        //System.out.println("收敛率" + (double) soulianSize / times * 100 + "%");

        System.out.println("标准差：" + std);

      /*  System.out.println("GA平均用时:" + formatDouble1(sumTime / times)+"(ms)");
        System.out.println("GA平均长度:" + formatDouble1(sumLength / times));
        System.out.println("GA最短:" + formatDouble1(minLength));*/
        printInfo(sumTime / times, minLength, sumLength / times, Double.valueOf(bestDis), "GA");

        //return new double[]{deviation, convergenceRate, std, sumTime / times,avgDeviation};
        return bestUserAllocations;
    }

    /**
     * 比较凸包的顺序是否与最优解相似
     */

    private void compareLocation(int[] hulls,int[][] dis, List<UserAllocation> userAllocations) {
        List<Integer> flags = new ArrayList<>();
        for (int hull : hulls) {
            for (int i = 0; i < userAllocations.size(); i++) {
                if (userAllocations.get(i).getAreaInnerId() == hull) {
                    flags.add(i);
                    break;
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < hulls.length - 1; i++) {
           /* System.out.println(hulls[i] + "到" + hulls[i + 1] + "dis:" + dis[hulls[i]][hulls[i + 1]]);*/
            sum += dis[hulls[i]][hulls[i + 1]];
        }
        sum += dis[hulls[hulls.length - 1]][hulls[0]];
       /* System.out.println(hulls[hulls.length - 1] + "到" + hulls[0] + "dis:" + dis[hulls[hulls.length - 1]][hulls[0]]);
        System.out.println("  ");*/
        Collections.sort(flags);

        int sum2 = 0;
        for (int i = 0; i < flags.size() - 1; i++) {
           /* System.out.println(userAllocations.get(flags.get(i)).getAreaInnerId() + "到" + userAllocations.get(flags.get(i+1)).getAreaInnerId()
                    + "dis" +dis[userAllocations.get(flags.get(i)).getAreaInnerId()][userAllocations.get(flags.get(i + 1)).getAreaInnerId()]);*/
            sum2 += dis[userAllocations.get(flags.get(i)).getAreaInnerId()][userAllocations.get(flags.get(i + 1)).getAreaInnerId()];
        }
        sum2+= dis[userAllocations.get(flags.get(flags.size() -1)).getAreaInnerId()]
                [userAllocations.get(flags.get(0)).getAreaInnerId()];

     /*   System.out.println(userAllocations.get(flags.get(flags.size() - 1)).getAreaInnerId() + "到"
                + userAllocations.get(flags.get(0)).getAreaInnerId() + "dis:" + dis[userAllocations.get(flags.get(flags.size() - 1)).getAreaInnerId()]
                [userAllocations.get(flags.get(0)).getAreaInnerId()]);*/

        if (sum != sum2) {
            System.out.println("error");
            for (UserAllocation userAllocation : userAllocations) {
                System.out.println(userAllocation.getAreaInnerId() + 1);
            }
        }








    }

    @Deprecated
    public void judgeError(int[] flags) {

        for (int i = 0; i < flags.length - 1; i++) {
            if (flags[i] > flags[i + 1]) {
                System.out.println(flags[i]);
            }
        }

    }


    private void JGAPTSP(int[][] dis, List<UserAllocation> userAllocations) {

        TravellingSalesmanForTesting travellingSalesmanForTesting
                = new TravellingSalesmanForTesting(dis, userAllocations);

        double value = 0;
        try {
            IChromosome optimal = travellingSalesmanForTesting.findOptimalPath(null);

            for (int i = 0; i < optimal.getGenes().length - 1; i++) {
                value += dis[(int) optimal.getGene(i).getAllele()][(int) optimal.getGene(i + 1).getAllele()];
            }
            value += dis[(int) optimal.getGene(optimal.getGenes().length - 1).getAllele()][(int) optimal.getGene(0).getAllele()];

            System.out.println(value);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class TravellingSalesmanForTesting
            extends Salesman {

        private int[][] DIS;
        private List<UserAllocation> userAllocations;


        public TravellingSalesmanForTesting(int[][] dis, List<UserAllocation> userAllocations) {
            this.userAllocations = userAllocations;
            CITIES = userAllocations.size();
            DIS = dis;
        }

        /**
         * The number of cities to visit, 7 by default.
         */
        public int CITIES = 7;

        @Override
        public Configuration createConfiguration(Object a_initial_data) throws InvalidConfigurationException {
            // This is copied from DefaultConfiguration.
            // -----------------------------------------
            Configuration config = new Configuration();

            BestChromosomesSelector bestChromsSelector =
                    new BestChromosomesSelector(config, 1.0d);
            bestChromsSelector.setDoubletteChromosomesAllowed(false);
            config.addNaturalSelector(bestChromsSelector, true);
            config.setRandomGenerator(new StockRandomGenerator());
            config.setMinimumPopSizePercent(0);
            config.setEventManager(new EventManager());
            config.setFitnessEvaluator(new DefaultFitnessEvaluator());
            config.setChromosomePool(new ChromosomePool());
            // These are different:
            // --------------------
            config.addGeneticOperator(new GreedyCrossover(config));
            MutationOperator mutationOperator = new SwappingMutationOperator(config, 20);
            mutationOperator.setMutationRate(200);
            config.addGeneticOperator(new SwappingMutationOperator(config, 20));
            return config;
        }

        /**
         * Create an array of the given number of integer genes. The first gene is
         * always 0, this is a city where the salesman starts the journey.
         *
         * @param a_initial_data not needed here
         * @return new chromosome
         */
        public IChromosome createSampleChromosome(Object a_initial_data) {
            try {
                Gene[] genes = new Gene[CITIES];
                for (int i = 0; i < genes.length; i++) {
                    genes[i] = new IntegerGene(getConfiguration(), 0, CITIES - 1);
                    genes[i].setAllele(new Integer(i));
                }
                IChromosome sample = new Chromosome(getConfiguration(), genes);
                return sample;
            } catch (InvalidConfigurationException iex) {
                throw new IllegalStateException(iex.getMessage());
            }
        }

        /**
         * Distance is equal to the difference between city numbers,
         * except the distance between the last and first cities that
         * is equal to 1. In this way, we ensure that the optimal
         * soultion is 0 1 2 3 .. n - easy to check.
         * @param a_from Gene
         * @param a_to Gene
         * @return distance betwen cities
         */
        public double distance(Gene a_from, Gene a_to) {
            IntegerGene a = (IntegerGene) a_from;
            IntegerGene b = (IntegerGene) a_to;
            int A = a.intValue();
            int B = b.intValue();
            int s = (int)(a_from.getAllele());

            return DIS[A][B];
        }
    }


    private  double testStand(double[] array,double sum,int time){
        double average = sum/time;  //求出数组的平均数
        System.out.println(average);   //52.0
        int total=0;
        for(int i=0;i<array.length;i++){
            total += (array[i]-average)*(array[i]-average);   //求出方差，如果要计算方差的话这一步就可以了
        }
        double standardDeviation = Math.sqrt(total/array.length);   //求出标准差

        return standardDeviation;
    }
    /**
     * 分支定界
     *
     * @param users
     * @param distances
     */
    private void caculateBranchTime(List<UserAllocation> users, int[][] distances) {
       /* long start = System.currentTimeMillis();
        for (int i = 0; i < users.size(); i++) {
            distances[i][i] = INF;
        }
        BranchBound.main1(distances);

        long end = System.currentTimeMillis();
        long allTimes = (end - start);
        System.out.println("Branch And Bound Time" + formatDouble1(allTimes) + "(ms)");*/

      /*  long start = System.currentTimeMillis();
        BranchAndBoundMain branchAndBoundMain = new BranchAndBoundMain(users, distances);
        branchAndBoundMain.main();
        long end = System.currentTimeMillis();
        long allTimes = (end - start);
        System.out.println("Branch And Bound Time" + formatDouble1(allTimes) + "(ms)");*/
        Date date = new Date();
        DateFormat df = DateFormat.getDateTimeInstance();
        SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance();
        System.out.println("当前日期时间：" + df.format(date));
        System.out.println("当前日期时间：" + sdf.format(date));
        // com.test.service.BranchBound.branch1.Timer timer = new com.test.service.BranchBound.branch1.Timer();
        int times = 10;
        long allTimes = 0;
        long start = System.currentTimeMillis();
        Solver solver = new Solver(users,distances);
        // timer.start();
        int[] path = solver.calculate();
        //timer.stop();
        long end = System.currentTimeMillis();
        allTimes +=  (end - start);

        String message = String.valueOf(path[0]);
        for(int j = 1; j < path.length; j++) {
            message += " to " + path[j];
        }
        message += " to " +  path[0] ;

        message += "\nCost: " + solver.getCost();
        System.out.println(message);
        //double data = allTimes / times;
        System.out.println("Branch And Bound Time" + formatDouble1(allTimes) + "(ms)");

        // message += "\nTime: " + timer.getFormattedTime();

    }

    public void test(int[][] dis) {
        int[] data = new int[]{
                4, 32, 30, 27, 23, 20, 21, 22, 28, 29, 31, 35, 2, 3, 0, 13, 11, 14, 15, 16, 1, 26, 25, 24, 19, 33, 18, 17, 10, 9, 34, 8, 12, 5, 7, 6, 4
        };
        int alldis = 0;
        for (int i = 0; i < data.length-1; i++) {
            alldis += dis[data[i]][data[i + 1]];
        }
        System.out.println(alldis);
    }

    /**
     * 凸包算法
     * @param users
     * @param distances
     */
    private Map<String,Object> caculateConvexHull(List<UserAllocation> users,
                                                  int[][] distances,int bestDis) {

        int times = 10;
        double sumTime = 0;
        double minLength = 0;
        Map<String, Object> map = new HashMap<>();
        List<UserAllocation> userAllocations1 = null;
        int[] convexHulls = null;
        for (int j = 0; j < times; j++) {
            long start = System.currentTimeMillis();
            List<UserAllocation> userAllocations = new ArrayList<>(users);

            ConvexHullAlogrithm convexHullAlogrithm = new ConvexHullAlogrithm();
            userAllocations1 = convexHullAlogrithm.main(userAllocations, distances);
            convexHulls = convexHullAlogrithm.convexHulls;
            int dis = 0;
            for (int i = 0; i < userAllocations1.size() - 1; i++) {
                dis += distances[userAllocations1.get(i).getAreaInnerId()]
                        [userAllocations1.get(i + 1).getAreaInnerId()];

            }

            minLength = dis;
            long end = System.currentTimeMillis();
            long add = end - start;
            sumTime += add;
            //System.out.println(dis);
        }

        printInfo(Double.valueOf(sumTime)/times, minLength, minLength, Double.valueOf(bestDis), "凸包算法");
        map.put("users", userAllocations1);
        map.put("hulls", convexHulls);

        return map;

    }




    /**
     * 最远插入 与最近插入
     *
     * @param users
     * @param distances
     * @param optionDis
     */
    private void caculateNearestNeighbor(List<UserAllocation> users, int[][] distances, int optionDis, String tspName,int TSPSize) {
        HashSet<String> stringHashSet = new HashSet<>();
        for (int i = 0; i < users.size(); i++) {
            stringHashSet.add(String.valueOf(i));
        }
        double times = 10;
        long allTimes = 0;
        //long oneTimes = Long.MAX_VALUE;
        int bestDis = Integer.MAX_VALUE;
        // for (int j = 0; j < times; j++) {

        double sumDis = 0;
        int k = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < users.size() - 2; i++) {
            for (int i1 = i + 1; i1 < users.size() - 1; i1++) {
                for (int i2 = i1 + 1; i2 < users.size(); i2++) {
                    // if (i1 != i) {
                    List<String> strings = new ArrayList<>();
                    strings.add(String.valueOf(i1));
                    strings.add(String.valueOf(i2));
                    HashSet<String> stringHashSet1 = new HashSet<>(stringHashSet);
                    myNearestInsertion myNearestInsertion =
                            new myNearestInsertion(distances, stringHashSet1, String.valueOf(i));
                    int dis = myNearestInsertion.main(strings);
                    sumDis += dis;
                    if (bestDis > dis) {
                        bestDis = dis;
                    }
                    // }
                    // }
                    k++;
                }
            }
        }
        long end = System.currentTimeMillis();
        allTimes = (end - start);
        //  allTimes += time;
        //System.out.println(k);
        //System.out.println(allTimes);
        System.out.println(tspData.NAME + ":" + k);
        FIA2testDatas.add(new TestDataModel(tspName, printPianChaAndAvgDis(sumDis, k, optionDis),TSPSize));
        //allTimes /= 10;
        // Double data = allTimes;
        printInfo(allTimes, bestDis, bestDis, Double.valueOf(optionDis), "NearestInsertionAlgorithm");


        //   System.out.println(bestDis);
    }


    /**
     * 计算单点与全部点的
     * @param users
     * @param bestdis
     */
    private void caculateFasterInsertion(List<UserAllocation> users,double bestdis,int[][] distances) {
        HashSet<String> stringHashSet = new HashSet<>();
        for (int i = 0; i < users.size(); i++) {
            stringHashSet.add(String.valueOf(i));
        }
        int k = 0;
        double maxValue = Double.MAX_VALUE;
        double minDis = Double.MIN_VALUE;
        double sumDis = 0;
        for (int i = 0; i < users.size() - 1; i++) {
            for (int i1 = i + 1; i1 < users.size(); i1++) {
                List<String> strings = new ArrayList<>();
                strings.add(String.valueOf(i1));
                HashSet<String> stringHashSet1 = new HashSet<>(stringHashSet);
                myNearestInsertion myNearestInsertion =
                        new myNearestInsertion(distances, stringHashSet1, String.valueOf(i));
                int dis = myNearestInsertion.main(strings);
                if (dis < maxValue) {
                    maxValue = dis;
                }
                if (dis > minDis) {
                    minDis = dis;
                }

                sumDis += dis;
                k++;//数量
            }
        }
        int avgDis = (int) (sumDis / k);
        DecimalFormat df = new DecimalFormat("######0.00");

        String bilv1 = df.format(((Double.valueOf(avgDis) / bestdis) - 1) * 100)+"%";

        String bilv2 = df.format(((Double.valueOf(maxValue) / bestdis) - 1) * 100)+"%";
        String bilv3 = df.format(((Double.valueOf(minDis) / bestdis) - 1) * 100)+"%";


        System.out.println("平均距离：" + avgDis + "," + "最好距离:" + maxValue + ",最差距离:" + minDis);
        System.out.println("平均偏差:" + bilv1 + "最好偏差:" + bilv2 + "最差偏差:" + bilv3);
        //ystem.out.println("总时间:" + sumTime + ",平均时间" + (double) (sumTime / tspData.DIMENSION));
    }



    private void caculateACOTime(int[][] distances, int size, List<UserAllocation> users, int bestDis) {

        Point[] points = Constant.getPointByClients(users);
        double sumTime = 0;
        double sumLength = 0;
        double minLength = Double.MAX_VALUE;
        int times = 15;
        for  (int i = 0; i < times; i++) {
            long start = System.currentTimeMillis();
            com.test.service.ACO.ACO1.ACO aco = new com.test.service.ACO.ACO1.ACO(distances, users);
            aco.init(4000);
            aco.run(40);
            int aw_best_tour_length = aco.reportResult();
            /*int ants = 100;          // Number of ants to run per generation.
            int gen = 100;          // Number of generations.
            double evap = 0.1;          // Evaporation rate of pheromones.
            int alpha = 1;            // Impact of pheromones on decision making.
            int beta = 5;            // Impact of distance on decision making.
            Graph graph = Import.getGraph(evap, alpha, beta, users, distances);
            long start = System.currentTimeMillis();
            TravelingSalesman travelingSalesman = new TravelingSalesman(ants, gen, evap, alpha, beta, graph);
            int aw_best_tour_length = travelingSalesman.run();*/


            /*Tsp.instance = new Tsp.problem();
            Tsp.n = size;
            set_default_parameters();
            int max_tries = size;
            Tsp.instance.distance = distances;
            Tsp.instance.nn_list = compute_nn_lists();

            Ants.allocate_ants();


            Ants.pheromone = Utilities.generate_double_matrix(Tsp.n, Tsp.n);
            Ants.total = Utilities.generate_double_matrix(Tsp.n, Tsp.n);
            InOut.best_in_try = new int[max_tries];
            InOut.best_found_at = new int[max_tries];
            InOut.time_best_found = new double[max_tries];
            InOut.time_total_run = new double[max_tries];

            InOut.aw_best_tour_in_try = new String[max_tries];
            InOut.time_used = Timer.elapsed_time();
            System.out.println("Initialization took " + InOut.time_used + " seconds\n");
            for (InOut.n_try = 0; InOut.n_try < InOut.max_tries; InOut.n_try++) {
                init_try(InOut.n_try);

                while (!termination_condition()) {

                    construct_solutions();

                    if (LocalSearch.ls_flag > 0)
                        local_search();

                    update_statistics();

                    pheromone_trail_update();

                    search_control_and_statistics();

                    InOut.iteration++;
                }
                InOut.exit_try(InOut.n_try);
            }
            InOut.exit_program();
            int aw_best_tour_length = Utilities.best_of_vector(InOut.best_in_try, InOut.max_tries);
            String aw_best_tour = InOut.aw_best_tour_in_try[Utilities.aw_best_tour_index()];*/


            /*InOut.best_in_try = new int[max_tries];
            InOut.best_found_at = new int[max_tries];
            InOut.time_best_found = new double[max_tries];
            InOut.time_total_run = new double[max_tries];

            InOut.aw_best_tour_in_try = new String[max_tries];


            Tsp.instance.nodeptr = points;
            Ants.pheromone = Utilities.generate_double_matrix(Tsp.n, Tsp.n);
            Ants.total = Utilities.generate_double_matrix(Tsp.n, Tsp.n);
            if (Ants.n_ants < 0)
                Ants.n_ants = Tsp.n;
            if (Ants.eas_flag && Ants.elitist_ants <= 0)
                Ants.elitist_ants = Tsp.n;
            LocalSearch.nn_ls = Math.min(Tsp.n - 1, LocalSearch.nn_ls);
            System.out.println("calculating distance matrix ..");
            Tsp.instance.distance = distances;
            System.out.println("allocate ants' memory ..");
            Ants.allocate_ants();
            System.out.println(" .. done\n");
            int aw_best_tour_length = Utilities.best_of_vector(InOut.best_in_try, InOut.max_tries);
            String aw_best_tour = InOut.aw_best_tour_in_try[Utilities.aw_best_tour_index()];*/
            if (aw_best_tour_length < minLength) {
                minLength = aw_best_tour_length;
            }
            sumLength += aw_best_tour_length;

            long end = System.currentTimeMillis();
            long time = (end - start);
            sumTime += time;
        }
        printInfo(sumTime / times, minLength, sumLength / times, Double.valueOf(bestDis),"ACO");
       /* System.out.println("ACO平均用时:" + formatDouble1(sumTime / times) + "(ms)");
        System.out.println("ACO平均长度:" + formatDouble1(sumLength / times));
        System.out.println("ACO最短:" + formatDouble1(minLength));*/
    }




    private double UserGA(int[][] distances) {
        GeneticAlgorithm ga = GeneticAlgorithm.getInstance();
        int[] best;
        //ga.setMaxGeneration(5000);
        ga.setAutoNextGeneration(true);

        best = ga.tsp(distances);
        //System.out.print("best path:");
        return ga.getBestDist();
    }



    private List<UserAllocation>  UserGA(int[][] distances,List<UserAllocation> userAllocations) {
        GeneticAlgorithm ga = GeneticAlgorithm.getInstance();
        int[] best;
        //ga.setMaxGeneration(5000);
        ga.setAutoNextGeneration(true);

        best = ga.tsp(distances);

        //System.out.print("best path:");

        return sort(userAllocations, ga.getBestIndivial1());
    }

    /**
     * 最优解
     * @param userAllocations
     * @param best
     * @return
     */
    public List<UserAllocation> sort(List<UserAllocation> userAllocations, int[] best) {
        List<UserAllocation> userAllocations1 = new ArrayList<>();

        for (int i : best) {
            for (UserAllocation userAllocation : userAllocations) {
                if (userAllocation.getAreaInnerId() == i) {
                    userAllocations1.add(userAllocation);
                    break;
                }
            }
        }
        for (UserAllocation userAllocation : userAllocations) {
            if (userAllocation.getAreaInnerId() == best[0]) {
                userAllocations1.add(userAllocation);
                break;
            }
        }



        return userAllocations1;
    }


    /**
     *重新生成
     * @param points
     * @param Lpoint
     * @param Rpoint
     * @param Mpoint
     * @return
     */
    @RequestMapping(value = "reCreatePath",method = {RequestMethod.POST})
    @ResponseBody
    public  Map<String, Object> reCreate(String points, String Lpoint, String Rpoint, String Mpoint,String direction) {

        Map<String, Object> reValue = new HashMap<>();
        Map<String, Object> pointMap = ATspService.setreturnPoints(Lpoint, Rpoint, Mpoint);

        List<UserAllocation> userAllocations = JSON.parseArray(points, UserAllocation.class);
        List<UserAllocation> ones = new ArrayList<>();
        List<UserAllocation> others = new ArrayList<>();
        List<UserAllocation> users = ATspService.divideArea(pointMap, userAllocations, distances,direction);

        reValue.put("data", JSON.toJSONString(users));

        // ATspService.divideClientPoint(ones,others,new Point( ));


        return reValue;

    }


    @RequestMapping(value = "VirtualPoint", method = {RequestMethod.POST})
    @ResponseBody
    public Map<String, Object> addVirtualPoint(String virtualPoint, String points, String dir) {
        Map<String, Object> reValue = new HashMap<>();
        List<UserAllocation> userAllocations = JSON.parseArray(points, UserAllocation.class);
        JSONObject obj = JSONObject.parseObject(virtualPoint);
        userAllocations.remove(userAllocations.size() - 1);
        UserAllocation userAllocation = new UserAllocation(new Point(obj.getDouble("lng"),
                obj.getDouble("lat")),userAllocations.size());
        userAllocations.add(userAllocation);
        int[][] dis = new int[userAllocations.size()  ][userAllocations.size() ];
        switch (tspData.EDGE_WEIGHT_TYPE) {
            case "ATT":
                for (int i = 0; i < userAllocations.size(); i++) {
                    userAllocations.get(i).setAreaInnerId(i);
                    for (int i1 = 0; i1 < userAllocations.size(); i1++) {
                        dis[i][i1] = (int) TSPLibService.calculateATTDistance(
                                userAllocations.get(i).getUserLocation().x, userAllocations.get(i).getUserLocation().y,
                                userAllocations.get(i1).getUserLocation().x, userAllocations.get(i1).getUserLocation().y);
                    }
                }
                break;
        }
        Spindle sPindle = new Spindle(userAllocations, userAllocations.size() - 1);
        AngularBisectorService angularBisectorService = new AngularBisectorService(dis, sPindle, Integer.valueOf(dir));

        List<UserAllocation> outData = new ArrayList<>();
        double value = angularBisectorService.planPathWithCommon(outData, Constant.none);
        outData.remove(0);
        outData.remove(outData.size() - 1);
        outData.add(outData.get(0));
        System.out.println("finnal" + readAllDis(outData, dis));
        reValue.put("data", JSON.toJSONString(outData));



        return reValue;
    }

    /**
     * 保留两位小数，四舍五入的一个老土的方法
     * @param d
     * @return
     */
    public static String formatDouble1(double d) {
        double value = (double) Math.round(d * 100) / 100;
        return  new DecimalFormat("#,##0.00").format(value);
    }


    @RequestMapping("showTspPoints")
    public String toShowTsp(){
        return "ShowTsp";
    }






}
