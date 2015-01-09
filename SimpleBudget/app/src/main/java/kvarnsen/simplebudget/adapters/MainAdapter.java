package kvarnsen.simplebudget.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kvarnsen.simplebudget.MainActivity;
import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.LineItem;

/**
 * Created by joshuapancho on 4/01/15.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mTextView;
        public ViewHolder(CardView v) {
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

        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LineItem cur;

        CardView curCard = holder.mTextView;
        curCard.setLongClickable(true);

        curCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        TextView curTitle = (TextView) curCard.findViewById(R.id.title);
        TextView curBudget = (TextView) curCard.findViewById(R.id.budgeted);
        TextView curSpent = (TextView) curCard.findViewById(R.id.spent);
        TextView curRemaining = (TextView) curCard.findViewById(R.id.remaining);

        cur = (LineItem) mDataset.get(position);

        curTitle.setText(cur.name);

        curBudget.setText("Budgeted: $" + Integer.toString(cur.budgeted) + ".00");
        curSpent.setText("Spent: $" + Integer.toString(cur.spent) + ".00");
        curRemaining.setText("Remaining: $" + Integer.toString(cur.budgeted - cur.spent) + ".00");

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}