package com.test.service.BranchBound;

import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZXF on 2019-01-26.
 */
public class Branch {
    public List<UserAllocation> userAllocations;

    public UserAllocation currentAllocation;



    public  Branch(){

    }

    public Branch(List<UserAllocation> userAllocations) {
        this.userAllocations = new ArrayList<>();
        for (UserAllocation userAllocation : userAllocations) {
            this.userAllocations.add(userAllocation);
        }

    }






}
