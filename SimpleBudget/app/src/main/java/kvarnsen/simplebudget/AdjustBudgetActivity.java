package kvarnsen.simplebudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kvarnsen.simplebudget.database.DBHelper;

/*
    Prompts user to enter a new budget. Called from MainActivity.
 */

public class AdjustBudgetActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_budget);

        Toolbar toolbar = (Toolbar) findViewById(R.id.adjust_budget_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Adjust Budget");

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /*
        Called by button click in layout file.
     */
    public void onSubmit(View v) {

        EditText amountHolder = (EditText) findViewById(R.id.amount);
        int newBudget = Integer.parseInt(amountHolder.getText().toString());

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        // check that budget does not exceed amount already allocated
        DBHelper myDb = DBHelper.getInstance(this);

        if(newBudget < myDb.getTotalAllocated()) {
            text = "New budget amount is less than amount already allocated, please try again";
            Toast.makeText(context, text, duration).show();
        } else {
            SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("curBudget", newBudget);
            boolean result = editor.commit();

            if(result) {
                text = "Budget adjusted";
                Toast.makeText(context, text, duration).show();
            } else {
                text = "Failed to adjust budget";
                Toast.makeText(context, text, duration).show();
            }

            finish();
        }

    }

}
