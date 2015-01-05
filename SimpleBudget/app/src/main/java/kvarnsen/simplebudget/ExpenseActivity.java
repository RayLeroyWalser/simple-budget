package kvarnsen.simplebudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;


public class ExpenseActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "MyBudgetPrefs";

    private LineItem item;
    private String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Toolbar toolbar = (Toolbar) findViewById(R.id.expense_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();

        if(b!=null) {

            itemName = b.getString("ITEM_NAME");
            getSupportActionBar().setTitle(itemName + ": Add Expense");

        } else {

            // some toast

            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expense, menu);
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

    public void addExpense(View v) {

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        EditText descHolder = (EditText) findViewById(R.id.desc);
        EditText amountHolder = (EditText) findViewById(R.id.amount);

        String desc = descHolder.getText().toString();
        int amount = Integer.parseInt(amountHolder.getText().toString());

        // get current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        String date = formatter.format(c.getTime());

        // get item
        DBHelper myDb = DBHelper.getInstance(this);
        item = myDb.getLineItem(itemName);

        // validate that expense does not exceed budget
        if(!(amount > item.remaining)) {

            boolean result = myDb.addHistoryExpense(itemName, date, desc, amount);

            if(result) {
                updateBudget(amount);

                text = "Expense added!";
                Toast.makeText(context, text, duration).show();
                finish();

            } else {

                text = "Failed to add expense";
                Toast.makeText(context, text, duration).show();
                finish();
            }

        } else {
            text = "Expense exceeds remaining budget for that item, please try again!";
            Toast.makeText(context, text, duration).show();

        }


    }

    public void updateBudget(int spent) {

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("curSpent", preferences.getInt("curSpent", 0) + spent);
        editor.commit();

    }
}
