package org.imemorize.activity;


/**
 * Created by briankurzius on 1/12/14.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.imemorize.ImemorizeApplication;
import org.imemorize.R;
import org.imemorize.fragments.SearchTermDialogFragment;
import org.imemorize.model.Consts;
import org.imemorize.model.Quote;
import org.imemorize.android.utils.UploadManager;
import org.imemorize.android.utils.Utils;

import java.util.ArrayList;

public class BaseActivity extends FragmentActivity implements UploadManager.LoadListener{
    private final static String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showAddQuoteActivity(){
        Log.d(TAG, "addQuote()");
        Intent intent = new Intent(this, AddQuoteActivity.class);
        this.startActivity(intent);
    }

    protected void showCategoryListActivity(){
        Intent listIntent = new Intent(this,CategoryListActivity.class);
        this.startActivity(listIntent);
    }

    protected void showMemorizeView(){
        Intent memorizeIntent = new Intent(this, MemorizeActivity.class);
        this.startActivity(memorizeIntent);
    }

    protected void shareQuote(Quote currentQuote){
        if(Utils.isUserQuote(currentQuote)){
            Log.d(TAG, "shareQuote() : the shareQuote needs to upload to the server");
            // check if there is an internet connection
            // upload to the server and wait for the response to do the share
            UploadManager um = new UploadManager(this);
            um.uploadQuote(currentQuote);
        }else{
            Log.d(TAG, "shareQuote() : the shareQuote just sends out as is");
            createShare(currentQuote, currentQuote.getQuoteId(),false);
        }

    }

    // the callback that gives us the ID for the newly added quote
    public void onQuoteUploadComplete(String id){
        Log.d(TAG, "BaseActivity : onQuoteUploadComplete(): the load completed: id: *" + id + "*");
        Quote q = ((ImemorizeApplication)getApplication()).getCurrentQuote();
        createShare(q, id, true);
    }

    // the callback that gives us the ID for the newly added quote
    public void onQuoteUploadError(Exception e){
        Log.d(TAG, "BaseActivity : onQuoteUploadError(): the load had an error: ");
        //go ahead and share it without the URL
        Quote q = ((ImemorizeApplication)getApplication()).getCurrentQuote();
        createShare(q, null, true);

    }

    /**
     * use whichever ID is sent since in some cases it might be the quote ID but in others it is the server generated one
     * if ID is null then we send it without the url
     */

    //
    private void createShare(Quote quote, String id, boolean userQuote){
        String formattedQuote = "'" + Utils.cleanupTextForSharing(quote.getText())  + "' \n --" + Utils.cleanupTextForSharing(quote.getAuthor());
        if(!Utils.cleanupTextForSharing(quote.getReference()).isEmpty()){
            formattedQuote = formattedQuote + " | " + Utils.cleanupTextForSharing(quote.getReference());
        }
        String shareString = String.format(getResources().getString(R.string.share_body), formattedQuote);
        if(id != null){
            if(userQuote){
                shareString = shareString + String.format(getResources().getString(R.string.share_user_url), id);
            }else{
                shareString = shareString + String.format(getResources().getString(R.string.share_url), id);
            }
        }
        shareString = shareString + getString(R.string.share_download_url);

        Utils.shareContent(this, getResources().getString(R.string.share_title), shareString);

        ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_SHARE,Utils.getQuoteTextForTracking(quote));

    }

    public void doSearch(String searchTerm){
        ArrayList<Quote> quoteList = ((ImemorizeApplication)getApplication()).getQuoteSetBySearch(searchTerm);

        //Toast.makeText(this," doSearch the quotesize is : " + quoteList.size(),Toast.LENGTH_LONG).show();
        if(quoteList.size()>0){
            Intent quoteListIntent = new Intent(this,QuoteListActivity.class);
            quoteListIntent.putExtra(CategoryListActivity.CONST_CATID,CategoryListActivity.CONST_USER_SEARCH_ID);
            quoteListIntent.putExtra(CategoryListActivity.CONST_CAT_NAME, "search results");
            quoteListIntent.putExtra(CategoryListActivity.KEY_SEARCH, searchTerm);

            this.startActivity(quoteListIntent);
        }else{
            Toast.makeText(this, "Your search for '" + searchTerm + "' returned no results", Toast.LENGTH_LONG).show();
        }

        ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_SEARCH,searchTerm);
    }

    protected void showSearch(){
        SearchTermDialogFragment dialog = new SearchTermDialogFragment();
       // dialog.show(this.getFragmentManager(),"Search");
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


