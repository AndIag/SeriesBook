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
        String select = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + " = '" + categoryName + "' AND " + IS_NOT_DELETED;
        Cursor cursor = db.rawQuery(select, null);

        Category category = null;
        if (cursor != null) {
            cursor.moveToFirst();
            category = new Category();
            category.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME)));
            category.setColor(cursor.getInt(cursor.getColumnIndex(DBHelper.CATEGORY_COLOR)));
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
            category.setColor(cursor.getInt(cursor.getColumnIndex(DBHelper.CATEGORY_COLOR)));
            categories.add(category);
        }
        if (cursor != null) cursor.close();
        db.close();

        return categories;
    }

    public Category addCategory(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Check if the category already exists
        String checkup = "SELECT * FROM " + DBHelper.CATEGORY_TABLE + " WHERE " + DBHelper.CATEGORY_NAME + " = '" + category.getName() + "'";
        Cursor cursor = db.rawQuery(checkup, null);
        if (cursor.getCount() > 0) {
            return retrieveCategory(cursor, category);
        }
        cursor.close();

        //Insert category
        ContentValues c = new ContentValues();
        c.put(DBHelper.CATEGORY_NAME, category.getName());
        c.put(DBHelper.CATEGORY_COLOR, category.getColor());
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
        s.put(DBHelper.CATEGORY_COLOR, category.getColor());

        if (db.update(DBHelper.CATEGORY_TABLE, s, where, null) > 0) {
            //Mark all series as deleted for this category
            for (Serie serie : getSerieByCategory(category, true)) {
                updateSerie(serie, false);
            }
            return category;
        }
        return null;
    }

    public Category removeCategory(String categoryName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Category category = getCategory(categoryName);

        String where = DBHelper.CATEGORY_NAME + "='" + categoryName + "'";

        ContentValues c = new ContentValues();
        c.put(DBHelper.DELETED, true);

        if (db.update(DBHelper.CATEGORY_TABLE, c, where, null) > 0) {
            for (Serie serie : getSerieByCategory(category, false)) {
                updateSerie(serie, true);
            }
            return category;
        }
        return null;
    }
    //endregion

    //region Series
    public List<Serie> getSerieByCategory(Category category, boolean deleted) {
        List<Serie> series = new ArrayList<>();
        Serie serie;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String wantDeleted;
        if (deleted) {
            wantDeleted = IS_DELETED;
        } else {
            wantDeleted = IS_NOT_DELETED;
        }

        String execute = "SELECT " + DBHelper.SERIE_ID + ", "
                + DBHelper.SERIE_NAME + ", " + DBHelper.SERIE_CHAPTER + ", "
                + "COALESCE(" + DBHelper.SERIE_SEASON + ", -1) AS " + DBHelper.SERIE_SEASON + ", "
                + DBHelper.SERIE_IMAGE
                + " FROM " + DBHelper.SERIE_TABLE
                + " WHERE " + DBHelper.SERIE_CATEGORY + " = " + category.getId()
                + " AND " + wantDeleted + " ORDER BY " + DBHelper.SERIE_NAME + " DESC";
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

        //Check if the serie already exists
        String checkup = "SELECT * FROM " + DBHelper.SERIE_TABLE + " WHERE " + DBHelper.SERIE_NAME + " = '" + serie.getName() + "'";
        Cursor cursor = db.rawQuery(checkup, null);
        if (cursor.getCount() > 0) {
            return retrieveSerie(cursor, serie);
        }
        cursor.close();

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIE_NAME, serie.getName());
        s.put(DBHelper.SERIE_CATEGORY, serie.getCategory().getId());
        s.put(DBHelper.SERIE_CHAPTER, serie.getChapter());
        s.put(DBHelper.SERIE_SEASON, serie.getSeason());
        s.put(DBHelper.SERIE_IMAGE, serie.getImageUrl());
        s.put(DBHelper.DELETED, false);

        long id = db.insert(DBHelper.SERIE_TABLE, null, s);
        serie.setId(id);
        return serie;
    }

    private Serie retrieveSerie(Cursor cursor, Serie serie) {
        cursor.moveToFirst();

        //If serie is not deleted
        if (cursor.getInt(cursor.getColumnIndex(DBHelper.DELETED)) == 0) {
            return null;
        }

        //Retrieve Serie
        serie.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.SERIE_ID)));
        cursor.close();

        //Update deleted value
        if (updateSerie(serie, false)) {
            return serie;
        }
        return null;
    }

    public boolean removeSerie(long serieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIE_ID + "='" + serieId + "'";

        ContentValues s = new ContentValues();
        s.put(DBHelper.DELETED, true);

        return db.update(DBHelper.SERIE_TABLE, s, where, null) > 0;
    }

    private boolean updateSerie(Serie serie, boolean deleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = DBHelper.SERIE_ID + "=" + serie.getId();

        ContentValues s = new ContentValues();
        s.put(DBHelper.DELETED, deleted);

        return db.update(DBHelper.SERIE_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieChapter(long serieId, int chapter) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIE_ID + "=" + serieId;

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIE_CHAPTER, chapter);

        return db.update(DBHelper.SERIE_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieChapter(Serie serie, int chapter) {
        return updateSerieChapter(serie.getId(), chapter);
    }

    public boolean updateSerieSeason(long serieId, int season) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String where = DBHelper.SERIE_ID + "=" + serieId;

        ContentValues s = new ContentValues();
        s.put(DBHelper.SERIE_SEASON, season);

        return db.update(DBHelper.SERIE_TABLE, s, where, null) > 0;
    }

    public boolean updateSerieSeason(Serie serie, int season) {
        return updateSerieSeason(serie.getId(), season);
    }
    //endregion
}
