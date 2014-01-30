package org.imemorize.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.imemorize.ImemorizeApplication;
import org.imemorize.activity.MemorizeActivity;
import org.imemorize.R;

/**
 * Created by briankurzius on 6/8/13.
 */
public class FontSizeDialogFragment extends DialogFragment {
    private static final String TAG = "FontSizeDialog";
    private int selectedFontItem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_font_size)
//                .setItems(R.array.font_size_array, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                        Log.d(TAG, "the font size was chosen");
//                        MemorizeActivity ma = (MemorizeActivity)getActivity();
//                        String[] mArray = getActivity().getResources().getStringArray(R.array.font_size_array);
//                        int fontSize = Integer.parseInt(mArray[which].replace(" pt.", ""));
//                        ma.changeFontSize(fontSize);
//                    }
//                })

                .setSingleChoiceItems(R.array.font_size_array, ImemorizeApplication.getSharedPrefInt(ImemorizeApplication.PREFS_FONT_SIZE,30),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                selectedFontItem = which;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "the font size was chosen");
                        MemorizeActivity ma = (MemorizeActivity)getActivity();
//                        String[] mArray = getActivity().getResources().getStringArray(R.array.font_size_array);
//                        int fontSize = Integer.parseInt(mArray[selectedFontItem].replace(" pt.", ""));
                        ma.changeFontSizeByValue(selectedFontItem);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });;
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
