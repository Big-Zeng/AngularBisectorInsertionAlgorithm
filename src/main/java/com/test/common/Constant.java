package com.test.common;

import com.test.model.Point;
import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZXF on 2018-12-09.
 */
public class Constant {
    public static final int EAST = 0;
    public static final int WEST = 1;
    public static final int SOUTH = 2;
    public static final int NORTH = 3;
    public static final int NONE = -1;

    public static final int left = -1;
    public static final int right = 1;
    public static final int none = 0;

    public static UserAllocation getUserByAreaInnerId(List<UserAllocation> userAllocations, int areaInnerId) {
        for (UserAllocation userAllocation : userAllocations) {
            if (userAllocation.getAreaInnerId() == areaInnerId) {
                return userAllocation;
            }
        }
        return new UserAllocation();
    }

    public static Point[] getPointByClients(List<UserAllocation> userAllocations) {

        Point[] points = new Point[userAllocations.size()];
        for (int i = 0; i < userAllocations.size(); i++) {
            points[i] = (userAllocations.get(i).getUserLocation());
        }
        return points;
    }
}
