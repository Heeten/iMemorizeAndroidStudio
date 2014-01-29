package org.imemorize.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.imemorize.ImemorizeApplication;
import org.imemorize.R;
import org.imemorize.android.utils.AppRater;
import org.imemorize.fragments.AppUpdatedDialogFragment;
import org.imemorize.model.Consts;

public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private ImageButton mBtnChooseQuote;
    private ImageButton mBtnAddQuote;
    private ImageButton mBtnSearchQuotes;

    private ImemorizeApplication app;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (ImemorizeApplication)this.getApplication();
        mContext = this;
        mBtnChooseQuote = (ImageButton)findViewById(R.id.btn_choose_quote);

        mBtnAddQuote = (ImageButton)findViewById(R.id.btn_add_quote);
        mBtnSearchQuotes = (ImageButton)findViewById(R.id.btn_search_quotes);

        mBtnChooseQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryListActivity();
            }
        });

        mBtnAddQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddQuoteActivity();
            }
        });

        mBtnSearchQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearch();
            }
        });

        // turn off the actionbar up button
        getActionBar().setDisplayHomeAsUpEnabled(false);

        // check if the app was updated and open a dialog
        if(app.wasDatabaseUpdated() && ! app.hasUserBeenNotifiedOfAppUpdate){
            AppUpdatedDialogFragment dialog = new AppUpdatedDialogFragment();
            dialog.show(this.getFragmentManager(),"dialog");
            app.hasUserBeenNotifiedOfAppUpdate = true;
        }

        // test code
        // AppRater.showRateDialog(this, null);
        AppRater.app_launched(this);

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_HOME);
    }

    @Override
    public void onResume(){
        super.onResume();
        // check for update
        if(app.updateData!=null){
            // we have the update data from the config
            //do the check for a new version and remind

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // only show search if on the first view

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
       if (itemId == R.id.menu_feedback){
            sendFeedback();
        }
        return true;
    }


}
