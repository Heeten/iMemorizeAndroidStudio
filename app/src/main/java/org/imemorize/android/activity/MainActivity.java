package org.imemorize.android.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.imemorize.android.model.Consts;
import org.imemorize.android.ImemorizeApplication;
import org.imemorize.android.R;

public class MainActivity extends BaseActivity {
    private final static String TAG = "MainActivity";
    private ImageButton mBtnChooseQuote;
    private ImageButton mBtnAddQuote;
    private ImageButton mBtnSearchQuotes;

    private ImemorizeApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (ImemorizeApplication)this.getApplication();

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

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_HOME);
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
