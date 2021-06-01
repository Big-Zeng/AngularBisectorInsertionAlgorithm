package com.test.service.GA;

import com.test.model.UserAllocation;

import java.util.List;
import java.util.Random;

/**
 * GeneticTest
 *
 * @author: onlylemi
 */
public class GeneticTest {
    private Random random = new Random();
    private int rows; //种群个体数
    private int time; //迭代次数
    private int clientsNum; //客户数量
    private double tons; //货车载重
    private int dis; //货车最大行驶距离
    private int PW; //惩罚因子

    private double JCL;//交叉率
    private double BYL;//变异率
    private int JYHW;//基因换位次数
    private int PSCS;//爬山次数

    private int[][] d;
    /**
     * wlr 初始化遗传算法
     * @param clients
     */
    public void initializationGA(List<UserAllocation> clients, int[][] disL) {
        rows = 20;
        time = 2000;
        dis = 1000;
        PW = 1000;

        JCL = 0.95;
        BYL = 0.25;
        JYHW = 5;
        PSCS = 20;
        d = disL;
        clientsNum = clients.size();
    }


    /**
     * 判断个体中是否已经存在这个基因
     * @param line
     * @param num
     * @return
     */
    private boolean isHas(int[] line, int num) {
        for (int i = 0; i < clientsNum; i++) {
            if (line[i] == num)
                return true;
        }
        return false;
    }

    /**
     * 选择随机的序列
     * @param ranFit
     * @return
     */
    public int ranSelect(double[] ranFit) {
        double ran = random.nextDouble();
        for (int i = 0; i < rows; i++) {
            if (ran < ranFit[i])
                return i;
        }
        System.out.println("ERROR!!! get ranSelect Error!");
        return 0;
    }


    /**
     * wlr贪心算法求出初始种群中的个体
     * @return
     */
    private int[] GreedyMakeNewPop() {
        int[] returnPop = new int[clientsNum];
        for (int i = 0; i < returnPop.length; i++) {
             returnPop[i] =-1;
        }
        int num = random.nextInt(clientsNum);
        int k = 0;
        returnPop[k] = num;

        while (returnPop[clientsNum - 1] == -1) {
            k++;
            double minDis = Double.MAX_VALUE;
            int flag = 0;
            for (int i = 0; i < clientsNum; i++) {
                if (!isHas(returnPop, i) && d[returnPop[k-1]][i] < minDis) {
                    minDis = d[returnPop[k-1]][i];
                    flag = i;
                }
            }
            returnPop[k] = flag;
        }
        return returnPop;
    }

    /**
     * 爬山算法
     * @param line
     * @param nextFit
     */
    public void clMountain(int[] line, double[] nextFit) {
        double oldFit = calFitness(line);
        int i = 0;
        while (i < PSCS) {
            int f = random.nextInt(clientsNum);
            int n = random.nextInt(clientsNum);
            change(line, f, n);
            double newFit = calFitness(line);

            if (newFit < oldFit) {
                change(line, f, n);
            } else {
                oldFit = newFit;
                nextFit[0] = newFit;
            }
            i++;
        }
    }
    /**
     * 基因变异
     * @param line
     */
    public void change(int[] line) {
        if (random.nextDouble() < BYL) {
            int i = 0;
            while (i < JYHW) {
                int f = random.nextInt(clientsNum);
                int n = random.nextInt(clientsNum);
                change(line, f, n);
                i++;
            }
        }
    }


    public void change(int[] line, int f, int n) {
        int temp = line[f];
        line[f] = line[n];
        line[n] = temp;
    }

    /**
     * wlr 找到适应度最高的那个个体
     * @param fit
     * @return
     */
    private int findTheBestOne(double[] fit) {
        double minfit = fit[0];
        int ml = 0;
        for (int i = 0; i < fit.length; i++) {
            if (minfit < fit[i]) {
                minfit = fit[i];
                ml = i;
            }
        }
        return ml;
    }


    /**
     * 计算适应度
     * @param line
     * @return
     */
    public double calFitness(int[] line) {
        // 默认为2倍
        int  M = 0;
        double dis = 0;
        for (int i = 0; i < line.length - 1; i++) {
            dis += d[line[i]][line[i + 1]];
        }
        dis += d[line[line.length - 1]][line[0]];
        // 目标函数
        double result = 1 / (dis + M * PW);
        return result;
    }



