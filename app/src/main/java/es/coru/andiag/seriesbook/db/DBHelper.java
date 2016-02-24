package es.coru.andiag.seriesbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iagoc on 06/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String CATEGORY_TABLE = "categories";
    public static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_NAME = "name";
    public static final String CATEGORY_COLOR = "color"; //Color int value
    private final static String TAG = "DBHelper";
    private static final String DATABASE_NAME = "sbai.sqlite";
    private static final int VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CATEGORY_TABLE + " ("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_NAME + " TEXT NOT NULL UNIQUE, "
                + CATEGORY_COLOR + " INTEGER)");

        sqLiteDatabase.execSQL("INSERT INTO " + CATEGORY_TABLE + " ( "
                + CATEGORY_NAME + " , " + CATEGORY_COLOR + " )"
                + " VALUES " + "('Following',3)");
        sqLiteDatabase.execSQL("INSERT INTO " + CATEGORY_TABLE + " ( "
                + CATEGORY_NAME + " , " + CATEGORY_COLOR + " )"
                + " VALUES " + "('Favourites',3)");
        sqLiteDatabase.execSQL("INSERT INTO " + CATEGORY_TABLE + " ( "
                + CATEGORY_NAME + " , " + CATEGORY_COLOR + " )"
                + " VALUES " + "('Viewed',3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        onCreate(sqLiteDatabase);
    }
}