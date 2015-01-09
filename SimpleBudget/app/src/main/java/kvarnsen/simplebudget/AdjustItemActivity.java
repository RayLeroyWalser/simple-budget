package kvarnsen.simplebudget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kvarnsen.simplebudget.database.DBHelper;

/*
    Dialog-like activity that handles adjustments to name and amount of line item
 */

public class AdjustItemActivity extends ActionBarActivity {

    private String itemName;
    private int itemBudget;
    private int itemSpent;

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_item);

        myDb = DBHelper.getInstance(this);

        Bundle b = getIntent().getExtras();
        itemName = b.getString("ITEM_NAME");
        itemBudget = b.getInt("ITEM_BUDGET");
        itemSpent = b.getInt("ITEM_SPENT");

        Log.w("AdjustItem", "Spent: " + Integer.toString(itemSpent));

        Toolbar toolbar = (Toolbar) findViewById(R.id.adjust_item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Adjust Item: " + itemName);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", itemName));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", itemName));
        finish();
    }


    public void onSubmitClick(View v) {

        EditText newNameView = (EditText) findViewById(R.id.name);
        EditText newBudgetView = (EditText) findViewById(R.id.amount);

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        String newName = newNameView.getText().toString();
        String newBudgetStr = newBudgetView.getText().toString();
        int newBudget;

        if(myDb.checkNameExists(newName)) {

            text = "An item with that name already exists, please try again";
            Toast.makeText(context, text, duration).show();

        } else if (newName.equals("") && newBudgetStr.equals("")) {    // no name or amount

            text = "A name or amount must be specified!";
            Toast.makeText(context, text, duration).show();

        } else if(newName.equals("") && !newBudgetStr.equals("")) { // amount but no name
            newName = itemName;
            newBudget = Integer.parseInt(newBudgetStr);

            if(newBudget < itemSpent) {

                text = "New item budget exceeds amount already spent, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.updateItem(trimString(itemName), itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();

            }

        } else if(!newName.equals("") && newBudgetStr.equals("")) { // name but no amount
            newBudget = itemBudget;

            if(!Character.isLetter(newName.charAt(0))) {

                text = "Name must begin with a letter, please try again";
                Toast.makeText(context, text, duration).show();

            } else {
                myDb.updateItem(trimString(itemName), itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();
            }

        } else {
            newBudget = Integer.parseInt(newBudgetStr);

            if(newBudget < itemSpent) {

                text = "New item budget exceeds amount already spent, please try again";
                Toast.makeText(context, text, duration).show();

            } else if(!Character.isLetter(newName.charAt(0))) {

                text = "Name must begin with a letter, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.updateItem(trimString(itemName), itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();

            }
        }

    }

    public void onDeleteItemClick(View v) {

        final Intent intent = new Intent(this, MainActivity.class);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AdjustItemActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Context context = getApplicationContext();
                CharSequence text = "Expense deleted";
                int duration = Toast.LENGTH_SHORT;

                // delete item from Budget table, and delete Item Table
                myDb.deleteLineItem(itemName, trimString(itemName));

                Toast.makeText(context, text, duration).show();
                // return to main activity
                startActivity(intent);

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();


    }

    public String trimString(String str) {

        String newStr = str.toLowerCase();
        newStr = newStr.replaceAll("\\s+", "");

        return newStr;
    }


}
