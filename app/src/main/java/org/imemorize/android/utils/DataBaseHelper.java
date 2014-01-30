package org.imemorize.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.imemorize.model.Category;
import org.imemorize.model.Consts;
import org.imemorize.model.Quote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {


    
    private final static String TAG = "DataBaseHelper";

    private static int DATABASE_VERSION = 2;
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/org.imemorize/databases/";
    private static String DB_NAME = "imemorize";
    private static String DB_NAME_TEMP = "imemorize_temp";
    private static String DB_TABLE_QUOTES = "quotes";
    private static String DB_TABLE_CATEGORIES = "quotecategories";
    private static String DB_TABLE_QUOTE_SETS = "quotesets";
    private static String DB_TABLE_CATEGORY_RELATIONS = "categoryrelations";
    // user related tables
    private static String DB_TABLE_USER_CATEGORIES = "userquotecategories";
    private static String DB_TABLE_USER_QUOTES = "userquotes";
    private static String DB_TABLE_USER_CATEGORY_RELATIONS = "usercategoryrelations";
    private static String DB_TABLE_USER_FAVORITES = "userfavorites";
    private static String DB_TABLE_USER_MEMORIZED = "usermemorized";
    private static String DB_COLUMN_ID = "_id";
    private static String DB_COLUMN_QUOTE_ID = "quoteId";
    private static String DB_COLUMN_INTRO_TEXT = "introtext";
    private static String DB_COLUMN_TEXT = "text";
    private static String DB_COLUMN_AUTHOR = "author";
    private static String DB_COLUMN_REFERENCE = "reference";
    private static String DB_COLUMN_LANGUAGE = "language";
    private static String DB_COLUMN_URL = "url";
    private static String DB_COLUMN_SORT_ORDER = "sortOrder";
    private static String DB_COLUMN_ACTIVE = "active";
    private static String DB_COLUMN_CATEGORY_NAME = "catName";
    private static String DB_COLUMN_CATEGORY_ID = "catID";
    private static String DB_COLUMN_CATEGORY_PARENT = "catParent";

    private static String DB_USER_FLAG ="user_";
    
    private int AUTHOR_COLUMN_INDEX ;
    private int INTRO_TEXT_COLUMN_INDEX ;
    private int TEXT_COLUMN_INDEX ;
    private int REFERENCE_COLUMN_INDEX ;
    private int LANGUAGE_COLUMN_INDEX ;
    private int CATEGORY_NAME_COLUMN_INDEX;
    private int URL_COLUMN_INDEX;
    private int CATEGORY_ID_COLUMN_INDEX;
    private int CATEGORY_PARENT_COLUMN_INDEX;
    private int ID_COLUMN_INDEX;
    private int QUOTE_ID_COLUMN_INDEX ;
    
    private static SQLiteDatabase myDataBase;
    private final Context myContext;
    
    private Cursor cursor = null;
    private ArrayList<Quote> userQuotes;
    public boolean wasDatabaseUpdated = false;
    private ArrayList<Quote> savedQuotes;
    private ArrayList<String> savedFavorites;
    private ArrayList<String> savedMemorized;
 

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Utils.logger(TAG, "onUpgrade");
        if ( newVersion > oldVersion){
            // TODO - get the users quotes and create a ListArray<Quote> -- so we can insert them again after the update is done

            Utils.logger(TAG, "New database version exists for upgrade.");
            try {
                Utils.logger(TAG, "Copying database...");
                copyDataBase();
                wasDatabaseUpdated = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }else{
            Utils.logger(TAG, "No upgrade.");
        }
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }


     /**
     * If does not exist - Creates a empty database on the system and rewrites it with our own database.
     * */
    public void createDataBase() throws IOException{
 
        boolean dbExist = checkDataBase();
        Log.i(TAG,"createDataBase()");
        if(dbExist){
            Log.i(TAG,"createDataBase: already exists");
            userQuotes = this.getUserQuoteSet();
            savedFavorites = this.getFavoritesQuoteArray();
            savedMemorized = getMemorizedQuoteArray();
            // force the update;
            this.getWritableDatabase();
            // then reinsert the user quotes if there were any and the db was updated
            if(wasDatabaseUpdated){
                Utils.logger(TAG,"createDataBase: the quotes are: " + userQuotes.size());
                // force the upgrade and add back the quotes
                for(Quote q:userQuotes){
                    Utils.logger(TAG,"createDataBase: addedget quote back");
                    addUserQuote(q);
                }
                // add the favorites
                for(String s:savedFavorites){
                    Utils.logger(TAG,"createDataBase: addedget quote back");
                    addFavoriteQuote(s);
                }
                // add the memorized
                for(String s:savedMemorized){
                    Utils.logger(TAG,"createDataBase: addedget quote back");
                    addMemorizedQuote(s);
                }
                Utils.logger(TAG,"createDataBase: should be done adding all items");
            }
        }else{
            Log.i(TAG,"DOES NOT exist - before getReadableDatabase();");
            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            try {
                this.getReadableDatabase();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                Log.i(TAG,"failed in getReadableDatabase");
                e1.printStackTrace();
            }
            Log.i(TAG,"DOES NOT exist - AFTER getReadableDatabase();");
            try {
                Log.i(TAG,"before copyDataBase()");
                copyDataBase();
 
            } catch (IOException e) {
 
                throw new Error("Error copying database");
 
            }
        }
    }

 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        Log.i(TAG,"checkDataBase()");
        SQLiteDatabase checkDB = null;
        File dbFile = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            dbFile = new File(DB_PATH + DB_NAME);
        }catch(SQLiteException e){
            //database does't exist yet.
        } 
        if(checkDB != null){
            Log.i(TAG,"got the db = now close it");
            checkDB.close();
        }
        return dbFile.exists();
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        Log.i(TAG,"copyDataBase()");
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME+".db");

        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/";
        }
 
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
 
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
 
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
 
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.i(TAG,"copyDataBase() - done");
 
    }

 
    public void openDataBase() throws SQLException{
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        Utils.logger(TAG, "myDataBase.getVersion()" + myDataBase.getVersion());
    }


    public ArrayList<Quote> getQuoteSet(int catID){
        Log.i(TAG, "getQuoteSet(int catID):" + catID);
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();
        String[] args = {catID + ""};
        String sql = "SELECT quotes._id, quotes.text, quotes.introtext, quotes.author, quotes.reference, quotes.language, quotes.url FROM quotecategories, quotesets, quotes WHERE quotecategories._id = quotesets.catid and quotesets.quoteid = quotes._id and quotesets.catid = ? order by quotesets.quoteid asc";
        Cursor cursor = myDataBase.rawQuery(sql, args);
        Log.i(TAG, "the length of the getQuoteSet result is:" + cursor.getCount());
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        AUTHOR_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
        INTRO_TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_INTRO_TEXT);
        TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_TEXT);
        REFERENCE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
        LANGUAGE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);
        URL_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_URL);
       
        while(cursor.moveToNext()){
            quoteList.add(getQuoteFromCursor(cursor, ID_COLUMN_INDEX, INTRO_TEXT_COLUMN_INDEX, TEXT_COLUMN_INDEX, AUTHOR_COLUMN_INDEX, REFERENCE_COLUMN_INDEX,LANGUAGE_COLUMN_INDEX,URL_COLUMN_INDEX));
        }
        Log.i(TAG, "the size of the quoteList result is:" + quoteList.size());
        close();
        return quoteList;
    }


    public ArrayList<Quote> getQuoteSetBySearch(String searchStr){
        Log.i(TAG, "getQuoteSetBySearch(String searchStr):" + searchStr);
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();

        // first get from quote

        // TODO - simply these two calls into a single method

        // now get from userquotes
        String sql = "SELECT  _id, text, author, reference, language FROM userquotes " +
                "WHERE text like '%" + searchStr + "%' " +
                "OR author like '%" + searchStr + "%' " +
                "OR reference like '%" + searchStr + "%'" ;
        cursor = myDataBase.rawQuery(sql, null);
        Log.i(TAG, "the length of the getQuoteSet result is:" + cursor.getCount());
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        AUTHOR_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
        TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_TEXT);
        REFERENCE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
        LANGUAGE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);

        while(cursor.moveToNext()){
            quoteList.add(getQuoteFromCursor(cursor, ID_COLUMN_INDEX, -1, TEXT_COLUMN_INDEX, AUTHOR_COLUMN_INDEX, REFERENCE_COLUMN_INDEX, LANGUAGE_COLUMN_INDEX,-1, true));
        }


        // db quotes have introtext
         sql = "SELECT  _id, text, introtext, author, reference,language, url FROM quotes " +
                "WHERE text like '%" + searchStr + "%' " +
                "OR author like '%" + searchStr + "%' " +
                "OR reference like '%" + searchStr + "%' group by text" ;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        Log.i(TAG, "the length of the getQuoteSet result is:" + cursor.getCount());
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        AUTHOR_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
        INTRO_TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_INTRO_TEXT);
        TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_TEXT);
        REFERENCE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
        LANGUAGE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);
        URL_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_URL);

        while(cursor.moveToNext()){
            quoteList.add(getQuoteFromCursor(cursor, ID_COLUMN_INDEX, INTRO_TEXT_COLUMN_INDEX, TEXT_COLUMN_INDEX, AUTHOR_COLUMN_INDEX, REFERENCE_COLUMN_INDEX,LANGUAGE_COLUMN_INDEX,URL_COLUMN_INDEX, false));
        }


        Log.i(TAG, "the size of the quoteList result is:" + quoteList.size());
        close();
        return quoteList;
    }

    // TODO -- add introtext to the quote
    private Quote getQuoteFromCursor(final Cursor cursor,
                                     final int _ID_COLUMN_INDEX,
                                     final int _INTRO_TEXT_COLUMN_INDEX,
                                     final int _TEXT_COLUMN_INDEX,
                                     final int _AUTHOR_COLUMN_INDEX,
                                     final int _REFERENCE_COLUMN_INDEX,
                                     final int _LANGUAGE_COLUMN_INDEX,
                                     final int _URL_COLUMN_INDEX
                                     ){
        return getQuoteFromCursor(cursor, _ID_COLUMN_INDEX, _INTRO_TEXT_COLUMN_INDEX, _TEXT_COLUMN_INDEX, _AUTHOR_COLUMN_INDEX, _REFERENCE_COLUMN_INDEX,_LANGUAGE_COLUMN_INDEX,_URL_COLUMN_INDEX, false);

    }

    // TODO -- add introtext to the quote
    private Quote getQuoteFromCursor(final Cursor cursor,
                                     final int _ID_COLUMN_INDEX,
                                     final int _INTRO_TEXT_COLUMN_INDEX,
                                     final int _TEXT_COLUMN_INDEX,
                                     final int _AUTHOR_COLUMN_INDEX,
                                     final int _REFERENCE_COLUMN_INDEX,
                                     final int _LANGUAGE_COLUMN_INDEX,
                                     final int _URL_COLUMN_INDEX,
                                     boolean isUserQuote){
        Quote quote = new Quote();
        if(isUserQuote){
            quote.setQuoteId(Consts.USER_QUOTE_PREFIX + cursor.getString(_ID_COLUMN_INDEX));
            quote.setIntroText("");
        }else{
            quote.setQuoteId(cursor.getString(_ID_COLUMN_INDEX));
            quote.setIntroText(cursor.getString(_INTRO_TEXT_COLUMN_INDEX));
            // add reference
            quote.setUrl(cursor.getString(_URL_COLUMN_INDEX));
        }
        quote.setId(Integer.parseInt(cursor.getString(_ID_COLUMN_INDEX)));
        quote.setText(cursor.getString(_TEXT_COLUMN_INDEX));
        quote.setAuthor(cursor.getString(_AUTHOR_COLUMN_INDEX));
        quote.setReference(cursor.getString(_REFERENCE_COLUMN_INDEX));
        quote.setLanguage(cursor.getString(_LANGUAGE_COLUMN_INDEX));
        return quote;
    }


    
    public ArrayList<Category> getCategoryChildren(int catID) {
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        ArrayList<Category> catList = new ArrayList<Category>();
        openDataBase();
        String[] args = {catID + ""};
        String sql = "select distinct quotecategories._id, quotecategories.catName from quotecategories, categoryrelations where quotecategories._id = categoryrelations.catID and categoryrelations.catParent = ? order by quotecategories.sortOrder, quotecategories.catName asc;";
        Cursor cursor = myDataBase.rawQuery(sql, args);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        CATEGORY_NAME_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_CATEGORY_NAME);
        while(cursor.moveToNext()){
            Category category = new Category();
            category.catName = cursor.getString(CATEGORY_NAME_COLUMN_INDEX);
            Log.d(TAG, "category.catName: " + category.catName);
            category.id = cursor.getInt(ID_COLUMN_INDEX);
            catList.add(category);
        }
        //*/
        Log.d(TAG, "the catList.size():" + catList.size());
        close();
        return catList;
    }


    // ******** USER QUOTES ***********

    // this allows the user to enter their own quotes
    public boolean addUserQuote(Quote q){
        return addUserQuote( q.getText(),  q.getAuthor(),  q.getReference(),  q.getLanguage());
    }

    // this allows the user to enter their own quotes
    public boolean addUserQuote(String text, String author, String reference, String language){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.DB_COLUMN_TEXT, text);
        values.put(DataBaseHelper.DB_COLUMN_REFERENCE, reference);
        values.put(DataBaseHelper.DB_COLUMN_AUTHOR, author);
        values.put(DataBaseHelper.DB_COLUMN_LANGUAGE, language);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DataBaseHelper.DB_TABLE_USER_QUOTES,
                null,
                values);
        Log.d(TAG,"the quote was added:" + newRowId);
        close();
        return newRowId>-1?true:false;
    }

    // this allows the user to enter their own quotes
    public boolean updateUserQuote(int id, String text, String author, String reference, String language){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.DB_COLUMN_TEXT, text);
        values.put(DataBaseHelper.DB_COLUMN_REFERENCE, reference);
        values.put(DataBaseHelper.DB_COLUMN_AUTHOR, author);
        values.put(DataBaseHelper.DB_COLUMN_LANGUAGE, language);
        String strFilter = "_id=" + id;
        db.update( DataBaseHelper.DB_TABLE_USER_QUOTES,values,strFilter, null);
        close();
        return true;
    }


    public ArrayList<Quote> getUserQuoteSet(){
        Log.i(TAG, "getUserQuoteSet()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();

        String[] projection = {
                DataBaseHelper.DB_COLUMN_ID,
                DataBaseHelper.DB_COLUMN_TEXT,
                DataBaseHelper.DB_COLUMN_AUTHOR,
                DataBaseHelper.DB_COLUMN_REFERENCE,
                DataBaseHelper.DB_COLUMN_LANGUAGE
        };
        Cursor cursor = myDataBase.query(
                DataBaseHelper.DB_TABLE_USER_QUOTES,  // The table to query
                projection,                               // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

//        String sql = "SELECT _id, text, author, reference FROM userquotes";
//        Cursor cursor = myDataBase.rawQuery(sql);
//        Log.i(TAG, "the length of the getQuoteSet result is:" + cursor.getCount());
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        AUTHOR_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
        TEXT_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_TEXT);
        REFERENCE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
        LANGUAGE_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);
        Log.i(TAG, "step two:");
        while(cursor.moveToNext()){
            quoteList.add(getQuoteFromCursor(cursor, ID_COLUMN_INDEX, -1, TEXT_COLUMN_INDEX, AUTHOR_COLUMN_INDEX, REFERENCE_COLUMN_INDEX, LANGUAGE_COLUMN_INDEX,-1, true));
        }
        close();
        return quoteList;
    }
    
    public void deleteUserQuote(int id){

        Log.d(TAG,"delete quote id" + id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE_USER_QUOTES,DB_COLUMN_ID + "=" + id,null);
        close();
    }

    public int getNumberOfUserQuotes(){
        Log.i(TAG, "getNumberOfUserQuotes()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_QUOTES;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        int num = cursor.getCount();
        close();
        return num;
    }


    // ******** FAVORITES ***********


    /**
     * adds a users favorite quotes into a db
     * @param id -- id of the quote from the table
     * @return
     */
    public boolean addFavoriteQuote(String id){
        Utils.logger(TAG,"DBHELPER addFavoriteQuote() : the id is:" + id);
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.DB_COLUMN_QUOTE_ID, id);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DataBaseHelper.DB_TABLE_USER_FAVORITES,
                null,
                values);
        Log.d(TAG,"the quote was added:" + id);
        return newRowId>-1?true:false;
    }

    /**
     * delete a user quote
     * @param id
     */
    public void deleteFavoriteQuote(String id){
        Log.d(TAG,"delete quote id: " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {id};
        db.delete(DB_TABLE_USER_FAVORITES, DB_COLUMN_QUOTE_ID + "=?", args);
        close();
    }

    public ArrayList<String> getFavoritesQuoteArray(){

        Log.i(TAG, "getFavoritesQuoteArray()");
        ArrayList<String> quoteList = new ArrayList<String>();
        openDataBase();
        // first get all the ids from the favorites
        String[] args = {};
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_FAVORITES;
        Cursor cursor = myDataBase.rawQuery(sql, args);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);
        while(cursor.moveToNext()){
            Log.i(TAG, "DBHELPER getFavoritesQuoteArray(): cursor.getInt(QUOTE_ID_COLUMN_INDEX) : " + cursor.getInt(QUOTE_ID_COLUMN_INDEX));
            String quoteId = cursor.getString(QUOTE_ID_COLUMN_INDEX);
            quoteList.add(quoteId);
        }
        close();
        return quoteList;
    }

    public String getFavoritesQuoteString(){

        Log.i(TAG, "getFavoritesQuoteString()");
        String str = "*";
        openDataBase();

        // first get all the ids from the favorites
        String[] args = {};
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_FAVORITES;
        Cursor cursor = myDataBase.rawQuery(sql, args);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);
        while(cursor.moveToNext()){
            Log.i(TAG, "DBHELPER getFavoritesQuoteString(): cursor.getInt(QUOTE_ID_COLUMN_INDEX) : " + cursor.getInt(QUOTE_ID_COLUMN_INDEX));
            str = str +  "*" + cursor.getString(QUOTE_ID_COLUMN_INDEX) + "*";
        }

        close();
        return str;
    }

    public ArrayList<Quote> getFavoriteQuoteSet(){
        Log.i(TAG, "getFavoriteQuoteSet()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();

        // first get all the ids from the favorites
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_FAVORITES;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);

        while(cursor.moveToNext()){
            //we have the quote set
            boolean userQuote = false;
            String dbTable = DB_TABLE_QUOTES;
            Utils.logger(TAG,"now loop through the results and get the individual quotes");
            String quoteId = cursor.getString(QUOTE_ID_COLUMN_INDEX);
            Utils.logger(TAG,"quote id: " + quoteId);
            if(quoteId.indexOf(Consts.USER_QUOTE_PREFIX)>-1){
                Log.i(TAG, "getFavoriteQuoteSet(): USER QUOTE: " + quoteId);
                userQuote = true;
                // remove the prefix
                quoteId =  quoteId.replace(Consts.USER_QUOTE_PREFIX,"");
                // set the table
                dbTable = DB_TABLE_USER_QUOTES;
            }
            String[] args2 = {quoteId};
            // use the db of either the quotes or suer quotes
            String sql2 = "SELECT * FROM " + dbTable + " WHERE _id = ?";
            Cursor cursor2 = myDataBase.rawQuery(sql2, args2);
            Log.i(TAG, "getFavoriteQuoteSet(): USER QUOTE: NOW quoteId:" + quoteId + " : db table: " + dbTable);

            int colIdIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_ID);
            int colAuthorIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
            int colTextIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_TEXT);
            int colReferenceIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
            int colLanguageIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);
            int colIntroTextIndex = -1;
            int colUrlIndex = -1;
            // if the quote is a user quote then don't get the introtext colum
            if(dbTable.equalsIgnoreCase(DB_TABLE_QUOTES)){
                colIntroTextIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_INTRO_TEXT);
                colUrlIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_URL);
            }

            while(cursor2.moveToNext()){
                quoteList.add(getQuoteFromCursor(cursor2, colIdIndex, colIntroTextIndex, colTextIndex, colAuthorIndex, colReferenceIndex,colLanguageIndex,colUrlIndex, userQuote));
            }
        }
        close();
        return quoteList;
    }

    public int getNumberOfFavorites(){
        Log.i(TAG, "getNumberOfFavorites()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_FAVORITES;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        int num = cursor.getCount();
        close();
        return num;
    }


    // ******** MEMORIZED ***********


    /**
     * adds a users favorite quotes into a db
     * @param id -- id of the quote from the table
     * @return
     */
    public boolean addMemorizedQuote(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.DB_COLUMN_QUOTE_ID, id);
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DataBaseHelper.DB_TABLE_USER_MEMORIZED,
                null,
                values);
        Log.d(TAG,"the quote was added:" + id);
        return newRowId>-1?true:false;
    }

    public String getMemorizedQuoteString(){
        Log.i(TAG, "getMemorizedQuoteString()");
        String str = "*";
        openDataBase();
        // first get all the ids from the favorites
        String[] args = {};
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_MEMORIZED;
        Cursor cursor = myDataBase.rawQuery(sql, args);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);

        while(cursor.moveToNext()){
            str = str +  "*" + cursor.getString(QUOTE_ID_COLUMN_INDEX) + "*";
        }
        close();
        return str;
    }

    /**
     * delete a user quote
     * @param id
     */
    public void deleteMemorizedQuote(String id){
        Log.d(TAG,"delete memorized quote id" + id);
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = {id};
        db.delete(DB_TABLE_USER_MEMORIZED, DB_COLUMN_QUOTE_ID + "=?", args);
        close();
    }

    public int getNumberOfMemorized(){
        Log.i(TAG, "getNumberOfMemorized()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_MEMORIZED;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        int num = cursor.getCount();
        close();
        return num;
    }

    public ArrayList<String> getMemorizedQuoteArray(){
        Log.i(TAG, "getMemorizedQuoteArray()");
        ArrayList<String> quoteList = new ArrayList<String>();
        openDataBase();

        // first get all the ids from the favorites
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_MEMORIZED;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);

        while(cursor.moveToNext()){
            Utils.logger(TAG,"now loop through the results and get the individual quotes");
            String quoteId = cursor.getString(QUOTE_ID_COLUMN_INDEX);
            quoteList.add(quoteId);
        }
        close();
        return quoteList;
    }


    public ArrayList<Quote> getMemorizedQuoteSet(){
        Log.i(TAG, "getMemorizedQuoteSet()");
        ArrayList<Quote> quoteList = new ArrayList<Quote>();
        openDataBase();

        // first get all the ids from the favorites
        String[] args = null;
        String sql = "select * from " + DataBaseHelper.DB_TABLE_USER_MEMORIZED;
        Cursor cursor = myDataBase.rawQuery(sql, null);
        ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_ID);
        QUOTE_ID_COLUMN_INDEX = cursor.getColumnIndexOrThrow(DB_COLUMN_QUOTE_ID);

        while(cursor.moveToNext()){
            //we have the quote set
            boolean userQuote = false;
            String dbTable = DB_TABLE_QUOTES;
            Utils.logger(TAG,"now loop through the results and get the individual quotes");
            String quoteId = cursor.getString(QUOTE_ID_COLUMN_INDEX);
            Utils.logger(TAG,"quote id: " + quoteId);
            if(quoteId.indexOf(Consts.USER_QUOTE_PREFIX)>-1){
                Log.i(TAG, "getMemorizedQuoteSet(): USER QUOTE: " + quoteId);
                userQuote = true;
                // remove the prefix
                quoteId =  quoteId.replace(Consts.USER_QUOTE_PREFIX,"");
                // set the table
                dbTable = DB_TABLE_USER_QUOTES;
            }
            String[] args2 = {quoteId};
            // use the db of either the quotes or suer quotes
            String sql2 = "SELECT * FROM " + dbTable + " WHERE _id = ?";
            Cursor cursor2 = myDataBase.rawQuery(sql2, args2);
            Log.i(TAG, "getMemorizedQuoteSet(): USER QUOTE: NOW quoteId:" + quoteId + " : db tabel: " + dbTable);
            int colIntroTextIndex = -1;
            int colUrlIndex = -1;
            // if the quote is a user quote then don't get the introtext colum
            if(dbTable.equalsIgnoreCase(DB_TABLE_QUOTES)){
                colIntroTextIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_INTRO_TEXT);
                colUrlIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_URL);
            }

            int colIdIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_ID);
            int colAuthorIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_AUTHOR);
            int colTextIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_TEXT);
            int colReferenceIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_REFERENCE);
            int colLanguageIndex = cursor2.getColumnIndexOrThrow(DB_COLUMN_LANGUAGE);

            while(cursor2.moveToNext()){
                quoteList.add(getQuoteFromCursor(cursor2, colIdIndex, colIntroTextIndex, colTextIndex, colAuthorIndex, colReferenceIndex,colLanguageIndex,colUrlIndex, userQuote));
            }
        }
        close();
        return quoteList;
    }
    
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
    
    /**
     * @return the cursor
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * @param cursor the cursor to set
     */
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

}
