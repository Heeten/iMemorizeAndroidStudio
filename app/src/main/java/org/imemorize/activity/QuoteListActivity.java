package org.imemorize.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.imemorize.ImemorizeApplication;
import org.imemorize.model.Consts;
import org.imemorize.model.Quote;
import org.imemorize.android.utils.Utils;
import org.imemorize.R;

import java.util.ArrayList;

public class QuoteListActivity extends BaseListActivity  {
    private final static String TAG = "QuoteListActivity";
    public final static String CONST_USERQUOTES = "userQuotes";
    public final static String CONST_QUOTEPOSITION = "quotePosition";



    private boolean userQuotes = false;
    private int catID = 0 ;
    private String catName = "";
    private QuoteListAdapter mQuoteListAdapter;
    private TextView headerText;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.quote_list_activity);
        bundle = getIntent().getExtras();
        if(bundle!=null){
            Log.d(TAG,"there is a bundle");
            catID = bundle.getInt(CategoryListActivity.CONST_CATID);
            catName = bundle.getString(CategoryListActivity.CONST_CAT_NAME);
            getActionBar().setTitle(catName);

        }
        headerText = (TextView) findViewById(R.id.list_header);

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_QUOTES);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(catID<0){
            switch (catID){
                case  CategoryListActivity.CONST_USER_QUOTES_ID:
                    quoteList = app.getUserQuoteSet();
                    break;
                case  CategoryListActivity.CONST_USER_FAVORITES_ID:
                    quoteList = app.getFavoriteQuoteSet();
                    break;
                case  CategoryListActivity.CONST_USER_MEMORIZED_ID:
                    quoteList = app.getMemorizedQuoteSet();
                    break;
                case CategoryListActivity.CONST_USER_SEARCH_ID:
                    quoteList = app.getCurrentQuoteSet();
                    String searchResultsString = String.format(getString(R.string.search_results_string), bundle.getString(CategoryListActivity.KEY_SEARCH) , quoteList.size()+"");
                    headerText.setText(searchResultsString);
                    break;
            }
        }else{
            quoteList = app.getQuoteSet(catID);
        }
        ArrayAdapter<Quote> arrayAdapter = new ArrayAdapter<Quote>(this, R.layout.quote_list_item, quoteList);
        //this.setListAdapter(arrayAdapter);
        if(quoteList!=null){
            mQuoteListAdapter = new QuoteListAdapter(this, R.layout.item_quotelist_row,quoteList);
            this.setListAdapter(mQuoteListAdapter);
        }else{
           quoteList = new ArrayList<Quote>();
           mQuoteListAdapter = new QuoteListAdapter(this, R.layout.item_quotelist_row,quoteList);
           this.setListAdapter(mQuoteListAdapter);
           Toast.makeText(this,"There are no more quotes in this collection", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
           // finish();
        }else if (itemId == R.id.menu_feedback){
            sendFeedback();
        }
        return true;
    }
    
    @Override
    //@SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "the position of this item is: " + position);
        Log.d(TAG, "the ((Quote)catList.get(position)): " + (Quote) quoteList.get(position));
        Quote selectedQuote = (Quote)quoteList.get(position);
        if(selectedQuote!=null){
            Log.d(TAG, "the selected is: " + selectedQuote);
            app.setCurrentQuoteSetIndex(position);
            Intent memorizeIntent = new Intent(this, MemorizeActivity.class);
            memorizeIntent.putExtra(CONST_QUOTEPOSITION,position);
            if(userQuotes){
                memorizeIntent.putExtra(CONST_USERQUOTES,true);
            }
            this.startActivity(memorizeIntent);
            
        }else{
            Log.d(TAG, "the selectedQuote item is NULL ");
        }

    }

    class QuoteListAdapter extends ArrayAdapter<Quote>{
        int resource;
        ArrayList<Quote> items;

        QuoteListAdapter(Context context, int _resource, ArrayList<Quote> _items){
            super(context, _resource, _items);
            resource = _resource;
            items = _items;
        }

        @Override
        public View getView(int position,View convertView, ViewGroup parent){
            LinearLayout newView;
            if(convertView==null){
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater li;
                li = (LayoutInflater)getContext().getSystemService(inflater);
                li.inflate(resource, newView, true);

            }else{
                newView = (LinearLayout)convertView;
            }
            Quote thisQuote = items.get(position);
            ImageView memIcon = (ImageView)newView.findViewById(R.id.iv_check);
            ImageView favIcon = (ImageView)newView.findViewById(R.id.iv_heart);
            if(app.isMemorizedQuote(thisQuote)){
                memIcon.setVisibility(View.VISIBLE);
            }else{
                memIcon.setVisibility(View.GONE);
            }
            if(app.isFavoriteQuote(thisQuote)){
                favIcon.setVisibility(View.VISIBLE);
            }else{
                favIcon.setVisibility(View.GONE);
            }
            TextView tv = (TextView)newView.findViewById(R.id.txt_quote);
            tv.setText(Utils.cleanupQuoteListText(thisQuote.getText()));
            return newView;
        }


    }


    
    

}
