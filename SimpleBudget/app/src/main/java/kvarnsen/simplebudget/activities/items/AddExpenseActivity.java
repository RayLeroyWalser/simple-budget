package kvarnsen.simplebudget.activities.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.activities.MainActivity;
import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;

/*
    A dialog-like activity that prompts a user to add a new expense for a line item.

    Started by ItemHistoryActivity.
 */

public class AddExpenseActivity extends ActionBarActivity {

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

            Context context = getApplicationContext();
            CharSequence text = "Item name was not provided";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();

            finish();
        }
    }

    public void onAddExpenseClick(View v) {

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        EditText descHolder = (EditText) findViewById(R.id.desc);
        EditText amountHolder = (EditText) findViewById(R.id.amount);

        String desc = descHolder.getText().toString();
        String amountStr = amountHolder.getText().toString();

        // get current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        String date = formatter.format(c.getTime());

        // get item
        DBHelper myDb = DBHelper.getInstance(this);
        LineItem item = myDb.getLineItem(itemName);

        if(desc.equals("") || amountStr.equals("")) {

            text = "Both a description and an amount must be entered, please try again!";
            Toast.makeText(context, text, duration).show();

        } else {

            int amount = Integer.parseInt(amountStr);

            if(!(desc.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();

            } else if(Integer.parseInt(amountStr) > item.getRemaining()) {

                text = "Expense exceeds remaining budget for that item, please try again!";
                Toast.makeText(context, text, duration).show();

            } else {

                boolean result = myDb.addExpense(itemName, date, desc, amount);

                if(result) {
                    updateBudget(amount);

                    text = "Expense added!";
                    Toast.makeText(context, text, duration).show();
                    finish();

                }

            }

        }


    }

    // convenience method to handle SharedPreferences updating of "spent" value
    public void updateBudget(int spent) {

        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("curSpent", preferences.getInt("curSpent", 0) + spent);
        editor.commit();

    }

}
