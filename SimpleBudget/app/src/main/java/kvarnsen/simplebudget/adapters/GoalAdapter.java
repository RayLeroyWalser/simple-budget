package kvarnsen.simplebudget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.Expense;
import kvarnsen.simplebudget.containers.Goal;

/**
 * Created by joshuapancho on 5/01/15.
 */

/*
    RecyclerView Adapter that lists savings goals
 */
public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {
    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mLayout;
        public ViewHolder(RelativeLayout v) {
            super(v);
            mLayout = v;
        }
    }

    public GoalAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public GoalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Goal cur = (Goal) mDataset.get(position);
        RelativeLayout curGoal = holder.mLayout;

        double progress = (cur.getDeposited()/(double)cur.getGoal()) * 100;

        TextView curTitle = (TextView) curGoal.findViewById(R.id.item_name);
        TextView curProgText = (TextView) curGoal.findViewById(R.id.prog_text);
        ProgressBar curProgBar = (ProgressBar) curGoal.findViewById(R.id.prog_bar);

        curTitle.setText(cur.getName());
        curProgText.setText("$" + Integer.toString(cur.getDeposited()) + ".00/$" + Integer.toString(cur.getGoal()) + ".00");
        curProgBar.setProgress((int) progress);

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}