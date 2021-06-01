package com.test.service.GA;

import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/12/17.
 */
public class GeneticAlgorithmMethod {
    Random random = new Random();
    /**
     * wlr 遗传算法进行路径规划
     * @param userAllocations
     * @return
     */
    public double geneticAlg(List<UserAllocation> userAllocations,int[][] dis) {
        int pops = 50; //种群个体数量
        double JCL = 0.95; //交叉率
        double BYL = 0.25; //变异率

        /*for (int i = 0; i < userAllocations.size(); i++) {
            userAllocations.get(i).setAreaInnerId(i + 1);
        }*/
        int[][] disL = dis;
        //iWlrService.getTheWareHouseToClients(userAllocations, disL);
        //初始种群
        int[][] lines = new int[pops][userAllocations.size()];
        for (int i = 0; i < lines.length; i++) {
            for (int i1 = 0; i1 < lines[i].length; i1++) {
                lines[i][i1] = -1;
            }
        }
        //适应度
        double[] fit = new double[pops];
        //初始化种群
        initializePops(lines, fit, pops, userAllocations.size(), disL);

        int t = 0;
        while (t < 1000) {
            //下一代种群
            int[][] nextLines = new int[pops][userAllocations.size()];
            //下一代种群的适应度
            double[] nextFit = new double[pops];

            double[] ranFit = new double[pops];
            double totalFit = 0, tempFit = 0;

            for (int i = 0; i < pops; i++) {
                totalFit += fit[i];
            }
            for (int i = 0; i < pops; i++) {
                ranFit[i] = tempFit + fit[i] / totalFit;
                tempFit += ranFit[i];
            }

            //上代最优直接进入下一代
            int ml = findTheBestOne(fit);
            for (int i = 0; i < userAllocations.size(); i++) {
                nextLines[0][i] = lines[ml][i];
            }
            nextFit[0] = fit[ml];

            // 最优使用爬山算法
            clMountain(nextLines[0], nextFit, userAllocations.size(), disL);//爬山操作后更新个体的同时，对应的适应度也需要更新

            //交叉变异产生下一代中的后userAllocations.size()-1个基因
            int nl = 1;
            while (nl < pops) {
                //根据概率选取，进行个体基因交叉的另一个个体
                int r = ranSelect(ranFit);
                // 判断是否交叉 不能超出界限
                if (random.nextDouble() < JCL && nl + 1 < pops) {
                    int[] fLine = new int[userAllocations.size()];
                    int[] nLine = new int[userAllocations.size()];

                    //获取交叉排序
                    int rn = ranSelect(ranFit);
                    // 随机获得交叉的段
                    int f = random.nextInt(userAllocations.size());
                    int l = random.nextInt(userAllocations.size());
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
                    for (int i = 0; i < userAllocations.size(); i++) {
                        if (!isHas(fLine, lines[r][i])) {
                            fLine[fpo] = lines[r][i];
                            fpo++;
                        }
                        if (!isHas(nLine, lines[rn][i])) {
                            nLine[npo] = lines[rn][i];
                            npo++;
                        }
                    }
                    //基因突变
                    change(fLine, BYL);
                    change(nLine, BYL);

                    //将交叉后的两个新个体加入到下一代
                    for (int i = 0; i < userAllocations.size(); i++) {
                        nextLines[nl][i] = fLine[i];
                        nextLines[nl + 1][i] = nLine[i];
                    }
                    nextFit[nl] = calFitness(fLine, disL);
                    nextFit[nl + 1] = calFitness(nLine, disL);
                    nl += 2;
                } else {
                    int[] line = new int[userAllocations.size()];
                    int i = 0;
                    while (i < userAllocations.size()) {
                        line[i] = lines[r][i];
                        i++;
                    }
                    change(line, BYL);
                    for (int j = 0; j < userAllocations.size(); j++) {
                        nextLines[nl][j] = line[j];
                    }
                    nextFit[nl] = calFitness(line, disL);
                    nl++;
                }
            }
            //新一代覆盖上一代
            for (int i = 0; i < pops; i++) {
                for (int j = 0; j < userAllocations.size(); j++) {
                    lines[i][j] = nextLines[i][j];
                }
                fit[i] = nextFit[i];
            }
            t++;
        }
        int ml = findTheBestOne(fit);
        int[] sorted = new int[userAllocations.size()];//最优的排序
        for (int i = 0; i < lines[ml].length; i++) {
            sorted[i] = lines[ml][i];
        }

        //更新客户顺序，计算路径长度
        double length = updateTheSortedClient(sorted, userAllocations, disL);
        return length;
    }

