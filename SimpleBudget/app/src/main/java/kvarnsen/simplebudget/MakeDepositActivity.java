package kvarnsen.simplebudget;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import kvarnsen.simplebudget.database.DBHelper;


public class MakeDepositActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private DBHelper myDb;
    private String itemName;
    private String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_deposit);

        Bundle b = getIntent().getExtras();
        itemName = b.getString("ITEM_NAME");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(itemName + ": Make Goal Deposit");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myDb = DBHelper.getInstance(this);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myDb.getGoalNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(adapter);


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        goalName = (String) parent.getItemAtPosition(pos);

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onMakeDepositClick(View v) {

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        EditText amountHolder = (EditText) findViewById(R.id.amount);
        String amountStr = amountHolder.getText().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        String date = formatter.format(c.getTime());

        if(amountStr.equals("")) {

            text = "Deposit amount must be specified, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            if(Integer.parseInt(amountStr) > myDb.getLineItem(itemName).getRemaining()) {

                text = "Expense exceeds remaining budget for that item, please try again!";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.addDeposit(goalName, itemName, date, Integer.parseInt(amountStr));

                text = "Deposit added!";
                Toast.makeText(context, text, duration).show();

                finish();

            }

        }


    }

}