    public double startRun() {
        int[][] lines = new int[rows][clientsNum]; // 初始种群
        double[] fit = new double[rows]; // 适应度
        for (int i = 0; i < rows; i++) { // 获取rows个随机排列，并计算适应度，初始化种群个体
            int j = 0;
            while (j < clientsNum) {
                int num = random.nextInt(clientsNum) + 1;
                if (!isHas(lines[i], num)) {
                    lines[i][j] = num;
                    j++;
                }
            }
            lines[i] = GreedyMakeNewPop();//贪婪算法初始种群的一个个体
            fit[i] = calFitness(lines[i]);
        }
        int t = 0;

        while (t < time) {
            int[][] nextLines = new int[rows][clientsNum]; // 下一代种群
            double[] nextFit = new double[rows]; // 下一代个体的适应度

            double[] ranFit = new double[rows];
            double totalFit = 0, tempFit = 0;
            for (int i = 0; i < rows; i++) {
                totalFit += fit[i];
            }
            for (int i = 0; i < rows; i++) {
                ranFit[i] = tempFit + fit[i] / totalFit;
                tempFit += ranFit[i];
            }

            int ml = findTheBestOne(fit); //找到上一代中最优的个体, 直接到下一代

            for (int i = 0; i < clientsNum; i++) {//上一代适应度最好的，复制到下一代种群第一个个体
                nextLines[0][i] = lines[ml][i];
            }
            nextFit[0] = fit[ml];


           /* double totaldis = getTheDisOfLine(lines[ml]); //每一次迭代的最佳里程
            System.out.print(" " + totaldis);*/

            clMountain(nextLines[0], nextFit);//最优使用爬山算法, 爬山操作后更新个体的同时，对应的适应度也需要更新

            int nl = 1;
            while (nl < rows) {
                // 根据概率选取，进行个体基因交叉的另一个个体
                int r = ranSelect(ranFit);
                // 判断是否交叉 不能超出界限
                if (random.nextDouble() < JCL && nl + 1 < rows) {
                    int[] fLine = new int[clientsNum];
                    int[] nLine = new int[clientsNum];

                    // 获取交叉排列
                    int rn = ranSelect(ranFit);

                    // 随机获得交叉的段
                    int f = random.nextInt(clientsNum);
                    int l = random.nextInt(clientsNum);
                    int min, max, fpo = 0, npo = 0;
                    if (f < l) {
                        min = f;
                        max = l;
                    } else {
                        min = l;
                        max = f;
                    }

                    // 将截取的段加入新生成的基因（通过上代群体中的个体，交叉获得下代种群的两个个体）——依据是上代种群个体的适应度
                    while (min <= max) {
                        fLine[fpo] = lines[rn][min];
                        nLine[npo] = lines[r][min];
                        min++;
                        fpo++;
                        npo++;
                    }

                    for (int i = 0; i < clientsNum; i++) {
                        if (!isHas(fLine, lines[r][i])) {
                            fLine[fpo] = lines[r][i];
                            fpo++;
                        }
                        if (!isHas(nLine, lines[rn][i])) {
                            nLine[npo] = lines[rn][i];
                            npo++;
                        }
                    }
                    // 基因变异
                    change(fLine);
                    change(nLine);

                    // 将交叉变异产生的两个新的个体，加入到下一代
                    for (int i = 0; i < clientsNum; i++) {
                        nextLines[nl][i] = fLine[i];
                        nextLines[nl + 1][i] = nLine[i];
                    }
                    nextFit[nl] = calFitness(fLine);
                    nextFit[nl + 1] = calFitness(nLine);

                    nl += 2;
                } else {
                    int[] line = new int[clientsNum];
                    int i = 0;
                    while (i < clientsNum) {
                        line[i] = lines[r][i];
                        i++;
                    }
                    // 基因变异
                    change(line);
                    // 加入下一代
                    i = 0;
                    while (i < clientsNum) {
                        nextLines[nl][i] = line[i];
                        i++;
                    }
                    nextFit[nl] = calFitness(line);
                    nl++;
                }
            }
            // 新的一代覆盖上一代
            for (int i = 0; i < rows; i++) {
                for (int h = 0; h < clientsNum; h++) {
                    lines[i][h] = nextLines[i][h];
                }
                fit[i] = nextFit[i];
            }
            t++;
        }
   /*     System.out.println();
        System.out.println("shouliangshuju");*/

        int ml = findTheBestOne(fit); // 迭代过后，找出种群中最优的个体

        System.out.println("最优结果为:");
        for (int i = 0; i < clientsNum; i++) {
            System.out.print(lines[ml][i] + ",");
        }
        System.out.println();
        double totaldis = getTheDisOfLine(lines[ml]);
        return totaldis;
    }

    /**
     * wlr 计算路径长度
     *
     * @param line
     * @return
     */
    private double getTheDisOfLine(int[] line) {
        double value = 0;
        for (int i = 0; i < line.length - 1; i++) {
            value += d[line[i]][line[i + 1]];
        }
        value += d[line[line.length-1]][line[0]];

        return value;
    }

   /* public static void main(String[] args) {
        Point[] points = new Point[30];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point();
            points[i].x = new Random().nextInt(200);
            points[i].y = new Random().nextInt(200);
        }

        int[] best;

        //=======================method 1=======================
        //GeneticAlgorithm ga = new GeneticAlgorithm();
        //best = ga.tsp(getDist(points));

        *//*int n = 0;
        while (n++ < 100) {
            best = ga.nextGeneration();

            System.out.println("best distance:" + ga.getBestDist() +
                    " current generation:" + ga.getCurrentGeneration() +
                    " mutation times:" + ga.getMutationTimes());
            System.out.print("best path:");
            for (int i = 0; i < best.length; i++) {
                System.out.print(best[i] + " ");
            }
            System.out.println();
        }*//*

        //=======================method 2========================
        GeneticAlgorithm ga = GeneticAlgorithm.getInstance();

        ga.setMaxGeneration(1000);
        ga.setAutoNextGeneration(true);
       // best = ga.tsp(getDist(points));


     *//*   System.out.print("best path:");
        for (int i = 0; i < best.length; i++) {
            System.out.print(best[i] + " ");
        }
        System.out.println();*//*
    }

    private static float[][] getDist(Point[] points) {
        float[][] dist = new float[points.length][points.length];
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points.length; j++) {
                dist[i][j] = distance(points[i], points[j]);
            }
        }
        return dist;
    }

    private static float distance(Point p1, Point p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }*/
}
