package es.coru.andiag.seriesbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.entities.Category;
import es.coru.andiag.seriesbook.entities.Series;

/**
 * Created by iagoc on 20/02/2016.
 */
public class DAO {

    private static final String IS_NOT_DELETED = DBHelper.DELETED + " = 0";
    private static final String IS_DELETED = DBHelper.DELETED + " = 1";
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
    private Category getCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String select = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + " = '" + categoryName.toLowerCase() + "' AND " + IS_NOT_DELETED;
        Cursor cursor = db.rawQuery(select, null);

        Category category = null;
        if (cursor != null) {
            cursor.moveToFirst();
            category = new Category();
            category.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME)));
            cursor.close();
        }
        return category;
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        Category category;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String execute = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + IS_NOT_DELETED + " ORDER BY " + DBHelper.CATEGORY_NAME;
        Cursor cursor = db.rawQuery(execute, null);

        while (cursor != null && cursor.moveToNext()) {
            category = new Category();
            category.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME)));
            categories.add(category);
        }
        if (cursor != null) cursor.close();
        db.close();

        return categories;
    }

    public Category addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Check if the category already exists
        String checkup = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + " = '" + category.getName().toLowerCase() + "'";
        Cursor cursor = db.rawQuery(checkup, null);
        if (cursor.getCount() > 0) {
            return retrieveCategory(cursor, category);
        }
        cursor.close();

        //Insert category
        ContentValues c = new ContentValues();
        c.put(DBHelper.CATEGORY_NAME, category.getName().toLowerCase());
        c.put(DBHelper.DELETED, false);

        long id = db.insert(DBHelper.CATEGORY_TABLE, null, c);
        category.setId(id);
        return category;
    }

    private Category retrieveCategory(Cursor cursor, Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor.moveToFirst();

        //If category is not deleted
        if (cursor.getInt(cursor.getColumnIndex(DBHelper.DELETED)) == 0) {
            return null;
        }

        //Retrieve Category
        category.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
        category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME)));
        cursor.close();

        //Update deleted value
        String where = DBHelper.CATEGORY_ID + "=" + category.getId();

        ContentValues s = new ContentValues();
        s.put(DBHelper.DELETED, false);

        if (db.update(DBHelper.CATEGORY_TABLE, s, where, null) > 0) {
            //Mark all series as deleted for this category
            for (Series series : getSerieByCategory(category, true)) {
                updateSerie(series, false);
            }
            return category;
        }
        return null;
    }

    public Category removeCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Category category = getCategory(categoryName);

        String where = DBHelper.CATEGORY_NAME + "='" + categoryName.toLowerCase() + "'";

        ContentValues c = new ContentValues();
        c.put(DBHelper.DELETED, true);

        if (db.update(DBHelper.CATEGORY_TABLE, c, where, null) > 0) {
            for (Series series : getSerieByCategory(category, false)) {
                updateSerie(series, true);
            }
            return category;
        }
        return null;
    }
    //endregion

    //region Series
    public List<Series> getSerieByCategory(Category category, boolean deleted) {
        List<Series> series = new ArrayList<>();
        Series serie;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String wantDeleted;
        if (deleted) {
            wantDeleted = IS_DELETED;
        } else {
            wantDeleted = IS_NOT_DELETED;
        }

        String execute = "SELECT " + DBHelper.SERIES_ID + ", "
                + DBHelper.SERIES_NAME + ", " + DBHelper.SERIES_CHAPTER + ", "
                + "COALESCE(" + DBHelper.SERIES_SEASON + ", -1) AS " + DBHelper.SERIES_SEASON + ", "
                + DBHelper.SERIES_IMAGE
                + " FROM " + DBHelper.SERIES_TABLE
                + " WHERE " + DBHelper.SERIES_CATEGORY + " = " + category.getId()
                + " AND " + wantDeleted + " ORDER BY " + DBHelper.SERIES_NAME + " ASC";
        Cursor cursor = db.rawQuery(execute, null);

        while (cursor != null && cursor.moveToNext()) {
            serie = new Series();
            serie.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.SERIES_ID)));
            serie.setName(cursor.getString(cursor.getColumnIndex(DBHelper.SERIES_NAME)));
            serie.setCategory(category);
            serie.setChapter(cursor.getInt(cursor.getColumnIndex(DBHelper.SERIES_CHAPTER)));
            serie.setSeason(cursor.getInt(cursor.getColumnIndex(DBHelper.SERIES_SEASON)));
            serie.setImageUrl(cursor.getString(cursor.getColumnIndex(DBHelper.SERIES_IMAGE)));
            series.add(serie);
        }
        if (cursor != null) cursor.close();
        db.close();

        return series;
    }

    public Series addSerie(Series series) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Check if the series already exists
        String checkup = "SELECT * FROM " + DBHelper.SERIES_TABLE + " WHERE " + DBHelper.SERIES_NAME + " = '" + series.getName().toLowerCase() + "'";
        Cursor cursor = db.rawQuery(checkup, null);
        if (cursor.getCount() > 0) {
            return retrieveSerie(cursor, series);
        }
        cursor.close();

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIES_NAME, series.getName().toLowerCase());
        s.put(DBHelper.SERIES_CATEGORY, series.getCategory().getId());
        s.put(DBHelper.SERIES_CHAPTER, series.getChapter());
        s.put(DBHelper.SERIES_SEASON, series.getSeason());
        s.put(DBHelper.SERIES_IMAGE, series.getImageUrl());
        s.put(DBHelper.DELETED, false);

        long id = db.insert(DBHelper.SERIES_TABLE, null, s);
        series.setId(id);
        return series;
    }

    private Series retrieveSerie(Cursor cursor, Series series) {
        cursor.moveToFirst();

        //If series is not deleted
        if (cursor.getInt(cursor.getColumnIndex(DBHelper.DELETED)) == 0) {
            return null;
        }

        //Retrieve Series
        series.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.SERIES_ID)));
        cursor.close();

        //Update deleted value
        if (updateSerie(series, false)) {
            return series;
        }
        return null;
    }

    public boolean removeSerie(long serieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIES_ID + "='" + serieId + "'";

        ContentValues s = new ContentValues();
        s.put(DBHelper.DELETED, true);

        return db.update(DBHelper.SERIES_TABLE, s, where, null) > 0;
    }

    private boolean updateSerie(Series series, boolean deleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = DBHelper.SERIES_ID + "=" + series.getId();

        ContentValues s = new ContentValues();
        s.put(DBHelper.DELETED, deleted);

        return db.update(DBHelper.SERIES_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieChapter(long serieId, int chapter) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIES_ID + "=" + serieId;

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIES_CHAPTER, chapter);

        return db.update(DBHelper.SERIES_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieChapter(Series series, int chapter) {
        return updateSerieChapter(series.getId(), chapter);
    }

    public boolean updateSerieSeason(long serieId, int season) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIES_ID + "=" + serieId;

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIES_SEASON, season);

        return db.update(DBHelper.SERIES_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieSeason(Series series, int season) {
        return updateSerieSeason(series.getId(), season);
    }
    //endregion
}
