package kvarnsen.simplebudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kvarnsen.simplebudget.database.DBHelper;

/*
    A dialog-like activity that prompts a user to enter an name and amount for
    a new line item.
 */

public class AddItemActivity extends ActionBarActivity {

    public final static String PREFS_NAME = "MyBudgetPrefs";

    private DBHelper myDb;
    private int curBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Budget");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myDb = DBHelper.getInstance(this);

    }

    public void onClick(View v) {

        EditText nameView = (EditText) findViewById(R.id.name);
        EditText amountView = (EditText) findViewById(R.id.amount);

        String name = nameView.getText().toString();
        String amountStr = amountView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        int allocated = myDb.getTotalAllocated();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);

        Log.w("Cur Budget", Integer.toString(curBudget));
        Log.w("Allocated", Integer.toString(allocated));
        Log.w("Amount Str", amountStr);

        // basic input validation
        if(amountStr.equals("") || name.equals("")) {

            text = "Invalid input, please try again!";
            Toast.makeText(context, text, duration).show();

        } else {

            if(allocated == curBudget) {
                text = "Current budget has already been completely allocated";
                Toast.makeText(context, text, duration).show();
                finish();
            } else if((allocated + Integer.parseInt(amountStr)) > curBudget) {
                text = "Amount exceeds remaining allocatable budget, please try again!";
                Toast.makeText(context, text, duration).show();
            } else {

                if(myDb.checkNameExists(name)) {
                    text = "Item with that name already exists, please try again!";
                    Toast.makeText(context, text, duration).show();
                } else {
                    myDb.insertLineItem(name, Integer.parseInt(amountStr), 0);
                    finish();
                }

            }

        }


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
}
