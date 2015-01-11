package kvarnsen.simplebudget.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import kvarnsen.simplebudget.R;

/**
 * Created by joshuapancho on 3/01/15.
 */

/*
    Dialog fragment to prompt user for a budget if one does not already exist
 */
public class BudgetDialogFragment extends DialogFragment {

    public interface BudgetDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    private int budget;
    BudgetDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (BudgetDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BudgetDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Set budget limit");
        builder.setView(inflater.inflate(R.layout.dialog_budget, null))
                // Add action buttons
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Dialog myDialog = (Dialog) dialog;
                        EditText myEditText = (EditText) myDialog.findViewById(R.id.budgetDialog);

                        budget = Integer.parseInt(myEditText.getText().toString());

                        mListener.onDialogPositiveClick(BudgetDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public int getBudget() {

        return budget;

    }


}
