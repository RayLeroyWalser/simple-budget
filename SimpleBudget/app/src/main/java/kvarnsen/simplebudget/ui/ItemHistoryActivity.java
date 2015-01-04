package kvarnsen.simplebudget.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.LineItem;
import kvarnsen.simplebudget.database.DBHelper;

public class ItemHistoryActivity extends ActionBarActivity {

    private LineItem myItem;
    private TextView overview, history;

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

        DBHelper myDb = DBHelper.getInstance(this);

        if(b != null) {
            String name = b.getString("ITEM_NAME");
            getSupportActionBar().setTitle("Item Name: " + name);

            myItem = myDb.getLineItem(name);

            if(myItem != null) {
                setOverview();
                setHistory();
            }
        }
        else {
            getSupportActionBar().setTitle("Item Name Not Found");

            // some toast

            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_history, menu);
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

    public void setOverview() {

        overview = (TextView) findViewById(R.id.item_content);
        overview.setText("Budgeted: $" + myItem.budgeted + ".00\nSpent: $" + myItem.spent + ".00\nRemaining: $" + myItem.remaining + ".00\n\n");

    }

    public void setHistory() {

        history = (TextView) findViewById(R.id.item_history_content);

        // set history here

    }

}
