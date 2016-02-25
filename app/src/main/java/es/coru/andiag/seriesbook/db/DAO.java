package es.coru.andiag.seriesbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.coru.andiag.seriesbook.entities.Category;

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

}
