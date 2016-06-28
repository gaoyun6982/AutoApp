package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;

/**
 * Created by Artem on 19.05.16.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{

    private static final String DATABASE_NAME = "autoAppBase.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FILE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();

    private static final String DATABASE_TABLE_REFILL = "refills";
        public static final String VOLUME_COLUMN = "volume";
        public static final String MARK_COLUMN = "mark";
        public static final String PRICE_COLUMN = "price";
        public static final String RANGE_COLUMN = "range";
        public static final String LEVEL_COLUMN = "fuelLevel";

    private static final String DATABASE_TABLE_CAR = "car";
        public static final String RANGE_DEFAULT_COLUMN = "defRange";
        public static final String CAR_MANUFACTURER = "manufacturer";
        public static final String CAR_MODEL = "model";

    private static final String DATABASE_CREATE_REFILL_SCRIPT = "create table "
            + DATABASE_TABLE_REFILL + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + VOLUME_COLUMN
            + " double, " + MARK_COLUMN + " text not null, " + PRICE_COLUMN
            + " double, " + RANGE_COLUMN + " double, " + LEVEL_COLUMN + " integer);";

    private static final String DATABASE_CREATE_CAR_SCRIPT = "create table "
            + DATABASE_TABLE_CAR + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + CAR_MANUFACTURER
            + " text not null, " + CAR_MODEL + " text not null, " + RANGE_DEFAULT_COLUMN
            + " double);";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, FILE_DIR + File.separator + name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_REFILL_SCRIPT);
        db.execSQL(DATABASE_CREATE_CAR_SCRIPT);

    }

    public void createCar(SQLiteDatabase db){

        db.execSQL(DATABASE_CREATE_CAR_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_REFILL);
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_CAR);
        onCreate(db);

    }
}
