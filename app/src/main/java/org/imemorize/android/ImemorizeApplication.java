package org.imemorize.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import org.imemorize.android.model.Category;
import org.imemorize.android.model.Consts;
import org.imemorize.android.model.Quote;
import org.imemorize.android.utils.DataBaseHelper;
import org.imemorize.android.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ImemorizeApplication extends Application {
    private final static String TAG = "ImemorizeApplication";
    private DataBaseHelper myDbHelper = null;
    private Quote currentQuote = null;
    public ArrayList<Quote> currentQuoteSet;
    public int currentQuoteSetIndex = 0;
    public static SharedPreferences smSharedPrefs;
    private final String PREFS_NAME = "prefs";
    private int currentQuoteSetCatId = 0;
    private String currentCategoryName = "";
    public final static String PREFS_FONT_SIZE_INDEX = "prefs_font_size_index";
    private String  mFavoriteQuotesString = "";
    private String  mMemorizedQuotesString = "";

    private static GoogleAnalytics mGa;
    private static Tracker mTracker;
    

    public ImemorizeApplication() {
        // TODO Auto-generated constructor stub
        Log.i(TAG,"initialize the application");
    }

    @Override
    public void onCreate(){

        super.onCreate();
        initDataBase(getApplicationContext());
        smSharedPrefs = this.getSharedPreferences(PREFS_NAME, 0);
        // get the strings from the database
        // this speeds up execution of the list views in particular
        resetFavoriteQuotesString();
        resetMemorizedQuotesString();

        initializeGa();

    }

    private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(Consts.GA_ANALYTICS_ID);

        // Set dispatch period.
        GAServiceManager.getInstance().setLocalDispatchPeriod(Consts.GA_DISPATCH_PERIOD);

        // Set dryRun flag.
        mGa.setDryRun(Consts.GA_IS_DRY_RUN);

        // Set Logger verbosity.
        mGa.getLogger().setLogLevel(Consts.GA_LOG_VERBOSITY);

    }

