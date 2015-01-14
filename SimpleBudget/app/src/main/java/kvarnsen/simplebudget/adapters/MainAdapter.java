package kvarnsen.simplebudget.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.LineItem;

/**
 * Created by joshuapancho on 4/01/15.
 */

/*
    RecyclerView Adapter that displays line items in MainActivity
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mTextView;
        public ViewHolder(RelativeLayout v) {
            super(v);
            mTextView = v;
        }
    }

    public MainAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LineItem cur = (LineItem) mDataset.get(position);
        RelativeLayout curItem = holder.mTextView;

        double progress = (cur.spent/(double)cur.budgeted) * 100;

        TextView curTitle = (TextView) curItem.findViewById(R.id.item_name);
        TextView curProgText = (TextView) curItem.findViewById(R.id.prog_text);
        ProgressBar curProgBar = (ProgressBar) curItem.findViewById(R.id.prog_bar);

        curTitle.setText(cur.name);
        curProgText.setText("$" + Integer.toString(cur.spent) + ".00/$" + Integer.toString(cur.budgeted) + ".00");
        curProgBar.setProgress((int) progress);

        /*
        CardView curCard = holder.mTextView;

        TextView curTitle = (TextView) curCard.findViewById(R.id.title);
        TextView curBudget = (TextView) curCard.findViewById(R.id.budgeted);
        TextView curSpent = (TextView) curCard.findViewById(R.id.spent);
        TextView curRemaining = (TextView) curCard.findViewById(R.id.remaining);

        cur = (LineItem) mDataset.get(position);

        curTitle.setText(cur.name);
        curBudget.setText("Budgeted: $" + Integer.toString(cur.budgeted) + ".00");
        curSpent.setText("Spent: $" + Integer.toString(cur.spent) + ".00");
        curRemaining.setText("Remaining: $" + Integer.toString(cur.budgeted - cur.spent) + ".00");
        */

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}