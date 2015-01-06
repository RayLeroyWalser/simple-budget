package kvarnsen.simplebudget;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import kvarnsen.simplebudget.database.DBHelper;

/*
    Dialog-like activity that handles adjustments to name and amount of line item
 */

public class AdjustItemActivity extends ActionBarActivity {

    private String itemName;
    private int itemSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_item);

        Bundle b = getIntent().getExtras();
        itemName = b.getString("ITEM_NAME");
        itemSpent = b.getInt("ITEM_SPENT");

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

        String newName = newNameView.getText().toString();
        int newBudget = Integer.parseInt(newBudgetView.getText().toString());

        DBHelper myDb = DBHelper.getInstance(this);

        myDb.updateItem(itemName, newName, newBudget, itemSpent);

        setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
        finish();

    }


}
