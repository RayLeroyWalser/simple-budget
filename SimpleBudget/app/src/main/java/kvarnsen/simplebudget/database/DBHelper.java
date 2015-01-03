package kvarnsen.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kvarnsen.simplebudget.containers.LineItem;

/**
 * Created by joshuapancho on 3/01/15.
 */

/*
    Handles SQLite Database creation, updating and deletion.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final String DATABASE_NAME = "SimpleBudget.db";
    public static final String BUDGET_TABLE_NAME = "budget";
    public static final String BUDGET_ITEM_ID = "id";
    public static final String BUDGET_ITEM_NAME = "name";
    public static final String BUDGET_ITEM_BUDGETED = "budgeted";
    public static final String BUDGET_ITEM_SPENT = "spent";
    public static final String BUDGET_ITEM_REMAINING = "remaining";

    Context myContext = null;

    public static DBHelper getInstance(Context context) {

        if(instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }

        return instance;

    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table budget " +
                        "(id integer primary key, name text,budgeted integer, spent integer, remaining integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS budget");
        onCreate(db);
    }

    public boolean insertLineItem(String name, int budgeted, int spent) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int remaining = budgeted - spent;

        cv.put("name", name);
        cv.put("budgeted", budgeted);
        cv.put("spent", spent);
        cv.put("remaining", remaining);

        myDb.insert("budget", null, cv);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res =  myDb.rawQuery( "select * from budget where id="+id+"", null );
        return res;
    }

    public int getNoRows() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(myDb, BUDGET_TABLE_NAME);
        return numRows;
    }

    public boolean updateLineItem(int id, String name, int budgeted, int spent) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int remaining = budgeted - spent;

        cv.put("name", name);
        cv.put("budgeted", budgeted);
        cv.put("spent", spent);
        cv.put("remaining", remaining);

        myDb.update("budget", cv, "id = ?", new String[] {
                Integer.toString(id)
        });

        return true;
    }

    public int deleteLineItem(int id) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        return myDb.delete("budget", "id = ?", new String[] {
                Integer.toString(id)
        });

    }

    public ArrayList getAllLineItems() {

        ArrayList lineItems = new ArrayList<LineItem>();
        LineItem curItem;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from budget", null);

        res.moveToFirst();

        while(!res.isAfterLast()) {

            curItem = new LineItem(
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)),
                    res.getString(res.getColumnIndex(BUDGET_ITEM_NAME)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_SPENT)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_REMAINING))
                    );

            lineItems.add(curItem);

            res.moveToNext();

        }

        return lineItems;

    }

    public boolean deleteDatabase() {

        boolean result = false;

        if(myContext != null) {
            this.close();
            result = myContext.deleteDatabase(DATABASE_NAME);
        }

        return result;
    }






}
