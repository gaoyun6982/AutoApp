package com.android.gaoyun.autoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import data.DatabaseHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static TextView textCar;
    static TextView textRange;
    static TextView newRange;

    SQLiteDatabase sdb;
    DatabaseHelper databaseHelper;

    Cursor carCursor;
    Cursor dataCursor;

    Intent fuelAddIntent;

    public static final int REFILL_RESULT = 1;
    public static boolean REFILL_STATUS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newRange = (TextView) findViewById(R.id.newRangeText);
        textCar = (TextView) findViewById(R.id.modelText);           //Access to UI components
        textRange = (TextView) findViewById(R.id.rangeText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fuelAddIntent = new Intent(MainActivity.this, addFuelActivity.class);
                startActivityForResult(fuelAddIntent, REFILL_RESULT);

            }


        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*if(flag){

            Intent intent = new Intent(MainActivity.this, NewCarActivity.class);
            startActivity(intent);

            flag = false;

            drawMainUI();

        } else{

            drawMainUI();

        }*/

        try{

            drawMainUI();

        }catch (Exception needSQL){

            Intent intent = new Intent(MainActivity.this, NewCarActivity.class);
            startActivity(intent);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if((requestCode == REFILL_RESULT)&&(resultCode == RESULT_OK)){
            updateLastRefill();
            REFILL_STATUS = true;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_refill) {

            if(REFILL_STATUS){

                Intent fuelStatistics = new Intent(MainActivity.this, FuelStatisticsActivity.class);
                startActivity(fuelStatistics);

            }else{

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Нет данных по заправкам. Сначала добавьте их.", Toast.LENGTH_SHORT);
                toast.show();

            }

        }
                /*else if (id == R.id.nav_gallery) {

                } else if (id == R.id.nav_slideshow) {

                } else if (id == R.id.nav_manage) {

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateLastRefill(){
        try {
            dataCursor = sdb.query("refills", new String[]{databaseHelper.RANGE_COLUMN}, null, null, null, null, null);
            dataCursor.moveToLast();
            newRange.setText("Последняя заправка на " + dataCursor.getString(dataCursor.getColumnIndex(databaseHelper.RANGE_COLUMN)) + " км");
        } catch (Exception e) {
            dataCursor = sdb.query("car", new String[]{databaseHelper.RANGE_DEFAULT_COLUMN}, null, null, null, null, null);
            newRange.setText("Вы ещё ни разу не заправлялись с нами!");
        }
    }

    public void drawMainUI(){

        databaseHelper = new DatabaseHelper(this, "autoAppBase.db", null, 1);

        try {
            sdb = databaseHelper.getWritableDatabase();
        } catch (Exception sqlEx){
            sdb = databaseHelper.getReadableDatabase();
        }

        carCursor = sdb.query("car", new String[]{databaseHelper.CAR_MANUFACTURER, databaseHelper.CAR_MODEL, databaseHelper.RANGE_DEFAULT_COLUMN}, null, null, null, null, null);

        updateLastRefill();

        carCursor.moveToFirst();
        textCar.setText(carCursor.getString(carCursor.getColumnIndex(databaseHelper.CAR_MANUFACTURER)) + " " + carCursor.getString(carCursor.getColumnIndex(databaseHelper.CAR_MODEL)));

        carCursor.moveToFirst();
        textRange.setText("Вы с нами с " + carCursor.getString(carCursor.getColumnIndex(databaseHelper.RANGE_DEFAULT_COLUMN)) + " км");

    }

}
