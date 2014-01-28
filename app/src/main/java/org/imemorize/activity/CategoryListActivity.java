package org.imemorize.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.imemorize.ImemorizeApplication;
import org.imemorize.model.Category;
import org.imemorize.model.Consts;
import org.imemorize.model.Quote;
import org.imemorize.android.utils.Utils;
import org.imemorize.R;

import java.util.ArrayList;


public class CategoryListActivity extends BaseListActivity {
    public final static String CONST_USER_QUOTES = "My Quotes";
    public final static String CONST_USER_FAVORITES = "Favorites";
    public final static String CONST_USER_MEMORIZED = "Memorized";

    public final static String KEY_SEARCH = "key_search";
    public final static int CONST_USER_QUOTES_ID = -1;
    public final static int CONST_USER_FAVORITES_ID = -2;
    public final static int CONST_USER_MEMORIZED_ID = -3;
    public final static int CONST_USER_SEARCH_ID = -4;
    public final static String CONST_CATID = "catID";
    public final static String CONST_CAT_NAME = "catName";
    private final static String TAG = "ListActivity";
    private String catName;
    private ArrayList<Category> catList = null;
    private Context mContext = null;
    private TextView mHeader;
    private int catID = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // default catID to 0 == which means it will get all categories with a catParent of 0

        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_list_activity);
        mHeader = (TextView)findViewById(R.id.list_header);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Log.d(TAG,"there is a bundle");
            catID = bundle.getInt(CONST_CATID);
            catName = bundle.getString(CONST_CAT_NAME);
            Log.d(TAG,"catName is:" + catName);
        }else{
            Log.d(TAG,"there is no bundle");
        }
        Log.d(TAG,"the catID is:" + catID);
        catList = (ArrayList<Category>)app.getCategoryChildren(catID);
        // for main list add the user quotes, favorites and memorized option
        if(catID==0){

            getActionBar().setTitle(R.string.title_choose_category);

            Category category = new Category();
            category.catName = getString(R.string.category_label_favorites);
            category.id = CONST_USER_FAVORITES_ID;
            catList.add(category);

            category = new Category();
            category.catName = getString(R.string.category_label_memorized);
            category.id = CONST_USER_MEMORIZED_ID;
            catList.add(category);

            category = new Category();
            category.catName = getString(R.string.category_label_my_quotes);
            category.id = CONST_USER_QUOTES_ID;
            catList.add(category);
        }

        // hide the header for all
        mHeader.setVisibility(View.GONE);


        //ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<Category>(this,android.R.layout.simple_list_item_1, catList);
        ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<Category>(this, R.layout.quote_list_item, catList);
        getActionBar().setTitle(catName);

        this.setListAdapter(arrayAdapter);

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_CATEGORY);
    }
    

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        outState.putInt("catID", catID);
        super.onSaveInstanceState(outState);
    }

    // -----------------------------------------------------
    // Menu --
    // -----------------------------------------------------
    //manually create the menu so we can reset the favorites icon when the user toggles it
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
        Log.d(TAG, "the ((Category)catList.get(position)): " + (Category)catList.get(position));
        Category selectedCat = catList.get(position);

        // if its the last item in the list then get user quotes
/*        if(selectedCat.catName.equalsIgnoreCase(CONST_USER_QUOTES)){
            Intent quoteListIntent = new Intent(mContext,QuoteListActivity.class);
            mContext.startActivity(quoteListIntent);
            return;
        }*/
        if(selectedCat!=null){
            Log.d(TAG, "the selectedCat is: " + selectedCat);
            Log.d(TAG, "the selectedID is: " + selectedCat.id);

            // if the catID is less than 0 then it is a specialized category
            // so check to see if any of the items exist and execute
            // otherwise return
            if(selectedCat.id<0){
                switch (selectedCat.id){
                    case  CONST_USER_QUOTES_ID:
                        if(app.getNumberOfUserQuotes()<1){
                            Toast.makeText(this,getString(R.string.prompt_no_user_quotes), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case  CONST_USER_FAVORITES_ID:
                        if(app.getNumberOfFavorites()<1){
                            Toast.makeText(this,getString(R.string.prompt_no_favorites), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case  CONST_USER_MEMORIZED_ID:
                        if(app.getNumberOfMemorized()<1){
                            Toast.makeText(this,getString(R.string.prompt_no_memorized), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
                Utils.logger(TAG,"now prep the intent");
                Intent quoteListIntent = new Intent(mContext,QuoteListActivity.class);
                quoteListIntent.putExtra(CONST_CATID, selectedCat.id);
                quoteListIntent.putExtra(CONST_CAT_NAME, selectedCat.catName);
                mContext.startActivity(quoteListIntent);
                return;
            }

            ArrayList<Category>catList = (ArrayList<Category>)app.getCategoryChildren(selectedCat.id);
            // if there are no child categories we must have quotes instead
            if(catList.size()<1){
                Log.d(TAG, "the categoryrelations are less than 1");
                // now check if this category has quotes
                ArrayList<Quote> quoteList = (ArrayList<Quote>) app.getQuoteSet(selectedCat.id);
                // but if there are no quotes then show a toast - this should NEVER happen
                if(quoteList!=null){
                    Log.d(TAG, "the quoteList.size() is " + quoteList.size());
                    // if we have quotes open the quoteListActivity
                    if(quoteList.size()>0){
                        Intent quoteListIntent = new Intent(mContext,QuoteListActivity.class);
                        quoteListIntent.putExtra(CONST_CATID, selectedCat.id);
                        quoteListIntent.putExtra(CONST_CAT_NAME, selectedCat.catName);
                        mContext.startActivity(quoteListIntent);
                    }
                }else{
                    Log.i(TAG,"there are no quotes in this list item");
                    Toast.makeText(this,"no quotes",Toast.LENGTH_SHORT).show();
                }
            }else{
                // We have more categories to show
                Intent catIntent = new Intent(mContext,CategoryListActivity.class);
                catIntent.putExtra(CONST_CATID, selectedCat.id);
                catIntent.putExtra(CONST_CAT_NAME, selectedCat.catName);
                mContext.startActivity(catIntent);
            }

            ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_SELECT_CATEGORY,selectedCat.catName);
        }else{
            Log.d(TAG, "the selectedCat item is NULL ");
        }
    }



}