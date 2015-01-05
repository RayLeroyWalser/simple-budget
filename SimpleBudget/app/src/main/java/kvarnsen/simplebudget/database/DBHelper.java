package kvarnsen.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kvarnsen.simplebudget.containers.ItemHistory;
import kvarnsen.simplebudget.containers.LineItem;

/**
 * Created by joshuapancho on 3/01/15.
 */

/*
    Handles SQLite Database creation, updating and deletion.

    Code adapted with alterations from http://www.tutorialspoint.com/android/android_sqlite_database.htm
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

    public boolean checkNameExists(String name) {

        boolean result = false;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor c = myDb.rawQuery("select * from budget where name='" + name + "'", null);

        if(c.getCount() > 0)
            result = true;

        return result;
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

        myDb.execSQL(
                "create table " + name + " " +
                        "(id integer primary key, name text, date text, amount integer)"
        );

        return true;
    }

    public LineItem getLineItem(String name) {

        LineItem item = null;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor c = myDb.rawQuery("select * from budget where name='" + name + "'", null);

        c.moveToFirst();

        while(!c.isAfterLast()) {

            item = new LineItem(
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_ID)),
                    c.getString(c.getColumnIndex(BUDGET_ITEM_NAME)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_BUDGETED)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_SPENT)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_REMAINING))
            );

            c.moveToNext();

        }

        return item;

    }

    public int getTotalAllocated() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select budgeted from budget", null);

        int totalAllocated = 0;

        res.moveToFirst();

        while(!res.isAfterLast()) {

            totalAllocated += res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED));
            res.moveToNext();

        }

        return totalAllocated;

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

    /*
        Creates an ArrayList of ItemHistory objects for ItemHistoryActivity to parse and display
     */
    public ArrayList getHistory(String name) {

        ArrayList history = new ArrayList<ItemHistory>();
        ItemHistory cur;
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + name, null);

        if(res.getCount() <= 0) {
            Log.w("History", "request failed"); // request failed with test case
            return history;
        }

        res.moveToFirst();

        while(!res.isAfterLast()) {

            cur = new ItemHistory(
                res.getString(res.getColumnIndex("name")),
                res.getString(res.getColumnIndex("date")),
                res.getInt(res.getColumnIndex("amount"))
            );

            history.add(cur);

            res.moveToNext();

        }

        return history;
    }

    public boolean addHistoryExpense(String name, String date, String desc, int amount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", desc);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(name, null, cv);

        updateItemState(name, amount);

        return true;
    }

    public void updateItemState(String name, int spent) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from budget where name='" + name + "'", null);

        res.moveToFirst();

        updateLineItem(
                res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)),
                name,
                res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED)),
                (res.getInt(res.getColumnIndex(BUDGET_ITEM_SPENT)) + spent)
        );

    }

    public boolean isOpen() {

        SQLiteDatabase myDb = this.getWritableDatabase();

        if(myDb.isOpen())
            return true;
        else
            return false;

    }



}
