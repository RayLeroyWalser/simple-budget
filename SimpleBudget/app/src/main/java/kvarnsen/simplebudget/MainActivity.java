package kvarnsen.simplebudget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kvarnsen.simplebudget.database.DBHelper;
import kvarnsen.simplebudget.ui.BudgetDialogFragment;
import kvarnsen.simplebudget.ui.ItemHistoryActivity;
import kvarnsen.simplebudget.ui.MainAdapter;


public class MainActivity extends ActionBarActivity implements BudgetDialogFragment.BudgetDialogListener{

    public final static String PREFS_NAME = "MyBudgetPrefs";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DBHelper db;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    CardView budgetCard;

    private int curBudget = 0;  // how much of the budget has been spent
    private int totalSpent = 0;

    private ArrayList myLineItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetCard = (CardView) findViewById(R.id.budget_card);
        budgetCard.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);
        Log.w("SB", "curBudget: " + Integer.toString(curBudget));

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Budget");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer, R.string.main);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = DBHelper.getInstance(this);

        if(curBudget == 0) {
            DialogFragment fragment = new BudgetDialogFragment();
            fragment.show(getSupportFragmentManager(), "budget");
        } else {
            initCards();
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Log.w("SB", "Dialog Positive clicked!");

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        BudgetDialogFragment mBudgetDialog = (BudgetDialogFragment) dialog;

        editor.putInt("curBudget", mBudgetDialog.getBudget());
        editor.commit();

        curBudget = mBudgetDialog.getBudget();

        initCards();

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w("SB", "Resume called");

        if(db.getNoRows() != 0) {

            myLineItems = db.getAllLineItems();
            CardView placeholder = (CardView) findViewById(R.id.item_placeholder);
            placeholder.setVisibility(View.GONE);

            mAdapter = new MainAdapter(myLineItems);
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    public void initCards() {

        TextView budgetContent = (TextView) budgetCard.findViewById(R.id.budget_content);

        budgetContent.setText(
                "Budgeted: $" + Integer.toString(curBudget) + ".00\nSpent: $" + Integer.toString(totalSpent) + ".00\n"
                + "Remaining: $" + Integer.toString(curBudget - totalSpent) + ".00\n"
        );

        budgetCard.setVisibility(View.VISIBLE);

        if(db.getNoRows() != 0) {

            myLineItems = db.getAllLineItems();
            CardView placeholder = (CardView) findViewById(R.id.item_placeholder);
            placeholder.setVisibility(View.GONE);

            mAdapter = new MainAdapter(myLineItems);
            mRecyclerView.setAdapter(mAdapter);

        } else {
            Log.w("SB", "Database empty");
        }
    }

    public void addLineItem(View v) {

        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);

    }

    public void deleteDatabase(View v) {

        boolean result = db.deleteDatabase();

        if(result) {
            Context context = getApplicationContext();
            CharSequence text = "Database cleared";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "Database failed to clear";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();
        }

    }

    public void onItemClick(View v) {

        CardView callHolder = (CardView) v;
        String itemName = ((TextView) callHolder.findViewById(R.id.title)).getText().toString();

        Intent intent = new Intent(this, ItemHistoryActivity.class);
        intent.putExtra("ITEM_NAME", itemName);
        startActivity(intent);

    }

    public void onSettingsClick(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onResume(); // buggy at the moment - noOfRows returning > 0
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // code adapted from http://stackoverflow.com/questions/6290599/prompt-user-when-back-button-is-pressed
    private void exit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to quit?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {

        exit();

    }

}
