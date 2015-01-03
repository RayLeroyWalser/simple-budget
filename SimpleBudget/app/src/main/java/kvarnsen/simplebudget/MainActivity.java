package kvarnsen.simplebudget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;
import kvarnsen.simplebudget.ui.BudgetDialogFragment;


public class MainActivity extends ActionBarActivity implements BudgetDialogFragment.BudgetDialogListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DBHelper db;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    private int curBudget = 0;
    private int totalSpent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
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

        db = new DBHelper(this);

        if(curBudget == 0) {
            DialogFragment fragment = new BudgetDialogFragment();
            fragment.show(getSupportFragmentManager(), "budget");
        } else {
            // create a card containing budget details and display
            Log.w("SB", "Budget is: " + Integer.toString(curBudget));

            // if db entries for line items exist, intialise adapter and start recycler view
            // else prompt user to add items
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

        Log.w("SB", "Dialog Positive clicked!");

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        BudgetDialogFragment mBudgetDialog = (BudgetDialogFragment) dialog;

        editor.putInt("curBudget", mBudgetDialog.getBudget());

        editor.commit();

    }

    public void onItemViewClick(View v) {

        ArrayList myLineItems = db.getAllLineItems();
        String result = "";
        LineItem curItem;

        for(int i=0; i < myLineItems.size(); i++) {

            curItem = (LineItem) myLineItems.get(i);

            result = result + curItem.id + "\n" + curItem.name + "\n" + curItem.budgeted + "\n" + curItem.spent + "\n" + curItem.remaining + "\n\n";

        }

        Log.w("Current Items", result);
    }

    public void deleteDatabase(View v) {

        boolean result = db.deleteDatabase();

        if(result)
            Log.w("Deletion", "Success");
        else
            Log.w("Deletion", "Failed");

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
