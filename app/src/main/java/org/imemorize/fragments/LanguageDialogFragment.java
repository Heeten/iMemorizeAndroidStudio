package org.imemorize.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.imemorize.activity.AddQuoteActivity;
import org.imemorize.model.Consts;
import org.imemorize.R;

/**
 * Created by briankurzius on 6/8/13.
 */
public class LanguageDialogFragment extends DialogFragment {
    private static final String TAG = "LanguageDialogFragment";
    private int selectedLanguage = Consts.DEFAULT_LANGUAGE_INDEX;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_language)


                .setSingleChoiceItems(R.array.language_array, selectedLanguage ,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                selectedLanguage = which;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "the language was chosen");
                        AddQuoteActivity act = (AddQuoteActivity) getActivity();
//                        String[] mArray = getActivity().getResources().getStringArray(R.array.font_size_array);
//                        int fontSize = Integer.parseInt(mArray[selectedFontItem].replace(" pt.", ""));
                        act.setQuoteLanguage(getResources().getStringArray(R.array.language_array)[selectedLanguage]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });;
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setLanguage(int language){
        selectedLanguage = language;

    }
}
