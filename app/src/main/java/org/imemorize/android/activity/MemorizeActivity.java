package org.imemorize.android.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.imemorize.android.fragments.FontSizeDialogFragment;
import org.imemorize.android.fragments.MemorizeActivityInstructionsDialogFragment;
import org.imemorize.android.model.Consts;
import org.imemorize.android.model.Quote;
import org.imemorize.android.utils.Utils;
import org.imemorize.android.ImemorizeApplication;
import org.imemorize.android.R;


public class MemorizeActivity extends BaseActivity implements OnClickListener {
    private final static String TAG = "MemorizeActivity";
    private final String HIDDEN_WORDS_ARRAY = "hiddenwordsarray";
    private final int MENU_ITEM_ID_FAVORITES = 1;
    private final int MENU_ITEM_ID_MEMORIZED = 2;
    private final int MENU_ITEM_ID_FONT_SIZE = 3;
    private final int MENU_ITEM_ID_ADD_QUOTE = 4;
    private final int MENU_ITEM_ID_DELETE_QUOTE = 5;
    private final int MENU_ITEM_ID_EDIT_QUOTE = 6;
    private final int MENU_ITEM_ID_SHARE = 7;

    private Button shareButton;
    private WebView memorizeView;
    private Button btnHideWords;
    private Button btnShowAllWords;
    private Button btnLoadQuote;
    private Button btnPrevious;
    private Button btnNext;
    public ImemorizeApplication app;
    private Quote currentQuote  = null;
    final Activity activity = this;
    private ProgressDialog pd;
    private int[] hiddenWordsArray;
    private static final String KEY_INITED_MEMORIZE_ACTIVITY = "inited_memorize";


    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;


    private boolean isUserQuote = false;
    private boolean isQuoteMemorized = false;
    private boolean isQuoteFavorite = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ImemorizeApplication) this.getApplication();
        setContentView(R.layout.activity_memorize);
        getActionBar().setTitle("");
        
        btnHideWords = (Button)findViewById(R.id.btnHideWords);
        btnShowAllWords = (Button)findViewById(R.id.btnShowAllWords);
        btnPrevious = (Button)findViewById(R.id.btnPrevious);
        btnNext = (Button)findViewById(R.id.btnNext);
        memorizeView = (WebView)findViewById(R.id.memorizeView);

        // if the bundle contains an element of HIDDEN_WORDS_ARRAY
        // we want to rebuiild the hidden words
        if(savedInstanceState!=null){
            hiddenWordsArray = savedInstanceState.getIntArray(HIDDEN_WORDS_ARRAY);
        }

        // only show this the first time a user goes to the app
        if(!app.getSharedPrefBoolean(KEY_INITED_MEMORIZE_ACTIVITY, false)){
            // when the user first uses the screen show the instructions
            MemorizeActivityInstructionsDialogFragment dialog = new MemorizeActivityInstructionsDialogFragment();
            dialog.show(this.getFragmentManager(),"dialog");
            app.setSharedPrefBoolean(KEY_INITED_MEMORIZE_ACTIVITY, true);
        }

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_MEMORIZE);

