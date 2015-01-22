package kvarnsen.simplebudget.activities.goals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.database.DBHelper;

public class AdjustDepositActivity extends ActionBarActivity {

    private String depositName;
    private String depositDate;
    private String itemName;
    private int depositAmount;
    private int remaining;

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_deposit);

        myDb = DBHelper.getInstance(this);

        Bundle b = getIntent().getExtras();
        depositName = b.getString("DEPOSIT_NAME");
        depositDate = b.getString("DEPOSIT_DATE");
        depositAmount = b.getInt("DEPOSIT_AMOUNT");
        itemName = b.getString("ITEM_NAME");
        remaining = b.getInt("ITEM_REMAINING");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(depositName);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onSubmitClick(View v) {

        EditText newAmountView = (EditText) findViewById(R.id.amount);

        String newAmountStr = newAmountView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        // validation
        if(newAmountStr.equals("")) {

            text = "A new amount must be specified, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            int newAmount = Integer.parseInt(newAmountStr);

            if(newAmount > (remaining + depositAmount)) {   // calculates what the remaining would be minus this item

                text = "Amount exceeds remaining budget for that item, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                String parts[] = depositName.split(": ");
                String trimmedDepositName = parts[1];

                myDb.updateExpense(itemName, depositName, depositName, depositDate, newAmount);
                myDb.adjustDeposit(trimmedDepositName, itemName, depositDate, newAmount);

                text = "Deposit has been adjusted";
                Toast.makeText(context, text, duration).show();

                finish();

            }

        }

    }

    public void onDeleteExpenseClick(View v) {


    }
}
