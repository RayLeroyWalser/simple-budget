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

    public boolean insertLineItem(String tableName, String name, int budgeted, int spent) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int remaining = budgeted - spent;

        cv.put("name", name);
        cv.put("budgeted", budgeted);
        cv.put("spent", spent);
        cv.put("remaining", remaining);

        myDb.insert("budget", null, cv);

        myDb.execSQL(
                "create table " + tableName + " " +
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

    public int getTotalSpent() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from budget", null);

        int totalSpent = 0;

        res.moveToFirst();

        while(!res.isAfterLast()) {

            totalSpent += res.getInt(res.getColumnIndex(BUDGET_ITEM_SPENT));
            res.moveToNext();

        }

        return totalSpent;

    }

    public int getNoRows() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(myDb, BUDGET_TABLE_NAME);
        return numRows;
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
    public ArrayList getHistory(String tableName) {

        ArrayList history = new ArrayList<ItemHistory>();
        ItemHistory cur;
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + tableName, null);

        if(res.getCount() <= 0) {
            Log.w("DBHelper", "request failed"); // request failed with test case
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

    public boolean addHistoryExpense(String tableName, String name, String date, String desc, int amount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", desc);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(tableName, null, cv);

        updateItemState(tableName, name);

        return true;
    }

    public void updateItemState(String tableName, String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from budget where name='" + name + "'", null);

        res.moveToFirst();

        int budget = res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED));
        int spent = getItemSpent(tableName);
        int remaining = budget - spent;

        cv.put("name", name);
        cv.put("budgeted", budget);
        cv.put("spent", spent);
        cv.put("remaining", remaining);

        myDb.update("budget", cv, "id = ?", new String[] {
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });

    }

    public int getItemSpent(String tableName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + tableName, null);

        int spent = 0;

        res.moveToFirst();

        while(!res.isAfterLast()) {
            spent += res.getInt(res.getColumnIndex("amount"));

            res.moveToNext();
        }

        return spent;
    }

    public void updateItem(String tableName, String oldName, String newName, int newBudget, int spent) {

        // update item in budget
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from budget where name='" + oldName + "'", null);

        res.moveToFirst();

        cv.put("name", newName);
        cv.put("budgeted", newBudget);
        cv.put("spent", spent);
        cv.put("remaining", (newBudget - spent));

        myDb.update("budget", cv, "id = ?", new String[] {
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });

        newName = newName.toLowerCase();
        newName = newName.replaceAll("\\s+", "");

        if(!tableName.equals(newName))
            // update name of itemHistory table
            myDb.execSQL("ALTER TABLE " + tableName + " RENAME TO " + newName);

    }

    public void deleteLineItem(String itemName, String tableName) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        // delete from Budget
        myDb.execSQL("DELETE FROM BUDGET WHERE NAME ='" + itemName + "'");

        // delete Item table
        myDb.execSQL("DROP TABLE " + tableName);

    }

    public void updateExpense(String tableName, String itemName, String oldName, String newName, String date, int newAmount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from " + tableName + " where name='" + oldName + "'", null);

        res.moveToFirst();

        cv.put("name", newName);
        cv.put("date", date);
        cv.put("amount", newAmount);

        myDb.update(tableName, cv, "id = ?", new String[] {
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });

        updateItemState(tableName, itemName);

    }


    public boolean isOpen() {

        SQLiteDatabase myDb = this.getWritableDatabase();

        if(myDb.isOpen())
            return true;
        else
            return false;

    }



}
