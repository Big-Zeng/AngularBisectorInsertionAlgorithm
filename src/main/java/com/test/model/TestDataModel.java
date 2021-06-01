package com.test.model;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2019/4/23.
 */
public class TestDataModel {
    public String tspName;
    public double avgDeviation;
    public double Time;
    public int iterationTimes; //迭代次数
    public int Size ;


    public TestDataModel(String tspName, double avgDeviation,int size) {
        this.tspName = tspName;
        this.avgDeviation = avgDeviation;
        this.Size = size;
    }

    public TestDataModel(String tspName, double avgDeviation, double time, int iterationTimes) {
        this.tspName = tspName;
        this.avgDeviation = avgDeviation;
        Time = time;
        this.iterationTimes = iterationTimes;
    }


    public TestDataModel() {
        super();
    }

    /**
     * 保留一位小数
     * @param d
     * @return
     */
    public static String formatDouble1(double d) {
        double value = (double) Math.round(d * 100) / 100;
        return new DecimalFormat("#,##0.0").format(value);
    }

    public static void sortPrint(List<TestDataModel> testDataModels) {
        Collections.sort(testDataModels, (o1, o2) -> {
            if (o1.Size < o2.Size) {
                return -1;
            } else {
                return 1;
            }
        });
        for (TestDataModel testDataModel : testDataModels) {
            testDataModel.toStr();
        }
    }


    public void toStr() {
        System.out.println(tspName + ":(" + formatDouble1(avgDeviation) + "%)");
    }



    public int compareTo(TestDataModel testDataModel1,TestDataModel testDataModel2) {
        if(testDataModel1.Size >= testDataModel2.Size){

            return 1;
        }
        return -1;

    }


}
