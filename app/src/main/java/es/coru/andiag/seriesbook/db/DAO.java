package es.coru.andiag.seriesbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.entities.Category;
import es.coru.andiag.seriesbook.entities.Serie;

/**
 * Created by iagoc on 20/02/2016.
 */
public class DAO {

    private static DAO ourInstance;
    private static DBHelper dbHelper;

    private DAO() {
    }

    public DAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DAO getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DAO(context);
        }
        return ourInstance;
    }

    //region Categories
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        Category category;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String execute = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " ORDER BY " + DBHelper.CATEGORY_NAME;
        Cursor cursor = db.rawQuery(execute, null);

        while (cursor != null && cursor.moveToNext()) {
            category = new Category();
            category.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME)));
            category.setColor(cursor.getInt(cursor.getColumnIndex(DBHelper.CATEGORY_COLOR)));
            categories.add(category);
        }
        if (cursor != null) cursor.close();
        db.close();

        return categories;
    }

    public Category addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues c = new ContentValues();
        c.put(DBHelper.CATEGORY_NAME, category.getName());
        c.put(DBHelper.CATEGORY_COLOR, category.getColor());

        long id = db.insert(DBHelper.CATEGORY_TABLE, null, c);
        category.setId(id);
        return category;
    }

    public boolean removeCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String delete = DBHelper.CATEGORY_NAME + "='" + categoryName + "'";

        return (db.delete(DBHelper.CATEGORY_TABLE, delete, null)) > 0;
    }
    //endregion

    //region Series
    public List<Serie> getSerieByCategory(Category category) {
        List<Serie> series = new ArrayList<>();
        Serie serie;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String execute = "SELECT " + DBHelper.SERIE_ID + ", "
                + DBHelper.SERIE_NAME + ", " + DBHelper.SERIE_CHAPTER + ", "
                + "COALESCE(" + DBHelper.SERIE_SEASON + ", -1) AS " + DBHelper.SERIE_SEASON + ", "
                + DBHelper.SERIE_IMAGE
                + " FROM " + DBHelper.SERIE_TABLE
                + " WHERE " + DBHelper.SERIE_CATEGORY + " = " + category.getId() + " ORDER BY " + DBHelper.SERIE_NAME;
        Cursor cursor = db.rawQuery(execute, null);

        while (cursor != null && cursor.moveToNext()) {
            serie = new Serie();
            serie.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.SERIE_ID)));
            serie.setName(cursor.getString(cursor.getColumnIndex(DBHelper.SERIE_NAME)));
            serie.setCategory(category);
            serie.setChapter(cursor.getInt(cursor.getColumnIndex(DBHelper.SERIE_CHAPTER)));
            serie.setSeason(cursor.getInt(cursor.getColumnIndex(DBHelper.SERIE_SEASON)));
            serie.setImageUrl(cursor.getString(cursor.getColumnIndex(DBHelper.SERIE_IMAGE)));
            series.add(serie);
        }
        if (cursor != null) cursor.close();
        db.close();

        return series;
    }

    public Serie addSerie(Serie serie) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIE_NAME, serie.getName());
        s.put(DBHelper.SERIE_CATEGORY, serie.getCategory().getId());
        s.put(DBHelper.SERIE_CHAPTER, serie.getChapter());
        s.put(DBHelper.SERIE_SEASON, serie.getSeason());
        s.put(DBHelper.SERIE_IMAGE, serie.getImageUrl());

        long id = db.insert(DBHelper.SERIE_TABLE, null, s);
        serie.setId(id);
        return serie;
    }

    public boolean removeSerie(long serieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String delete = DBHelper.SERIE_ID + "='" + serieId + "'";

        return (db.delete(DBHelper.SERIE_TABLE, delete, null)) > 0;
    }

    //endregion
}
