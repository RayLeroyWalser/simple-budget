package kvarnsen.simplebudget;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.adapters.ItemHistoryAdapter;
import kvarnsen.simplebudget.containers.ItemHistory;
import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;

public class ItemHistoryActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LineItem myItem;
    private TextView overview, history;
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

        Bundle b = getIntent().getExtras();

        mRecyclerView = (RecyclerView) findViewById(R.id.item_history_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDb = DBHelper.getInstance(this);

        if(b != null) {
            name = b.getString("ITEM_NAME");
            getSupportActionBar().setTitle("Item Name: " + name);

            myItem = myDb.getLineItem(name);

            if(myItem != null) {
                setOverview();
                setHistory(name);
            }
        }
        else {
            getSupportActionBar().setTitle("Item Name Not Found");

            // some toast

            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setOverview();
        setHistory(name);
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

    public void setOverview() {

        myItem = myDb.getLineItem(name);

        overview = (TextView) findViewById(R.id.item_content);
        overview.setText("Budgeted: $" + myItem.budgeted + ".00\nSpent: $" + myItem.spent + ".00\nRemaining: $" + myItem.remaining + ".00\n\n");

    }

    public void setHistory(String name) {

        history = (TextView) findViewById(R.id.item_history_placeholder);
        String content = "";
        ItemHistory cur;

        ArrayList myHistory = myDb.getHistory(name);

        if(myHistory.size() != 0) {
            history.setVisibility(View.GONE);

            mAdapter = new ItemHistoryAdapter(myHistory);
            mRecyclerView.setAdapter(mAdapter);
        }


    }

    public void addExpense(View v) {

        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra("ITEM_NAME", name);
        startActivity(intent);

    }

}
