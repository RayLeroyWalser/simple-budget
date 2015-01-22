package kvarnsen.simplebudget.activities.items;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.activities.goals.AdjustDepositActivity;
import kvarnsen.simplebudget.adapters.ItemHistoryAdapter;
import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;

/*
    Displays expense history and overview for a specific line item.
 */

public class ItemHistoryActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;

    private LineItem myItem;
    private int itemSpent;
    private DBHelper myDb;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnLongClickListener listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(1000);

                Toast toast = Toast.makeText(getApplicationContext(), v.getContentDescription(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();
                return true;
            }
        };

        findViewById(R.id.add_expense_button).setOnLongClickListener(listener);
        findViewById(R.id.make_deposit_button).setOnLongClickListener(listener);

        mRecyclerView = (RecyclerView) findViewById(R.id.item_history_recycler);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDb = DBHelper.getInstance(this);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            name = b.getString("ITEM_NAME");
            getSupportActionBar().setTitle("Item: " + name);

            myItem = myDb.getLineItem(name);

            if(myItem != null) {
                setOverview();
                setHistory(name);
            }
        }
        else {
            getSupportActionBar().setTitle("Item Name Not Found");

            Context context = getApplicationContext();
            CharSequence text = "Item name was not provided";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();

            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setOverview();
        setHistory(name);
    }

    /*
        Sets overview card
     */
    public void setOverview() {

        myItem = myDb.getLineItem(name);
        itemSpent = myDb.getItemSpent(name);

        TextView budgeted = (TextView) findViewById(R.id.budgeted);
        TextView spent = (TextView) findViewById(R.id.spent);
        TextView remaining = (TextView) findViewById(R.id.remaining);

        budgeted.setText("Budgeted: $" + Integer.toString(myItem.getBudget()) + ".00");
        spent.setText("Spent: $" + Integer.toString(itemSpent) + ".00");
        remaining.setText("Remaining: $" + Integer.toString(myItem.getBudget() - itemSpent) + ".00");

    }

    /*
        Set main content - RecyclerView of expenses
     */
    public void setHistory(String name) {

        TextView history = (TextView) findViewById(R.id.item_history_placeholder);

        ArrayList myHistory = myDb.getExpenseHistory(name);

        if(myHistory.size() != 0) {
            history.setVisibility(View.GONE);

            RecyclerView.Adapter mAdapter = new ItemHistoryAdapter(myHistory);
            mRecyclerView.setAdapter(mAdapter);
        } else {

            history.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(null);

        }



    }

    public void onAddExpenseClick(View v) {

        Intent intent = new Intent(this, AddExpenseActivity.class);
        intent.putExtra("ITEM_NAME", name);
        startActivity(intent);

    }

    public void onMakeDepositClick(View v) {

        Intent intent = new Intent(this, MakeDepositActivity.class);
        intent.putExtra("ITEM_NAME", name);
        startActivity(intent);

    }

    public void onOverviewClick(View v) {

        int REQUEST_ITEM_ADJUSTMENT = 0;

        Intent intent = new Intent(this, AdjustItemActivity.class);
        intent.putExtra("ITEM_NAME", name);
        intent.putExtra("ITEM_BUDGET", myItem.getBudget());
        intent.putExtra("ITEM_SPENT", itemSpent);
        startActivityForResult(intent, REQUEST_ITEM_ADJUSTMENT);

    }

    public void onHistoryClick(View v) {

        TableLayout table = (TableLayout) v;

        String expName = ((TextView) table.findViewById(R.id.history_name)).getText().toString();
        String expDate = ((TextView) table.findViewById(R.id.history_date)).getText().toString();
        int expAmount = trimExpenseAmount(((TextView) table.findViewById(R.id.history_amount)).getText().toString());

        if(expName.contains("Deposit")) {

            Intent intent = new Intent(this, AdjustDepositActivity.class);
            intent.putExtra("DEPOSIT_NAME", expName);
            intent.putExtra("DEPOSIT_DATE", expDate);
            intent.putExtra("DEPOSIT_AMOUNT", expAmount);
            intent.putExtra("ITEM_NAME", name);
            intent.putExtra("ITEM_REMAINING", (myItem.getBudget() - itemSpent));
            startActivity(intent);

        } else {

            Intent intent = new Intent(this, AdjustExpenseActivity.class);
            intent.putExtra("EXPENSE_NAME", expName);
            intent.putExtra("EXPENSE_DATE", expDate);
            intent.putExtra("EXPENSE_AMOUNT", expAmount);
            intent.putExtra("ITEM_NAME", name);
            intent.putExtra("ITEM_REMAINING", (myItem.getBudget() - itemSpent));
            startActivity(intent);

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        name = data.getExtras().getString("ITEM_NAME");
        getSupportActionBar().setTitle("Item Name: " + name);

    }


    public int trimExpenseAmount(String str) {

        String noDollar = str.substring(1);
        String[] noDecimals = noDollar.split("\\.");

        int trimmed = Integer.parseInt(noDecimals[0]);

        return trimmed;

    }

}
