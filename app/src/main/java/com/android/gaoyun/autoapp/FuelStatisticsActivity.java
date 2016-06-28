package com.android.gaoyun.autoapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;

import java.util.ArrayList;

import data.DatabaseHelper;
import graphics.LineView;

public class FuelStatisticsActivity extends Activity{

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_statistics);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Заправки");

        databaseHelper = new DatabaseHelper(this, "autoAppBase.db", null, 1);

        sdb = databaseHelper.getReadableDatabase();

        double range1 = 31900;
        double range2 = 0;
        double price2 = 0;
        double volume = 0;
        int level1 = 100;
        int level2 = 0;

        Cursor cursor = sdb.query("refills", new String[]{databaseHelper.RANGE_COLUMN, databaseHelper.VOLUME_COLUMN, databaseHelper.PRICE_COLUMN ,databaseHelper.LEVEL_COLUMN},null,null,null,null,null);
        cursor.moveToFirst();

        ArrayList<String> horizAxisOne = new ArrayList<>();

        while(cursor.moveToNext()){

            String arrStringElement = ""+ cursor.getString(cursor.getColumnIndex(databaseHelper.RANGE_COLUMN));
            horizAxisOne.add(arrStringElement);

        }

        cursor.moveToFirst();

        ArrayList<ArrayList<Integer>> vertAxisOne = new ArrayList<>();
        ArrayList<Integer> redLine = new ArrayList<>();

        while(cursor.moveToNext()){

            range2 = cursor.getDouble(cursor.getColumnIndex(databaseHelper.RANGE_COLUMN));
            price2 = cursor.getDouble(cursor.getColumnIndex(databaseHelper.PRICE_COLUMN));
            volume = cursor.getDouble(cursor.getColumnIndex(databaseHelper.VOLUME_COLUMN));
            level2 = cursor.getInt(cursor.getColumnIndex(databaseHelper.LEVEL_COLUMN));

            double totalVol = volume/(level1-level2)*100;

            double waste = totalVol*(level1-level2)/(range2-range1);

            //double result = waste * price2 * (range2-range1) * 100;
            //double res = volume*price2;
            double result = waste * price2;

            String res = ""+Math.round(result);
            redLine.add(Integer.parseInt(res));

            range1 = range2;
            level1 = level2;

        }

        vertAxisOne.add(redLine);

        ArrayList<String> horizAxisTwo = new ArrayList<>();
        cursor.moveToFirst();
        range1 = 31900;
        level1 = 100;

        while(cursor.moveToNext()){

            String arrStringElement = ""+ cursor.getString(cursor.getColumnIndex(databaseHelper.RANGE_COLUMN));
            horizAxisTwo.add(arrStringElement);

        }

        cursor.moveToFirst();

        ArrayList<ArrayList<Integer>> vertAxisTwo = new ArrayList<>();
        ArrayList<Integer> blueLine = new ArrayList<>();

        while(cursor.moveToNext()){

            range2 = cursor.getDouble(cursor.getColumnIndex(databaseHelper.RANGE_COLUMN));
            volume = cursor.getDouble(cursor.getColumnIndex(databaseHelper.VOLUME_COLUMN));
            price2 = cursor.getDouble(cursor.getColumnIndex(databaseHelper.PRICE_COLUMN));
            level2 = cursor.getInt(cursor.getColumnIndex(databaseHelper.LEVEL_COLUMN));

            double totalVol = volume/(level1-level2)*100;

            double result = totalVol*(level1-level2)/(range2-range1);
            range1 = range2;
            level1 = level2;

            String res = ""+Math.round(result);
            blueLine.add(Integer.parseInt(res));

        }

        cursor.close();

        vertAxisTwo.add(blueLine);
        //vertAxisOne.add(blueLine);

        LineView lineView = (LineView)findViewById(R.id.line_view);
        lineView.setDrawDotLine(false); //optional
        lineView.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView.setBottomTextList(horizAxisOne);
        lineView.setDataList(vertAxisOne);

        LineView lineView2 = (LineView)findViewById(R.id.line_view2);
        lineView2.setDrawDotLine(false); //optional
        lineView2.setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY); //optional
        lineView2.setBottomTextList(horizAxisTwo);
        lineView2.setDataList(vertAxisTwo);

        cursor.close();

    }

}
