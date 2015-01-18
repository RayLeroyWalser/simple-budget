package kvarnsen.simplebudget;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        ArrayList goals = db.getAllGoals();

        if(goals.size() > 0) {
            TextView placeholder = (TextView) findViewById(R.id.goal_placeholder);
            placeholder.setVisibility(View.GONE);
            mAdapter = new GoalAdapter(goals);
            mRecyclerView.setAdapter(mAdapter);
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

    public void clearGoals(View v) {


    }

}
