package com.android.gaoyun.autoapp;

import android.os.Bundle;
import android.app.Activity;

import java.util.ArrayList;

import graphics.LineView;

public class FuelStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_statistics);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<String> arstr = new ArrayList<>();
        arstr.add("1");
        arstr.add("7");
        arstr.add("13");
        arstr.add("25");
        arstr.add("30");
        arstr.add("5");
        arstr.add("12");
        arstr.add("17");
        arstr.add("28");
        arstr.add("4");


        ArrayList<ArrayList<Integer>> ardt = new ArrayList<>();
        ArrayList<Integer> arint = new ArrayList<>();
        arint.add(30);
        arint.add(25);
        arint.add(43);
        arint.add(10);
        arint.add(20);
        arint.add(10);
        arint.add(35);
        arint.add(23);
        arint.add(50);
        arint.add(20);

        ardt.add(arint);

        LineView lineView = (LineView)findViewById(R.id.line_view);
        lineView.setDrawDotLine(false); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView.setBottomTextList(arstr);
        lineView.setDataList(ardt);

        LineView lineView2 = (LineView)findViewById(R.id.line_view2);
        lineView2.setDrawDotLine(false); //optional
        lineView2.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView2.setBottomTextList(arstr);
        lineView2.setDataList(ardt);


    }

}
