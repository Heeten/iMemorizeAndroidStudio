package org.imemorize.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.imemorize.activity.BaseActivity;
import org.imemorize.R;

//import android.app.DialogFragment;

/**
 * Created by briankurzius on 6/8/13.
 */
public class SearchTermDialogFragment extends DialogFragment {

    private EditText etSearchTerm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.search_entry, null);
        etSearchTerm = (EditText)v.findViewById(R.id.search_term);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setIcon(R.drawable.ic_action_search)
                .setTitle(R.string.search_title)
               // Set the action buttons
               .setPositiveButton(R.string.search_btn_search, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       BaseActivity ma = (BaseActivity) getActivity();
                       if (!etSearchTerm.getText().toString().isEmpty()) {
                           ma.doSearch(etSearchTerm.getText().toString());
                       } else {
                           Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.search_error_no_text), Toast.LENGTH_SHORT).show();
                       }
                   }
               })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });

        return builder.create();
    }
}
