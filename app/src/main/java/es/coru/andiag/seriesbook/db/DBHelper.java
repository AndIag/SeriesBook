package es.coru.andiag.seriesbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by iagoc on 06/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String CATEGORY_TABLE = "categories";
    public static final String SERIE_TABLE = "series";
    public static final String SERIE_ID = "_id";
    public static final String SERIE_CATEGORY = "_category_id";
    public static final String SERIE_NAME = "name";
    public static final String SERIE_SEASON = "season";
    public static final String SERIE_CHAPTER = "chapter";
    public static final String SERIE_IMAGE = "image_url";
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

        sqLiteDatabase.execSQL("CREATE TABLE " + SERIE_TABLE + "("
                + SERIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SERIE_CATEGORY + " INTEGER, "
                + SERIE_NAME + " TEXT NOT NULL UNIQUE, "
                + SERIE_SEASON + " INTEGER, "
                + SERIE_CHAPTER + " INTEGER NOT NULL, "
                + SERIE_IMAGE + " TEXT, "
                + "FOREIGN KEY(" + SERIE_CATEGORY + ") REFERENCES " + CATEGORY_TABLE + "(" + CATEGORY_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SERIE_TABLE);
        onCreate(sqLiteDatabase);
    }
}