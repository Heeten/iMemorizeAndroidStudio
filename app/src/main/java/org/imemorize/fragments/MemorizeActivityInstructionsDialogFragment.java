package org.imemorize.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.imemorize.R;


/**
 * Created by briankurzius on 6/8/13.
 */
public class MemorizeActivityInstructionsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setTitle(R.string.choose_font_size)
        builder.setMessage(R.string.prompt_memorize_activity_instructions)
                .setTitle(R.string.prompt_memorize_activity_instructions_title)
                        // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
