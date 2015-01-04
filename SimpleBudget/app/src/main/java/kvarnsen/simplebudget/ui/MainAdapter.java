package kvarnsen.simplebudget.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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
        curCard.setClickable(true);

        TextView curTitle = (TextView) curCard.findViewById(R.id.title);
        TextView curContent = (TextView) curCard.findViewById(R.id.content);

        cur = (LineItem) mDataset.get(position);

        curTitle.setText(cur.name);
        curContent.setText(
                "Budgeted: $" + Integer.toString(cur.budgeted) + ".00\nSpent: $" + Integer.toString(cur.spent) + ".00\n"
                        + "Remaining: $" + Integer.toString(cur.remaining) + ".00\n"
        );

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}