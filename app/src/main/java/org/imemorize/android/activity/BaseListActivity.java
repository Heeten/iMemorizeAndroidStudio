package org.imemorize.android.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

import org.imemorize.android.R;
import org.imemorize.android.model.Quote;
import org.imemorize.android.ImemorizeApplication;

import java.util.ArrayList;

/**
 * Created by briankurzius on 1/17/14.
 */
public class BaseListActivity extends ListActivity{

    protected ImemorizeApplication app;
    protected ArrayList<Quote> quoteList = null;
    protected Context mContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        app = (ImemorizeApplication) this.getApplication();
        getActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            //finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }


    // FIXME -- add this to a central place
    public void sendFeedback(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.feedback_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        startActivity(Intent.createChooser(intent, "Send feedback..."));
    }


}
