package kvarnsen.simplebudget.activities.goals;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.database.DBHelper;


public class AddGoalActivity extends ActionBarActivity {

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Goal");

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
        EditText startDepositView = (EditText) findViewById(R.id.starting_deposit);

        String name = nameView.getText().toString();
        String amountStr = amountView.getText().toString();
        String startingDepositStr = startDepositView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        if(amountStr.equals("") || name.equals("")) {

            text = "Both a goal name and amount must be entered, please try again";
            Toast.makeText(context, text, duration).show();

        } else if(!(name.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

            text = "Goal name can only contain letters, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            boolean result = myDb.addGoal(name, Integer.parseInt(amountStr));

            if(!startingDepositStr.equals("")) {
                myDb.addDeposit(name, "Initial", Integer.parseInt(startingDepositStr), false);
            }

            if(result) {
                text = "Goal added!";
                Toast.makeText(context, text, duration).show();

                finish();
            } else {
                text = "A goal with that name already exists, please try again";
                Toast.makeText(context, text, duration).show();
            }

        }

    }

}