    /**
     * wlr 排好序的序号，对客户进行重新排序，计算路径长度
     * @param sorted
     * @param userAllocations
     * @param disL
     * @return
     */
    private double updateTheSortedClient(int[] sorted, List<UserAllocation> userAllocations, int[][] disL) {
        List<UserAllocation> sortedUser = new ArrayList<>();//排序完成的客户
        for (int aSorted : sorted) {
            for (UserAllocation userAllocation : userAllocations) {
                if (aSorted == userAllocation.getAreaInnerId()) {
                    sortedUser.add(userAllocation);
                    break;
                }
            }
        }
        double length = routeLength(sorted, disL);
        //更新原来的客户顺序，使得排序成功（不知道对不对）
        for (int i = 0; i < sortedUser.size(); i++) {
            userAllocations.set(i, sortedUser.get(i));
        }
        return length;
    }


    /**
     * wlr 求一辆车的路径长度
     * @param sorted
     * @param disL
     * @return
     */
    private double routeLength(int[] sorted, int[][] disL) {
        double dis = disL[0][sorted[0]];
        for (int i = 0; i < sorted.length - 1; i++) {
            dis += disL[sorted[i]][sorted[i + 1]];
        }
        dis += disL[sorted[sorted.length - 1]][0];
        return dis;
    }
    /**
     * wlr 集合中没有这个数
     * @param sorted
     * @param i
     * @return
     */
    private boolean isHas(int[] sorted, int i) {
        for (int j = 0; j < sorted.length; j++) {
            if (sorted[j] == i)
                return true;
        }
        return false;
    }
    /**
     * wlr 最优个体使用爬山算法
     * @param line
     * @param nextFit
     * @param size 客户数量
     * @param disL
     */
    private void clMountain(int[] line, double[] nextFit, int size, int[][] disL) {
        double oldFit = calFitness(line, disL);
        int i = 0;
        while (i < 20) { //爬山次数为20
            int f = random.nextInt(size);
            int n = random.nextInt(size);
            change(line, f, n);
            double newFit = calFitness(line, disL);

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
     * wlr 基因突变
     * @param nLine
     * @param BYL
     */
    private void change(int[] nLine, double BYL) {
        if (random.nextDouble() < BYL) {
            int i = 0;
            while (i < 5) { // 基因换位5次
                int f = random.nextInt(nLine.length);
                int n = random.nextInt(nLine.length);
                int temp = nLine[f];
                nLine[f] = nLine[n];
                nLine[n] = temp;
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
     * wlr 选择随机的序列
     * @param ranFit
     * @return
     */
    private int ranSelect(double[] ranFit) {
        double ran = random.nextDouble();
        for (int i = 0; i < ranFit.length; i++) {
            if (ran < ranFit[i])
                return i;
        }
        System.out.println("ERROR!!! get ranSelect Error!");
        return 0;
    }


    /**
     * wlr 初始化种群
     * @param lines
     * @param fit
     * @param pops
     * @param size
     * @param disL
     */
    private void initializePops(int[][] lines, double[] fit, int pops, int size, int[][] disL) {
        for (int i = 0; i < pops; i++) {
            int j = 0;
            while (j < size) {
                int num = random.nextInt(size) ;
                if (!isHas(lines[i], num)) {
                    lines[i][j] = num;
                    j++;
                }
            }
            fit[i] = calFitness(lines[i], disL);
        }
    }


    /**
     * wlr 计算个体的适应度
     * @param line
     * @param disL
     * @return
     */
    private double calFitness(int[] line, int[][] disL) {
        double dis = disL[0][line[0]];
        for (int i = 0; i < line.length - 1; i++) {
            dis += disL[line[i]][line[i + 1]];
        }
        dis += disL[line[line.length - 1]][0];
        return 1/dis;
    }


}
