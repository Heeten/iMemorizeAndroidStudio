package org.imemorize.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.imemorize.android.ImemorizeApplication;
import org.imemorize.android.R;
import org.imemorize.android.fragments.LanguageDialogFragment;
import org.imemorize.android.model.Consts;
import org.imemorize.android.model.Quote;
import org.imemorize.android.utils.Utils;

import java.util.ArrayList;

/**
 * Created by briankurzius on 10/7/13.
 */
public class AddQuoteActivity extends BaseActivity {
    private final static String TAG = "AddQuoteActivity";
    private TextView tvText;
    private TextView tvAuthor;
    private TextView tvReference;
    private Button btnAddQuote;
    private Button btnAddQuoteAndMemorize;
    private Button btnCancel;
    private ImemorizeApplication app;
    private String action;
    private boolean actionEdit = false;
    private Quote thisQuote;
    private boolean addedQuote = false;
    private Button btnChooseLanguage;
    private String languageText = "";
    private String selectedLanguage;
    private String[] languageArray;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ImemorizeApplication) this.getApplication();
        setContentView(R.layout.activity_add_quote);
        tvText = (TextView) findViewById(R.id.tvText);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvReference = (TextView) findViewById(R.id.tvReference);
        btnAddQuote = (Button) findViewById(R.id.btnAdd);
        btnAddQuoteAndMemorize = (Button) findViewById(R.id.btnAddQuoteAndMemorize);
        btnCancel = (Button) findViewById(R.id.btnDone);
        btnChooseLanguage = (Button) findViewById(R.id.btn_choose_language);
        languageArray = getResources().getStringArray(R.array.language_array);

        setLanguagePickerText(languageArray[Consts.DEFAULT_LANGUAGE_INDEX]);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            action = bundle.getString(Consts.ACTION);
            if(action.equalsIgnoreCase(Consts.ACTION_EDIT_QUOTE))actionEdit = true;
        }

        if(actionEdit){
            // hide the edit and memorize
            btnAddQuoteAndMemorize.setVisibility(View.GONE);
            // change the title of the add button
            btnAddQuote.setText(R.string.btn_label_edit_quote);
        } else{
            // set the language picker accordingly
            setQuoteLanguage(languageArray[Consts.DEFAULT_LANGUAGE_INDEX]);
        }
        btnAddQuoteAndMemorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuoteAndMemorize();
            }
        });
        btnAddQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(actionEdit){
                    updateQuote();
                }else{
                    addQuote();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });

        btnChooseLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageSpinner();
            }
        });

        if(actionEdit)populateQuote();

        // set analytics
        ((ImemorizeApplication)getApplication()).trackScreen(Consts.TRACK_SCREEN_ADD_QUOTE_VIEW);
    }

    private void showLanguageSpinner(){
        LanguageDialogFragment dialog = new LanguageDialogFragment();
        dialog.setLanguage(getLanguageIndex(selectedLanguage));
        dialog.show(this.getFragmentManager(),"language");
    }

    private void populateQuote(){
        thisQuote = app.getCurrentQuote();
        tvText.setText(Utils.cleanupEditQuoteText(thisQuote.getText()));
        tvAuthor.setText(Utils.cleanupEditQuoteText(thisQuote.getAuthor()));
        tvReference.setText(Utils.cleanupEditQuoteText(thisQuote.getReference()));
        setQuoteLanguage(thisQuote.getLanguage());
        Log.d(TAG,"populateQuote(): the language is: " + thisQuote.getLanguage());
    }

    private void addQuote(){
        if(!tvText.getText().toString().isEmpty()){
            String text = Utils.cleanupAddQuoteText(tvText.getText().toString());
            String author = Utils.cleanupAddQuoteText(tvAuthor.getText().toString());
            String reference = Utils.cleanupAddQuoteText(tvReference.getText().toString());
            String language = selectedLanguage;
            Log.d(TAG,"the language is: " + language);
            if (app.addUserQuote(text,author,reference,language)){
                Toast.makeText(this,getResources().getString(R.string.prompt_quote_added),Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,getResources().getString(R.string.prompt_quote_not_added), Toast.LENGTH_SHORT).show();
            }
            //clear text
            tvText.setText("");
            tvAuthor.setText("");
            tvReference.setText("");
            // leave the language as it was chosen
            //setLanguagePickerText(languageArray[Consts.DEFAULT_LANGUAGE_INDEX]);
            addedQuote = true;

        }else{
            Toast.makeText(this,getResources().getString(R.string.prompt_quote_error_no_text),Toast.LENGTH_SHORT).show();
        }

    }

    private void setLanguagePickerText(String language){
        btnChooseLanguage.setText(getString(R.string.btn_language_text) + language);
    }

    private void addQuoteAndMemorize(){
        if(!tvText.getText().toString().isEmpty()){
            addQuote();
            goToMemorizeView();
        }else{
            Toast.makeText(this,getResources().getString(R.string.prompt_quote_error_no_text),Toast.LENGTH_SHORT).show();
        }
    }

    // create a new Quote object and populate it with the new values
    private void updateQuote(){
        Quote newQuote = new Quote();
        // get the ID of the current quote
        newQuote.setId(thisQuote.getId());
        // get the new values
        newQuote.setText(Utils.cleanupAddQuoteText(tvText.getText().toString()));
        newQuote.setAuthor(Utils.cleanupAddQuoteText(tvAuthor.getText().toString()));
        newQuote.setReference(Utils.cleanupAddQuoteText(tvReference.getText().toString()));
        newQuote.setLanguage(selectedLanguage);
        app.updateUserQuote(newQuote);
        Toast.makeText(this,getResources().getString(R.string.prompt_quote_updated),Toast.LENGTH_SHORT).show();
        // now go back to the previous activity
        this.finish();
    }

    private void done(){
        if(!addedQuote){
            finish();
        }else{
            goToUserQuoteList();
        }
    }


    private void goToMemorizeView(){
        ArrayList<Quote> quoteList = (ArrayList<Quote>) app.getUserQuoteSet();
        // but if there are no quotes then show a toast - this should NEVER happen

        if(quoteList.size()>0){
            app.setCurrentQuoteSetIndex(quoteList.size()-1);
            Intent memorizeIntent = new Intent(this, MemorizeActivity.class);
            memorizeIntent.putExtra(QuoteListActivity.CONST_QUOTEPOSITION,quoteList.size()-1);
            memorizeIntent.putExtra(QuoteListActivity.CONST_USERQUOTES,true);
            this.startActivity(memorizeIntent);
            finish();
        }else{
            Log.i(TAG,"there are no quotes in this list item");
            Toast.makeText(this,"no quotes",Toast.LENGTH_SHORT).show();
        }
    }

    private void goToUserQuoteList(){
        ArrayList<Quote> quoteList = (ArrayList<Quote>) app.getUserQuoteSet();
        // but if there are no quotes then show a toast - this should NEVER happen

        if(quoteList.size()>0){
            Intent quoteListIntent = new Intent(this,QuoteListActivity.class);
            quoteListIntent.putExtra(CategoryListActivity.CONST_CATID, CategoryListActivity.CONST_USER_QUOTES_ID);
            quoteListIntent.putExtra(CategoryListActivity.CONST_CAT_NAME, "");
            this.startActivity(quoteListIntent);
            finish();
        }else{
            Log.i(TAG,"there are no quotes in this list item");
            Toast.makeText(this,"no quotes",Toast.LENGTH_SHORT).show();
        }
    }

    public void setQuoteLanguage(String language){
        selectedLanguage = language;
        setLanguagePickerText(selectedLanguage);
    }

    private int getLanguageIndex(String language){
        int index = -1;
        for(int i=0; i<languageArray.length; i++){
            if(languageArray[i].equalsIgnoreCase(language)){
                index = i;
                break;
            }
        }
        return index;
    }
}