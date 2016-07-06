package com.android.gaoyun.autoapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import data.DatabaseHelper;
import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class addFuelActivity extends Activity {

    public DatabaseHelper databaseHelper;
    SQLiteDatabase sdb;

    boolean successRefill;

    EditText editLiters;
    EditText fuelMarkEdit;
    EditText editPrice;
    EditText editRange;

    String mark = "";
    String lastRange = "";
    String refillDate = "";

    SeekBar fuelLevel;

    Spinner octaSpinner;
    String[] dataOctaSpinner = {"АИ-98", "АИ-95", "АИ-92", "ДТ"};

    double volume;
    double price;
    double range;
    double lastRangeDouble;

    int level;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        successRefill = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fuel);
        //setTitle("Добавить заправку");

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Добавить заправку");

        //Window window = this.getWindow();
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataOctaSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        octaSpinner = (Spinner) findViewById(R.id.octaSpinner);
        octaSpinner.setAdapter(adapter);

        octaSpinner.setPrompt("Октановое число");
        octaSpinner.setSelection(1);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapController();

    }

    private void mapController(){

        final MapView mMapView = (MapView) findViewById(R.id.map);

        final MapController mMapController = mMapView.getMapController();
        mMapController.setPositionAnimationTo(new GeoPoint(51.529973, 45.979069));
        mMapController.setZoomCurrent(0);

        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    mMapController.setPositionAnimationTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
                    //setTitle(location.getLatitude() + " " + location.getLongitude());
                }catch(Exception geoException){
                    System.out.println("geoException");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                try {
                    mMapController.setPositionAnimationTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
                    //setTitle(location.getLatitude() + " " + location.getLongitude());
                }catch(Exception geoException){
                    System.out.println("geoException");
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("Permission location error.");
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch (Exception geoException){
            System.out.println("geoException");
        }


        //mMapController.setPositionAnimationTo(new GeoPoint(location.getAltitude(),location.getLongitude()));

    }

    public void saveRefill(View view) {

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");

        successRefill = true;

        databaseHelper = new DatabaseHelper(this, "autoAppBase.db", null, 1);
        sdb = databaseHelper.getWritableDatabase();

        Cursor cursor = sdb.query("refills", new String[]{databaseHelper.RANGE_COLUMN},null,null,null,null,null);
        cursor.moveToFirst();

        editLiters = (EditText)findViewById(R.id.editLiters);
        fuelMarkEdit = (EditText)findViewById(R.id.fuelMarkEdit);
        editPrice = (EditText)findViewById(R.id.editPrice);
        editRange = (EditText)findViewById(R.id.editRange);
        fuelLevel = (SeekBar)findViewById(R.id.seekBar);

        try {
            volume = Double.parseDouble(editLiters.getText().toString());
            price = Double.parseDouble(editPrice.getText().toString());
            range = Double.parseDouble(editRange.getText().toString());
            mark = fuelMarkEdit.getText().toString();
            level = Integer.valueOf(fuelLevel.getProgress());
            refillDate = sdf.format(date);

            while (cursor.moveToNext()) {
                lastRange = cursor.getString(cursor.getColumnIndex(databaseHelper.RANGE_COLUMN));
            }

            lastRangeDouble = Double.parseDouble(lastRange);
            System.out.println(lastRangeDouble);

            if(lastRangeDouble >= range){

                successRefill = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка в данных пробега! Проверьте правильность заполнения.", Toast.LENGTH_SHORT);
                toast.show();

            }

            if(price < 30){

                successRefill = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка в цене! Проверьте правильность заполнения.", Toast.LENGTH_SHORT);
                toast.show();

            }

            if(level == 0){

                successRefill = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка в уровне заполненности бака! Проверьте правильность заполнения.", Toast.LENGTH_SHORT);
                toast.show();

            }

            if(mark.equals("")){

                successRefill = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка в марке бензина! В случае, если марка неизвестна, впишите \"Другая\".", Toast.LENGTH_SHORT);
                toast.show();

            }

        }catch (Exception ex){

            ex.printStackTrace();

            successRefill = false;
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка в данных заправки! Проверьте правильность заполнения.", Toast.LENGTH_SHORT);
            toast.show();
        }



        if(successRefill) {

            ContentValues values = new ContentValues();
            values.put(databaseHelper.VOLUME_COLUMN, volume);
            values.put(databaseHelper.MARK_COLUMN, mark);
            values.put(databaseHelper.PRICE_COLUMN, price);
            values.put(databaseHelper.RANGE_COLUMN, range);
            values.put(databaseHelper.LEVEL_COLUMN, level);
            values.put(databaseHelper.REFILL_DATE_COLUMN, refillDate);

            sdb.insert("refills", null, values);

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Вы успешно заправились на " + range + " километре пробега!", Toast.LENGTH_SHORT);
            toast.show();

            cursor.close();

            setResult(RESULT_OK);

            finish();
        }

        cursor.close();

    }

    public void checkGeo(View view) {

        mapController();

    }
}
