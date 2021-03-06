package org.imemorize.android.utils;

/**
 * Code thanks to:
 * http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
 *
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.imemorize.ImemorizeApplication;
import org.imemorize.model.Consts;

public class AppRater {
    private final static String TAG = "AppRater";
    private final static String APP_TITLE = "iMemorize";
    private final static String APP_PNAME = "org.imemorize";

    private final static int DAYS_UNTIL_PROMPT = 2;
    private final static int LAUNCHES_UNTIL_PROMPT = 4;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

/*
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(20,20,20,0);

        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setText("Rate " + APP_TITLE);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // dont show it again since the user accepted it
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
                ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_OK);
            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText("Remind me later");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // reset launch count
                if (editor != null) {
                    editor.putLong("launch_count", 0);
                    editor.commit();
                }
                dialog.dismiss();
                ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_LATER);
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText("No, thanks");
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
                ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_NO_THANKS);
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();

        ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_SHOWED);
    }
*/

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor){
        Utils.logger(TAG, "showUpdateDialog()1");
        final String DIALOG_OK = "OK!";
        final String DIALOG_NOT_NOW = "Remind me later";
        final String DIALOG_DONT_REMIND_ME = "No thanks";
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Rate iMemorize")
                .setMessage( "If you enjoy using iMemorize, please take a moment to rate it. Thanks for your support!")
                .setCancelable(false)
                .setPositiveButton(DIALOG_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.logger(TAG, DIALOG_OK);
                        // dont show it again since the user accepted it
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                        dialog.dismiss();
                        ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_OK);

                    }
                })
                .setNeutralButton(DIALOG_NOT_NOW, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // reset launch count
                        if (editor != null) {
                            editor.putLong("launch_count", 0);
                            editor.commit();
                        }
                        dialog.dismiss();
                        ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_LATER);

                    }
                })
                .setNegativeButton(DIALOG_DONT_REMIND_ME, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                        ((ImemorizeApplication)mContext.getApplicationContext()).trackEvent(Consts.TRACK_RATE_REMINDER,Consts.TRACK_RATE_REMINDER_NO_THANKS);

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}