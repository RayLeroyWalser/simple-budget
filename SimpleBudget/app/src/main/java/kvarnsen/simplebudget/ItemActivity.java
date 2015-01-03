package kvarnsen.simplebudget;

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

import kvarnsen.simplebudget.database.DBHelper;


public class ItemActivity extends ActionBarActivity {

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.item_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple Budget");

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

        String name = nameView.getText().toString();
        String amountStr = amountView.getText().toString();

        if(amountStr.equals("") || name.equals("")) {

            Context context = getApplicationContext();
            CharSequence text = "Invalid input, please try again!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        } else {

            myDb.insertLineItem(name, Integer.parseInt(amountStr), 0);
            finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
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
}
