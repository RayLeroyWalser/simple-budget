package kvarnsen.simplebudget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.Expense;

/**
 * Created by joshuapancho on 5/01/15.
 */

/*
    RecyclerView Adapter that lists expenses for a line item in ItemHistoryActivity
 */
public class ItemHistoryAdapter extends RecyclerView.Adapter<ItemHistoryAdapter.ViewHolder> {
    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TableLayout mTable;
        public ViewHolder(TableLayout v) {
            super(v);
            mTable = v;
        }
    }

    public ItemHistoryAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public ItemHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        TableLayout v = (TableLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_table, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Expense cur;
        TableLayout curTable = holder.mTable;

        TextView historyDate = (TextView) curTable.findViewById(R.id.history_date);
        TextView historyName = (TextView) curTable.findViewById(R.id.history_name);
        TextView historyAmount = (TextView) curTable.findViewById(R.id.history_amount);

        cur = (Expense) mDataset.get(position);

        historyDate.setText(cur.date);
        historyName.setText(cur.name);
        historyAmount.setText("$" + Integer.toString(cur.amount) + ".00");

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}
