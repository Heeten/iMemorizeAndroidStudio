package org.imemorize.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import org.imemorize.ImemorizeApplication;
import org.imemorize.R;
import org.imemorize.activity.MemorizeActivity;
import org.imemorize.android.utils.Utils;

/**
 * Created by briankurzius on 6/8/13.
 */
public class FontSizeSliderDialogFragment extends DialogFragment {
    private static final String TAG = "FontSizeDialog";
    private int selectedFontItem = 0;
    private SeekBar mSeekBar;
    private int ratio = 4;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_font_size_picker, null);
        mSeekBar = (SeekBar)v.findViewById(R.id.seekbar_fontsize);
        mSeekBar.setMax(25);
        mSeekBar.setProgress(ImemorizeApplication.getSharedPrefInt(ImemorizeApplication.PREFS_FONT_SIZE,24)/ratio);


                mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // the progress has changed
                        final MemorizeActivity ma = (MemorizeActivity) getActivity();
                        progress = progress * ratio;
                        if (progress > 18) ma.changeFontSizeByValue(progress);
                        Utils.logger(TAG, "the selected size is:" + progress);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        builder.setView(v)
        .setTitle(R.string.choose_font_size)
        // Set the action buttons*/
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "the font size was chosen");
                //
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
