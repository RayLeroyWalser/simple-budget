package kvarnsen.simplebudget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kvarnsen.simplebudget.R;
import kvarnsen.simplebudget.containers.Deposit;
import kvarnsen.simplebudget.containers.Expense;

/**
 * Created by joshuapancho on 18/01/15.
 */

public class DepositHistoryAdapter extends RecyclerView.Adapter<DepositHistoryAdapter.ViewHolder> {
    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TableLayout mTable;
        public ViewHolder(TableLayout v) {
            super(v);
            mTable = v;
        }
    }

    public DepositHistoryAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public DepositHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        TableLayout v = (TableLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_table, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Deposit cur;
        TableLayout curTable = holder.mTable;

        TextView historyDate = (TextView) curTable.findViewById(R.id.history_date);
        TextView historyName = (TextView) curTable.findViewById(R.id.history_name);
        TextView historyAmount = (TextView) curTable.findViewById(R.id.history_amount);

        cur = (Deposit) mDataset.get(position);

        historyDate.setText(cur.getDate());
        historyName.setText(cur.getName());
        historyAmount.setText("$" + Integer.toString(cur.getAmount()) + ".00");

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}