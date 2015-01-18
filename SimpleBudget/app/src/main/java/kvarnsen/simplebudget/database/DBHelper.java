package kvarnsen.simplebudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import kvarnsen.simplebudget.containers.Deposit;
import kvarnsen.simplebudget.containers.Expense;
import kvarnsen.simplebudget.containers.Goal;
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

    private static final String DATABASE_NAME = "SimpleBudget.db";
    private static final String BUDGET_TABLE_NAME = "budget";
    private static final String BUDGET_ITEM_ID = "id";
    private static final String BUDGET_ITEM_NAME = "name";
    private static final String BUDGET_ITEM_BUDGETED = "budgeted";
    private static final String BUDGET_ITEM_SPENT = "spent";
    private static final String BUDGET_ITEM_REMAINING = "remaining";

    private Context myContext = null;

    /***********************************************************
     *
     * OVERALL DATABASE FUNCTIONS
     * Handles functionality to manage the database as a whole
     *
     ***********************************************************/

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
        db.execSQL(
                "create table goals " +
                        "(id integer primary key, name text, goal integer, deposited integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS budget");
        db.execSQL("DROP TABLE IF EXISTS goals");
        onCreate(db);
    }

    public boolean checkNameExists(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor c = myDb.rawQuery("select * from budget where name='" + name + "'", null);

        if(c.getCount() > 0)
            return true;
        else
            return false;
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
        return (int) DatabaseUtils.queryNumEntries(myDb, BUDGET_TABLE_NAME);
    }

    public void clearBudget() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM BUDGET", null);

        res.moveToFirst();

        while(!res.isAfterLast()) {

            myDb.execSQL("DROP TABLE " + createTableName(res.getString(res.getColumnIndex("name"))));

            res.moveToNext();

        }

        myDb.execSQL("DROP TABLE BUDGET");

    }

    public void checkBudgetIsDefined() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("CREATE TABLE IF NOT EXISTS BUDGET " + "(id integer primary key, name text,budgeted integer, spent integer, remaining integer)");

    }

    /*****************************************************************
     *
     * LINE ITEM FUNCTIONS
     * Manages creation, deletion and manipulation of line items
     *
     *****************************************************************/

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

    public void updateItem(String tableName, String oldName, String newName, int newBudget, int spent) {

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
            myDb.execSQL("ALTER TABLE " + tableName + " RENAME TO " + newName);

    }

    public void deleteLineItem(String itemName, String tableName) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        // delete from Budget
        myDb.execSQL("DELETE FROM BUDGET WHERE NAME ='" + itemName + "'");

        // delete Item table
        myDb.execSQL("DROP TABLE " + tableName);

    }

    /*****************************************************************
     *
     * EXPENSE FUNCTIONS
     * Manages creation, deletion and manipulation of expenses
     *
     *****************************************************************/

    public boolean addExpense(String tableName, String name, String date, String desc, int amount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", desc);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(tableName, null, cv);

        updateItemState(tableName, name);

        return true;
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

    /*
        Creates an ArrayList of Expense objects for ItemHistoryActivity to parse and display
     */
    public ArrayList getExpenseHistory(String tableName) {

        ArrayList history = new ArrayList<Expense>();
        Expense cur;
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + tableName, null);

        if(res.getCount() <= 0) {
            return history;
        }

        res.moveToFirst();

        while(!res.isAfterLast()) {

            cur = new Expense(
                    res.getString(res.getColumnIndex("name")),
                    res.getString(res.getColumnIndex("date")),
                    res.getInt(res.getColumnIndex("amount"))
            );

            history.add(cur);

            res.moveToNext();

        }

        return history;
    }

    public void deleteExpense(String tableName, String itemName, String expenseName) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        myDb.execSQL("DELETE FROM " + tableName + " WHERE NAME ='" + expenseName + "'");

        updateItemState(tableName, itemName);

    }

    /*****************************************************************
     *
     * GOAL FUNCTIONS
     * Manages creation, adjustment and deletion of goals
     *
     *****************************************************************/

    public boolean checkGoalExists(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS WHERE NAME='" + name + "'", null);

        if(res.getCount() > 0)
            return true;
        else
            return false;

    }

    public boolean addGoal(String name, int goalAmount, int initDeposit) {

        if(checkGoalExists(name)) {
            return false;
        } else {
            SQLiteDatabase myDb = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("name", name);
            cv.put("goal", goalAmount);
            cv.put("deposited", initDeposit);

            myDb.insert("goals", null, cv);

            myDb.execSQL(
                    "create table " + createTableName(name) + " " +
                            "(id integer primary key, name text, amount integer, date text)"
            );

            return true;
        }

    }

    public void adjustGoal(String oldName, String newName, int newGoal) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from goals where name='" + oldName + "'", null);

        res.moveToFirst();

        cv.put("name", newName);
        cv.put("goal", newGoal);

        myDb.update("goals", cv, "id = ?", new String[] {
                Integer.toString(res.getInt(res.getColumnIndex("id")))
        });

        if(!oldName.equals(newName))
            myDb.execSQL("ALTER TABLE " + createTableName(oldName) + " RENAME TO " + createTableName(newName));

    }

    public void updateGoalState(String goalName, int newAmount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS WHERE NAME='" + goalName + "'", null);

        res.moveToFirst();

        cv.put("name", goalName);
        cv.put("goal", res.getInt(res.getColumnIndex("goal")));
        cv.put("deposited", res.getInt(res.getColumnIndex("deposited")) + newAmount);

        myDb.update("goals", cv, "id = ?", new String[] {
                Integer.toString(res.getInt(res.getColumnIndex("id")))
        });

    }

    public void deleteGoal(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        myDb.execSQL("DELETE FROM GOALS WHERE NAME ='" + name + "'");
        myDb.execSQL("DROP TABLE " + createTableName(name));

    }

    public ArrayList getAllGoals() {

        ArrayList goals = new ArrayList<Goal>();
        Goal curGoal;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals", null);

        res.moveToFirst();

        while(!res.isAfterLast()) {

            curGoal = new Goal(
                    res.getString(res.getColumnIndex("name")),
                    res.getInt(res.getColumnIndex("goal")),
                    res.getInt(res.getColumnIndex("deposited"))
            );

            goals.add(curGoal);

            res.moveToNext();

        }

        return goals;

    }

    // Used to create ArrayAdapter for MakeDepositActivity's Spinner
    public ArrayList<String> getGoalNames() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals", null);

        ArrayList<String> goalNames = new ArrayList<String>();

        res.moveToFirst();

        while(!res.isAfterLast()) {

            goalNames.add(res.getString(res.getColumnIndex("name")));

            res.moveToNext();

        }

        return goalNames;

    }

    public Goal getGoal(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals where name='" + name + "'", null);

        res.moveToFirst();

        return new Goal(
                res.getString(res.getColumnIndex("name")),
                res.getInt(res.getColumnIndex("goal")),
                res.getInt(res.getColumnIndex("deposited"))
        );

    }

    public void deleteGoals() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS", null);

        res.moveToFirst();

        while(!res.isAfterLast()) {

            myDb.execSQL("DROP TABLE " + createTableName(res.getString(res.getColumnIndex("name"))));

            res.moveToNext();

        }

        myDb.execSQL("DROP TABLE GOALS");

    }

    public void checkGoalTableIsDefined() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("CREATE TABLE IF NOT EXISTS GOALS " + "(id integer primary key, name text, goal integer, deposited integer)");


    }

    /*****************************************************************
     *
     * DEPOSIT FUNCTIONS
     * Manages creation, adjustment and deletion of goal deposits
     *
     *****************************************************************/

    public void addDeposit(String goalName, String depositName, String date, int amount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", depositName);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(createTableName(goalName), null, cv);
        addExpense(createTableName(depositName), depositName, date, "Deposit: " + goalName, amount);

        updateGoalState(goalName, amount);
    }

    public void removeDeposit(String goalName, String depositName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("DELETE FROM " + createTableName(goalName) + " WHERE NAME ='" + createTableName(depositName) + "'");

    }


    public ArrayList getDepositHistory(String goalName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM " + createTableName(goalName), null);
        ArrayList depositHistory = new ArrayList<Deposit>();
        Deposit curDeposit;

        res.moveToFirst();

        while(!res.isAfterLast()) {

            curDeposit = new Deposit(
                    res.getString(res.getColumnIndex("name")),
                    res.getString(res.getColumnIndex("date")),
                    res.getInt(res.getColumnIndex("amount"))
            );

            depositHistory.add(curDeposit);

            res.moveToNext();

        }

        return depositHistory;

    }

    /*****************************************************************
     *
     * CONVENIENCE METHODS
     *
     *****************************************************************/

    public String createTableName(String name) {

        String tableName = name.replaceAll("\\s+", "");
        tableName = tableName.toLowerCase();

        return tableName;

    }


}
