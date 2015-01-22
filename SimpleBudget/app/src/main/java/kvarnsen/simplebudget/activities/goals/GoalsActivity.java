package kvarnsen.simplebudget.activities.goals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.activities.MainActivity;
import kvarnsen.simplebudget.adapters.GoalAdapter;
import kvarnsen.simplebudget.database.DBHelper;


public class GoalsActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Goals");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

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

        findViewById(R.id.add_goal_button).setOnLongClickListener(listener);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer, R.string.main);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = DBHelper.getInstance(this);

        initGoals();
    }

    public void onResume() {
        super.onResume();

        initGoals();
    }

    public void initGoals() {

        db.checkGoalTableIsDefined();

        ArrayList goals = db.getAllGoals();
        TextView placeholder = (TextView) findViewById(R.id.goal_placeholder);

        if(goals.size() > 0) {
            placeholder.setVisibility(View.GONE);
            mAdapter = new GoalAdapter(goals);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            placeholder.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(null);
        }

    }

    public void onItemClick(View v) {

        RelativeLayout holder = (RelativeLayout) v;

        Intent intent = new Intent(this, GoalHistoryActivity.class);
        intent.putExtra("GOAL_NAME", ((TextView) holder.findViewById(R.id.item_name)).getText().toString());
        startActivity(intent);

    }

    public void onAddClick(View v) {

        Intent intent = new Intent(this, AddGoalActivity.class);
        startActivity(intent);

    }

    public void onBudgetClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void clearGoals() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                GoalsActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.deleteGoals();

                Context context = getApplicationContext();
                CharSequence text = "Goals deleted";
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();

                initGoals();

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete your goals?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            clearGoals();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