// Returns the Google Analytics tracker.


    public static Tracker getGaTracker() {
        return mTracker;
    }

    // * Returns the Google Analytics instance.


    public static GoogleAnalytics getGaInstance() {
        return mGa;
    }




    public void initDataBase(Context mContext){
        myDbHelper = new DataBaseHelper(mContext);
        // create db
        try {
            Log.i(TAG,"myDbHelper.createDataBase()");
            myDbHelper.createDataBase();
         } catch (IOException ioe) {
             throw new Error("Unable to create database");
         }
        /*
        // then get the quotes
        try {
            Log.i(TAG,"myDbHelper.openDataBase()");
            //myDbHelper.openDataBase();
            myDbHelper.getQuotes();
            currentQuote = getNextQuote();
            Log.i(TAG,"the first quote is: " + currentQuote.getText());
        }catch(SQLException sqle){
            throw sqle;
        }
        */
        //Log.i(TAG,"initDataBase() done");
    }
    
    public Quote getNextQuote(){
        Log.i(TAG,"currentQuoteSetIndex:" + currentQuoteSetIndex);
        currentQuoteSetIndex++;
        Log.i(TAG,"currentQuoteSetIndex is now:" + currentQuoteSetIndex);
        if(currentQuoteSetIndex<currentQuoteSet.size()){
            Log.i(TAG,"currentQuoteSet.size():" + currentQuoteSet.size());
            return currentQuoteSet.get(currentQuoteSetIndex);
        }
        return null;
    }
    
    public Quote getPreviousQuote(){
        currentQuoteSetIndex--;
        if(currentQuoteSetIndex>-1){
            return currentQuoteSet.get(currentQuoteSetIndex);
        }
        return null;
        
    }
    
    public void setCurrentQuoteSetIndex(int index){
        currentQuoteSetIndex = index;
    }

    public void setCurrentCategoryName(String name){
        currentCategoryName = name;
    }

    public String getCurrentCategoryName(){
        return currentCategoryName;
    }
    
    /**
     * @return the currentQuote
     */
    public Quote getCurrentQuote() {
        Log.i(TAG,""+currentQuoteSetIndex);
        return currentQuoteSet.get(currentQuoteSetIndex);
    }


    public void setQuoteSet(ArrayList<Quote> quoteSet){
        currentQuoteSet = quoteSet;
        currentQuoteSetIndex = 0;
    }



    public ArrayList<Quote> getQuoteSet(int catID){
        currentQuoteSetCatId = catID;
        ArrayList<Quote> quoteSet = myDbHelper.getQuoteSet(catID);
        if(quoteSet.size()>0){
            currentQuoteSet = quoteSet;
            currentQuoteSetIndex = 0;
            return currentQuoteSet;
        }
        return null;
     }



    public ArrayList<Category> getCategoryChildren(int catID) {
        return myDbHelper.getCategoryChildren(catID);
    }
    
    public boolean isFirstQuoteInSet(){
        if (currentQuoteSetIndex==0){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isLastQuoteInSet(){
        Log.d(TAG,"currentQuoteSetIndex: " + currentQuoteSetIndex + "|currentQuoteSet.size()" + currentQuoteSet.size());
        if (currentQuoteSetIndex == (currentQuoteSet.size() - 1)){
            return true;
        }else{
            return false;
        }
    }

    // ********* USER QUOTES **************

    public ArrayList<Quote> getUserQuoteSet(){
        ArrayList<Quote> quoteSet = myDbHelper.getUserQuoteSet();
        if(quoteSet.size()>0){
            currentQuoteSet = quoteSet;
            currentQuoteSetIndex = 0;
            return currentQuoteSet;
        }
        return null;
    }

    public boolean addUserQuote(String text, String author, String reference, String language){
        boolean tf = myDbHelper.addUserQuote(text,author,reference,language);
        // now update the quote set so we have the latest quotes
        getUserQuoteSet();
        return tf;
    }


    public boolean addUserQuote(Quote q){
        boolean tf = myDbHelper.addUserQuote(q.getText(),q.getAuthor(),q.getReference(),q.getLanguage());
        // now update the quote set so we have the latest quotes
        getUserQuoteSet();
        return tf;
    }

    public boolean updateUserQuote(Quote quote){
        boolean tf =  myDbHelper.updateUserQuote(quote.getId(),quote.getText(),quote.getAuthor(),quote.getReference(), quote.getLanguage());
        // now update the quote set so we have the latest quotes
        getUserQuoteSet();
        return tf;
    }

    // only called when the quote is a user generated quote
    public void deleteUserQuote(){
        Quote deleteQuote = currentQuoteSet.get(currentQuoteSetIndex);
        // get id
        int id = deleteQuote.getId();
        myDbHelper.deleteUserQuote(id);
        // now update the quote set so we have the latest quotes
        getUserQuoteSet();
    }

    public int getNumberOfUserQuotes(){
        return myDbHelper.getNumberOfUserQuotes();
    }

    // ********* FAVORITES **************

    public void addFavoriteQuote(Quote quote){
        Utils.logger(TAG,"addFavoriteQuote() : the id is:" + quote.getQuoteId());
        myDbHelper.addFavoriteQuote(quote.getQuoteId());
        resetFavoriteQuotesString();
    }

    public void deleteFavoriteQuote(Quote quote){
        Utils.logger(TAG,"the id is:" + quote.getQuoteId());
        myDbHelper.deleteFavoriteQuote(quote.getQuoteId());
        resetFavoriteQuotesString();
    }

    public int getNumberOfFavorites(){
        return myDbHelper.getNumberOfFavorites();
    }

    public boolean isFavoriteQuote(Quote quote){
       Utils.logger(TAG,"isFavoriteQuote()" + quote.getQuoteId() );
        return  mFavoriteQuotesString.indexOf("*" + quote.getQuoteId() + "*")>-1?true:false;
    }

    public ArrayList<Quote> getFavoriteQuoteSet(){
        ArrayList<Quote> quoteSet = myDbHelper.getFavoriteQuoteSet();
        if(quoteSet.size()>0){
            currentQuoteSet = quoteSet;
            currentQuoteSetIndex = 0;
            return currentQuoteSet;
        }
        return null;
    }

    private void resetFavoriteQuotesString(){
        mFavoriteQuotesString = myDbHelper.getFavoritesQuoteString();
        Utils.logger(TAG, "the favorites is: " + mFavoriteQuotesString);
    }

    // ********** MEMORIZED QUOTES ***************

    public void addMemorizedQuote(Quote quote){
        String id = quote.getQuoteId();
        myDbHelper.addMemorizedQuote(id);
        resetMemorizedQuotesString();
    }

    public void deleteMemorizedQuote(Quote quote){
        String id = quote.getQuoteId();
        myDbHelper.deleteMemorizedQuote(id);
        resetMemorizedQuotesString();
    }

    public ArrayList<Quote> getMemorizedQuoteSet(){
        ArrayList<Quote> quoteSet = myDbHelper.getMemorizedQuoteSet();
        if(quoteSet.size()>0){
            currentQuoteSet = quoteSet;
            currentQuoteSetIndex = 0;
            return currentQuoteSet;
        }
        return null;
    }

    public boolean isMemorizedQuote(Quote quote){
       return  mMemorizedQuotesString.indexOf("*" + quote.getQuoteId() + "*")>-1?true:false;
    }

    public int getNumberOfMemorized(){
        return myDbHelper.getNumberOfMemorized();
    }

    private void resetMemorizedQuotesString(){
        mMemorizedQuotesString = myDbHelper.getMemorizedQuoteString();
        Utils.logger(TAG, "the memorized is: " + mMemorizedQuotesString);
    }


    // ******** SEARCH ************

    public ArrayList<Quote> getQuoteSetBySearch(String searchStr){
        currentQuoteSet = myDbHelper.getQuoteSetBySearch(searchStr);
        return currentQuoteSet;

    }

    public ArrayList<Quote> getCurrentQuoteSet(){
        return currentQuoteSet;
    }


    // get and set SharedPrefs

    static public boolean getSharedPrefBoolean(String key, boolean defValue) {
        return smSharedPrefs.getBoolean(key, defValue);
    }

    static public int getSharedPrefInt(String key, int defValue) {
        return smSharedPrefs.getInt(key, defValue);
    }

    static public String getSharedPrefString(String key, String defValue) {
        return smSharedPrefs.getString(key, defValue);
    }

    static public void setSharedPrefBoolean(String key, boolean value) {
        smSharedPrefs.edit().putBoolean(key, value).commit();
    }

    static public void setSharedPrefInt(String key, int value) {
        smSharedPrefs.edit().putInt(key, value).commit();
    }

    static public void setSharedPrefString(String key, String value) {
        smSharedPrefs.edit().putString(key, value).commit();
    }


    /**
     *  ANALYTICS
     *
     *
     */

    /**
     * not sure if this is working
     * @param screenName
     */
     public void  trackScreen(String screenName){
        /*
        * Send a screen view to Google Analytics by setting a map of parameter
        * values on the tracker and calling send.
        */
        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.SCREEN_NAME, screenName);
         getGaTracker().send(hitParameters);


    }

    /**
     * Tracks a user event
     * @param eventType
     * @param eventName
     */
    public void trackEvent(String eventType, String eventName){
        /*
        * Send a screen view to Google Analytics by setting a map of parameter
        * values on the tracker and calling send.
        */

        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.EVENT_ACTION, eventType);
        hitParameters.put(Fields.EVENT_VALUE, eventName);
        getGaTracker().send(hitParameters);


        EasyTracker easyTracker = EasyTracker.getInstance(this);

        // MapBuilder.createEvent().build() returns a Map of event fields and values
        // that are set and sent with the hit.
        easyTracker.send(MapBuilder
                .createEvent(eventType,     // Event category (required)
                        eventName,  // Event action (required)
                        null,   // Event label
                        null)            // Event value
                .build()
        );

    }



}