/*        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        memorizeView.setOnTouchListener(gestureListener);
        memorizeView.setOnClickListener(MemorizeActivity.this);*/

    }



    @Override
    protected void onResume(){
        super.onResume();
        memorizeView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
                Log.d(TAG,"webview progress: " + progress);
                if(progress == 100){
                    loadTargetedQuote();
                    pd.dismiss();
                }
            }
        });
        memorizeView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        memorizeView.getSettings().setJavaScriptEnabled(true);
        memorizeView.addJavascriptInterface(new JsObject(), "Android");

        memorizeView.loadUrl("file:///android_asset/index.html");
        setListeners();
        pd = ProgressDialog.show(MemorizeActivity.this,null,"loading",true,false,null);

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        // save the hiddenwordsarray;
        // test commit
        outState.putIntArray(HIDDEN_WORDS_ARRAY, hiddenWordsArray);
        //Log.d(TAG,"onSaveInstanceState()" + hiddenWordsArray.length);
        super.onSaveInstanceState(outState);
    }
    
    protected void loadTargetedQuote() {
        loadQuote(app.getCurrentQuote());
        checkButtons();
    }

    private void setListeners(){
        btnHideWords.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWords();
            }
        });
        
        btnShowAllWords.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllWords();
            }
        });
        
        
        btnPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevQuote();
            }
        });
        
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextQuote();

            }
        });
    }

    private void loadNextQuote(){
        if(app.isLastQuoteInSet()){
            // show toast
            Toast.makeText(activity,activity.getResources().getString(R.string.prompt_quote_last_in_set),Toast.LENGTH_SHORT).show();
        }else{
            hiddenWordsArray = null;
            loadQuote(app.getNextQuote());
            checkButtons();
        }
    }

    protected void loadPrevQuote(){
        if(app.isFirstQuoteInSet()){
            // show toast
            Toast.makeText(activity,activity.getResources().getString(R.string.prompt_quote_first_in_set),Toast.LENGTH_SHORT).show();
        }else{
            hiddenWordsArray = null;
            loadQuote(app.getPreviousQuote());
            checkButtons();
        }
    }
    
    protected void checkButtons() {
        //btnPrevious.setEnabled(!app.isFirstQuoteInSet());
       // btnNext.setEnabled(!app.isLastQuoteInSet());
    }


    /**
     * takes a quote
     */

    private void loadQuote(Quote quote){
        currentQuote = quote;
        isUserQuote = currentQuote.isUserQuote();
        isQuoteMemorized = app.isMemorizedQuote(quote);
        isQuoteFavorite = app.isFavoriteQuote(quote);
        TextView quoteSequence = (TextView) findViewById(R.id.quote_sequence);
        quoteSequence.setText("Quote " + (app.currentQuoteSetIndex + 1) + " of " + app.currentQuoteSet.size());

        Log.i(TAG,"quote:" + quote);
        if(quote!=null){
            String text = quote.getText();
            String author = quote.getAuthor();
            String reference = quote.getReference();
            String language = quote.getLanguage();
            String introText = quote.getIntroText();
            int id = quote.getId();
            loadQuote(introText, text,author,reference,language);
            // set the default font size from prefs
            changeFontSize(ImemorizeApplication.getSharedPrefInt(ImemorizeApplication.PREFS_FONT_SIZE_INDEX, getResources().getInteger(R.integer.default_font_size_index)));
        }
        invalidateOptionsMenu();

        ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_MEMORIZE,Utils.getQuoteTextForTracking(currentQuote));
    }
    /**
     * takes three strings and cleans them up and sets the variables
     */
    private void loadQuote(String introText, String quote,String author, String source, String language){
        introText = Utils.cleanupText(introText);
        quote = Utils.cleanupText(quote);
        author = Utils.cleanupText(author);
        source = Utils.cleanupText(source);
        // Log.d(TAG,quote);
        String hideWordsString = "";
        if(hiddenWordsArray!=null){
            for(int i=0; i<hiddenWordsArray.length; i++){
                if(i>0){
                    hideWordsString+=",";
                }
                hideWordsString += hiddenWordsArray[i];
            }
        }
        // Log.d(TAG, " hideWordsString: " + hideWordsString);

       // String introText = "";

        memorizeView.loadUrl("javascript:HideQuoteGame.buildQuoteScreen('" + introText + "', '" + quote + "','"+author+"','"+source+"','"+language+"','" + hideWordsString + "')");
        // add introtext if there is any
       // memorizeView.loadUrl("javascript: HideQuoteGame.addIntrotext('intro text')");

    }
    
    
    private void hideWords(){
        Log.i(TAG,"hideWords()");
        memorizeView.loadUrl("javascript:HideQuoteGame.hideWords()");
    }
    
    private void showAllWords(){
        Log.i(TAG,"showAllWords()");
        memorizeView.loadUrl("javascript:HideQuoteGame.showAllWords()");
        btnHideWords.setEnabled(true);

    }

    
    /// this sets the actual font size in the webview
    public void changeFontSize(int fontSizeIndex){
        Log.d(TAG, "the font sie should be:" + fontSizeIndex);
        // save to prefs
        ImemorizeApplication.setSharedPrefInt(ImemorizeApplication.PREFS_FONT_SIZE_INDEX,fontSizeIndex);
        // get the String array, select the index, remove the pt. and get the integer value
        String[] fontArray = getResources().getStringArray(R.array.font_size_array);
        int fontSize = Integer.parseInt(fontArray[fontSizeIndex].replace(" pt.", ""));
        memorizeView.loadUrl("javascript:setFontSize(" + fontSize + ")");
    }



    private void deleteUserQuote(){
        app.deleteUserQuote();
        Toast.makeText(this,getResources().getString(R.string.prompt_quote_deleted),Toast.LENGTH_SHORT).show();
        finish();
    }

    private void editUserQuote(){
        Log.d(TAG,"editQuote()");
        Intent intent = new Intent(this, AddQuoteActivity.class);
        intent.putExtra(Consts.ACTION,Consts.ACTION_EDIT_QUOTE);
        this.startActivity(intent);
    }


    private void showFontSizeSelector(){
        changeFontSize(30);
    }

    private void addQuoteToFavorites(){

    }

    // -----------------------------------------------------
    // Menu -- 
    // -----------------------------------------------------
    //manually create the menu so we can reset the favorites icon when the user toggles it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //FIXME - need to get these states from the DB

        // these are change on the fly so we can reset them based on the state
        int memorizedDrawable = R.drawable.ic_action_memorize;
        if(isQuoteMemorized)memorizedDrawable = R.drawable.ic_action_memorize_selected;

        int heartDrawable = R.drawable.ic_action_favorite;
        if(isQuoteFavorite)heartDrawable = R.drawable.ic_action_favorite_selected;

        menu.add(0,MENU_ITEM_ID_FAVORITES,0,"toggle favorites")
            .setIcon(heartDrawable)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0,MENU_ITEM_ID_MEMORIZED,1,"toggle memorized")
                .setIcon(memorizedDrawable)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0,MENU_ITEM_ID_SHARE,2,"share")
            .setIcon(R.drawable.ic_action_share)
            //.setActionProvider(mShareActionProvider)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0,MENU_ITEM_ID_FONT_SIZE,3,"Change font size")
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);


        // only allow user to enter quote if it is already on a user quote set
        if(isUserQuote){

            menu.add(0,MENU_ITEM_ID_ADD_QUOTE,4,"Add Quote")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

            menu.add(0,MENU_ITEM_ID_DELETE_QUOTE,5,"Delete Quote")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.add(0,MENU_ITEM_ID_EDIT_QUOTE,6,"Edit Quote")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }

        /*menu.add(0,3,3,"Feedback")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(0,4,4,"About")
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
*/


        return true;


    } 

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case MENU_ITEM_ID_FAVORITES:
                Log.i(TAG, "toggle favorites");
                toggleFavorite();
                break;
            case MENU_ITEM_ID_MEMORIZED:
                Log.i(TAG, "toggle memorized");
                toggleMemorized();
                break;
            case MENU_ITEM_ID_SHARE:
                Log.i(TAG, "share");
                shareQuote(currentQuote);
                break;
            case MENU_ITEM_ID_FONT_SIZE:
                Log.i(TAG, "change font size");
                FontSizeDialogFragment fontDialog = new FontSizeDialogFragment();
                fontDialog.show(this.getFragmentManager(),"fontSize");
                break;
            case MENU_ITEM_ID_ADD_QUOTE:
                Log.i(TAG, "add quote");
                showAddQuoteActivity();
                break;
            case MENU_ITEM_ID_DELETE_QUOTE:
                Log.i(TAG, "delete quote");
                deleteUserQuote();
                break;
            case MENU_ITEM_ID_EDIT_QUOTE:
                Log.i(TAG, "edit quote");
                editUserQuote();
                break;
            default:
                break;
        }
        return true;
    }

    private void toggleFavorite() {
        String toastMsg = "";
        if(isQuoteFavorite){
           app.deleteFavoriteQuote(currentQuote);
            toastMsg = getString(R.string.prompt_quote_removed_from_favorites);
        }else{
            app.addFavoriteQuote(currentQuote);
            toastMsg = getString(R.string.prompt_quote_added_to_favorites);
            ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_ADD_FAVORITE,Utils.getQuoteTextForTracking(currentQuote));
        }
        isQuoteFavorite=!isQuoteFavorite;
        invalidateOptionsMenu();
        Toast.makeText(this,toastMsg,Toast.LENGTH_SHORT).show();
    }

    private void toggleMemorized() {
        String toastMsg = "";
        if(isQuoteMemorized){
            app.deleteMemorizedQuote(currentQuote);
            toastMsg = getString(R.string.prompt_quote_removed_from_memorized);
        }else{
            app.addMemorizedQuote(currentQuote);
            toastMsg = getString(R.string.prompt_quote_added_to_memorized);
            ((ImemorizeApplication)getApplication()).trackEvent(Consts.TRACK_EVENT_TYPE_ADD_MEMORIZED,Utils.getQuoteTextForTracking(currentQuote));
        }
        isQuoteMemorized=!isQuoteMemorized;
        invalidateOptionsMenu();
        Toast.makeText(this,toastMsg,Toast.LENGTH_SHORT).show();
    }

    // methods that can be called by the JS on the webview

    class JsObject {

        @JavascriptInterface
        public String toString() { return "injectedObject"; }

        @JavascriptInterface
        public void allWordsHidden(final boolean tf){
           // Log.d(TAG,"allWordsHidden()" + tf);
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    btnHideWords.setEnabled(!tf);
                }
            });
        }

        @JavascriptInterface
        public void setHiddenWordsArray(int[] _hiddenWordsArray){
            hiddenWordsArray = _hiddenWordsArray;
            //Log.d(TAG,"hiddenwordarray length" + hiddenWordsArray.length);
           // for(int i : hiddenWordsArray) Log.d(TAG, "hidden:" + i);

        }

        @JavascriptInterface
        public void someWordsHidden(boolean tf){
            //Log.d(TAG,"someWordsHidden()" + tf);
 //             btnHideWords.setEnabled(tf);
//            btnShowAllWords.setEnabled(tf);
        }


    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    loadNextQuote();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    loadPrevQuote();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public void onClick(View v) {
        Utils.logger(TAG, "it was touched");
        Toast.makeText(this,"touched", Toast.LENGTH_SHORT).show();
    }

    
   
}
