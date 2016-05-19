package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Artem on 19.05.16.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{

    private static final String DATABASE_NAME = "autoAppBase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_TABLE = "refills";
        public static final String VOLUME_COLUMN = "volume";
        public static final String MARK_COLUMN = "mark";
        public static final String PRICE_COLUMN = "price";
        public static final String RANGE_COLUMN = "range";

    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + VOLUME_COLUMN
            + " double, " + MARK_COLUMN + " text not null, " + PRICE_COLUMN
            + " double, " + RANGE_COLUMN + " double);";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DATABASE_CREATE_SCRIPT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(db);

    }
}
