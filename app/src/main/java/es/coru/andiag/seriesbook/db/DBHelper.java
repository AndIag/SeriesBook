package es.coru.andiag.seriesbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iagoc on 06/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String CATEGORY_TABLE = "categories";
    public static final String SERIES_TABLE = "series";
    public static final String SERIES_ID = "_id";
    public static final String SERIES_CATEGORY = "_category_id";
    public static final String SERIES_NAME = "name";
    public static final String SERIES_SEASON = "season";
    public static final String SERIES_CHAPTER = "chapter";
    public static final String SERIES_IMAGE = "image_url";
    public static final String CATEGORY_ID = "_id";
    public static final String CATEGORY_NAME = "name";
    public static final String DELETED = "deleted";
    private final static String TAG = "DBHelper";
    private static final String DATABASE_NAME = "sbai.sqlite";
    private static final int VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CATEGORY_TABLE + " ("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORY_NAME + " TEXT NOT NULL UNIQUE, "
                + DELETED + " INTEGER)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SERIES_TABLE + "("
                + SERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SERIES_CATEGORY + " INTEGER, "
                + SERIES_NAME + " TEXT NOT NULL UNIQUE, "
                + SERIES_SEASON + " INTEGER, "
                + SERIES_CHAPTER + " INTEGER NOT NULL, "
                + SERIES_IMAGE + " TEXT, "
                + DELETED + " INTEGER, "
                + "FOREIGN KEY(" + SERIES_CATEGORY + ") REFERENCES " + CATEGORY_TABLE + "(" + CATEGORY_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SERIES_TABLE);
        onCreate(sqLiteDatabase);
    }
}