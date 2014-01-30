package org.imemorize;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import org.imemorize.android.utils.DataBaseHelper;
import org.imemorize.android.utils.UpdateManager;
import org.imemorize.android.utils.Utils;
import org.imemorize.fragments.SearchTermDialogFragment;
import org.imemorize.model.Category;
import org.imemorize.model.Consts;
import org.imemorize.model.Quote;

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
    public final static String PREFS_FONT_SIZE = "prefs_font_size";
    private String  mFavoriteQuotesString = "";
    private String  mMemorizedQuotesString = "";
    public boolean hasUserBeenNotifiedOfAppUpdate = false;

    // config variables
    public String currentAppVersionName = "";
    public int currentAppVersionCode = 0;
    public int latestVersionCode = 0;
    public String latestVersionDetails = "";
    public String latestVersionURL = "http://play.google.com";
    public String sponsorImageURL = "";
    public String promoURL = "http://www.history.com/interactives/history-here";


    private SharedPreferences prefs;

    // register to listen for the update manager
    private UpdateRequestReturnedReceiver updateRequestReturnedReceiver;
    private IntentFilter updateRequestReturnedFilter;
    public HashMap<String,String> updateData;
    public boolean hasShownUpdateDialogThisSession = false;


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

        //get the versionName from the manifest
        try{
            currentAppVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        }catch (NameNotFoundException e){
            Log.v(TAG,e.getMessage());
        }

        // update the config
        setReceivers();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UpdateManager updateManager = new UpdateManager();
        registerReceiver(updateRequestReturnedReceiver, updateRequestReturnedFilter);
        String updateURL = Consts.URL_CONFIG;
        if(Consts.TEST_UPDATE_CONFIG){
           updateURL = Consts.URL_CONFIG_TEST;
        }
        updateManager.updateConfig(this, updateURL);


        initializeGa();
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        Utils.logger(TAG,">>>>>>onTerminate");
        clearReceivers();
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


    public boolean wasDatabaseUpdated(){
        return myDbHelper.wasDatabaseUpdated;
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


    // -----------------------------------------------------
    // App update methods
    // -----------------------------------------------------
    // this checks the preferences and sees if the user has elected not to be reminded of new updates
    public boolean remindOnNewVersion(){

        Utils.logger(TAG,"in remindOnNewVersion");
            if(prefs.contains("dontRemindVersion" + currentAppVersionCode)){
                Utils.logger(TAG,"prefs has NOT REMIND FLAG");
                return false;
            }else{
                Utils.logger(TAG,"prefs *DOES NOT HAVE* NOT REMIND FLAG");
                return true;
            }
    }

    public boolean isLatestVersion(){
        boolean tf = true;
        Utils.logger(TAG,"compareVersionCode: " + latestVersionCode);
        Utils.logger(TAG,"getCurrentAppVersion(): " + currentAppVersionCode);
        if(latestVersionCode > currentAppVersionCode) {
            tf = false;
        }
        return tf;
    }


    // this sets a preference not to be reminded on new updates
    public void setDontRemindOnNewVersion(){
        //set a preference not to remind on this version
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("dontRemindVersion" + currentAppVersionCode, true);
        edit.commit();
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



    // -----------------------------------------------------
    // Receivers
    // -----------------------------------------------------

    private void setReceivers(){
        updateRequestReturnedReceiver = new UpdateRequestReturnedReceiver();
        updateRequestReturnedFilter = new IntentFilter(UpdateManager.UPDATE_DATA_RECEIVED);
        registerReceiver(updateRequestReturnedReceiver, updateRequestReturnedFilter);
    }

    private void clearReceivers(){
        unregisterReceiver(updateRequestReturnedReceiver);
    }

    class UpdateRequestReturnedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.logger(TAG,"UpdateRequestReturnedReceiver -- received!");
            // check if the preferences sponsorImage is the same and if not, download the new one -- or delete it if it doesn't exist
            //unregister the receiver
            unregisterReceiver(updateRequestReturnedReceiver);
            // now get the detail from the intent
            String key = UpdateManager.UPDATE_DATA_KEY;
            @SuppressWarnings("unchecked")
            HashMap<String,String> updateDetails = (HashMap<String,String>)intent.getExtras().get(key);
            if(null == updateDetails){
                Utils.logger(TAG,"updateDetails is NULL: ");
            }else{
                updateData = updateDetails;
                Utils.logger(TAG,"save the update config variables");
                // now set the variables so they are available anywhere in the app
                latestVersionCode = Integer.parseInt(updateData.get(UpdateManager.KEY_CONFIG_VERSION));
                latestVersionDetails = updateData.get(UpdateManager.KEY_CONFIG_UPDATE_DETAILS);
                latestVersionURL = updateData.get(UpdateManager.KEY_UPDATE_URL);
                Utils.logger(TAG, "The file on the server is: " + sponsorImageURL);
                Utils.logger(TAG, "now change the file for the next time --: ");
            }
        }
    };


    public void showSearch(final FragmentActivity activity){
        SearchTermDialogFragment dialog = new SearchTermDialogFragment();
        dialog.show(activity.getSupportFragmentManager(),"Search");
    }



}
