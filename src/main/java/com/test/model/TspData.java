package com.test.model;

import com.test.common.readTSPData;

import java.util.List;

/**
 * Created by ZXF on 2018-12-08.
 */
public class TspData {
    public int DIMENSION;
    public String EDGE_WEIGHT_FORMAT;
    public String EDGE_WEIGHT_SECTION;
    public String EDGE_WEIGHT_TYPE;
    public int BEST_RESULT;
    public String NAME;


    //TODO
    public void setEDGE_WEIGHT_SECTION(int valueStart, int valueEnd, String[] content) {
      //  handleAtspDifEdgeWeightFormat(valueStart, valueEnd, content);
    }



    private  void handleAtspDifEdgeWeightFormat(int valueStart, int valueEnd, String[] content ) {
        switch (EDGE_WEIGHT_FORMAT) {
            case "FULL_MATRIX":
             readTSPData.handleFullMatrix(EDGE_WEIGHT_SECTION,DIMENSION);
                break;
            case "UPPER_ROW":
                readTSPData.handleUPPERRow(valueStart, valueEnd, content, DIMENSION);
                break;
            case "LOWER_DIAG_ROW":
                readTSPData.handleLOWER_DIAG_ROW(valueStart, valueEnd, content, DIMENSION);
                break;

        }
    }








}
