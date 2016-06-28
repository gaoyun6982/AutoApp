package com.android.gaoyun.autoapp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import data.DatabaseHelper;

public class NewCarActivity extends Activity {

    SQLiteDatabase sdb;
    DatabaseHelper databaseHelper;

    Cursor dataCursor;
    Cursor carCursor;

    EditText manufacturerText;
    EditText modelText;
    EditText defaultRangeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MainActivity.REFILL_STATUS = false;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Ваш автомобиль");

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

    }

    public void createCar(View view) {

        createDatabaseFile();

        databaseHelper = new DatabaseHelper(this, "autoAppBase.db", null, 1);

        try {
            sdb = databaseHelper.getWritableDatabase();
        } catch (Exception sqlEx){
            sdb = databaseHelper.getReadableDatabase();
        }

        manufacturerText = (EditText)findViewById(R.id.editManufacturer);
        modelText = (EditText)findViewById(R.id.editModel);
        defaultRangeText = (EditText)findViewById(R.id.editDefaultRange);

        double defaultRange = Double.parseDouble(defaultRangeText.getText().toString());

        ContentValues values = new ContentValues();
        values.put(databaseHelper.CAR_MANUFACTURER, manufacturerText.getText().toString());
        values.put(databaseHelper.CAR_MODEL, modelText.getText().toString());
        values.put(databaseHelper.RANGE_DEFAULT_COLUMN, defaultRange);

        sdb.insert("car", null, values);

        carCursor = sdb.query("car", new String[]{databaseHelper.CAR_MANUFACTURER, databaseHelper.CAR_MODEL, databaseHelper.RANGE_DEFAULT_COLUMN}, null, null, null, null, null);

        try {
            dataCursor = sdb.query("refills", new String[]{databaseHelper.RANGE_COLUMN}, null, null, null, null, null);
            dataCursor.moveToLast();
            MainActivity.newRange.setText("Последняя заправка на " + dataCursor.getString(dataCursor.getColumnIndex(databaseHelper.RANGE_COLUMN)) + " км");
        } catch (Exception e) {
            dataCursor = sdb.query("car", new String[]{databaseHelper.RANGE_DEFAULT_COLUMN}, null, null, null, null, null);
            MainActivity.newRange.setText("Вы ещё ни разу не заправлялись с нами!");
        }

        carCursor.moveToFirst();
        MainActivity.textCar.setText(carCursor.getString(carCursor.getColumnIndex(databaseHelper.CAR_MANUFACTURER)) + " " + carCursor.getString(carCursor.getColumnIndex(databaseHelper.CAR_MODEL)));

        carCursor.moveToFirst();
        MainActivity.textRange.setText("Вы с нами с " + carCursor.getString(carCursor.getColumnIndex(databaseHelper.RANGE_DEFAULT_COLUMN)) + " км");


        finish();

    }

    public void createDatabaseFile() {

        String filename = "autoAppBase.db";

        File sdPatch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        sdPatch = new File(sdPatch.getPath());
        sdPatch.mkdirs();

        File sdFile = new File(sdPatch, filename);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sdFile));
            bufferedWriter.close();

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ваш автомобиль успешно создан!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
