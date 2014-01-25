package org.imemorize.android.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.imemorize.model.Consts;
import org.imemorize.model.Quote;

/**
 * Created by briankurzius on 1/12/14.
 */
public class Utils {
    private final static String TAG = "Utils";

    /**
     * Shares content
     *
     * @param context
     * @param shareSubject
     * @param shareBody
     */
    public static void shareContent(final Context context, String shareSubject,
                                    String shareBody) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    public static String cleanupText(String inString) {
        String outString = inString.replaceAll("'", "&#146;");
        outString = outString.replaceAll("\n", "");
        outString = outString.replaceAll("\r", "");
        Log.d(TAG, "outstring: " +  outString);
        return outString;
    }

    public static String cleanupAddQuoteText(String inString) {
        String outString = inString.replaceAll("'", "&#146;");
        outString = outString.replaceAll("\n", " <br/>");
        outString = outString.replaceAll("<br />", " <br/>");
        Log.d(TAG, "outstring: " +  outString);
        return outString;
    }

    /*
    * we want to make sure the text the user sees looks normal to them
    */
    public static String cleanupEditQuoteText(String inString) {
        String outString = inString.replaceAll("&#146;", "'");
        outString = outString.replaceAll("<br/>", "\n");
        outString = outString.replaceAll("<br />", "\n");
        Log.d(TAG, "outstring: " +  outString);
        return outString;
    }

    /*
* we want to make sure the text the user sees looks normal to them
*/
    public static String cleanupQuoteListText(String inString) {
        String outString = inString.replaceAll("&#146;", "'");
        outString = outString.replaceAll("<br/>", " ");
        outString = outString.replaceAll("<br />", " ");
        Log.d(TAG, "outstring: " +  outString);
        return outString;
    }

    public static String cleanupTextForSharing(String inString) {
        String outString = inString.replaceAll("&#146;", "'" );
        outString = outString.replaceAll("\n", "");
        outString = outString.replaceAll("\r", "");
        outString = outString.replaceAll("<br/>", " ");
        outString = outString.replaceAll("<br />", " ");
        return outString;
    }

    public static String cleanupTextForUpload(String inString) {
        String outString = inString.replaceAll("&#146;", "'" );
        //outString = outString.replaceAll("\n", "");
        //outString = outString.replaceAll("\r", "");
        outString = outString.replaceAll("<br/>", "\n");
        outString = outString.replaceAll("<br />", "\n");
        return outString;
    }

    public static void logger(String TAG, String msg){
        Log.i(TAG, msg);
    }

    public static boolean isUserQuote(Quote quote){
        if(quote.getQuoteId().indexOf(Consts.USER_QUOTE_PREFIX)>-1){
            return true;
        }else{
            return false;
        }

    }

    public static String getQuoteTextForTracking(Quote quote){
        int subStr = 30;
        if(quote.getText().length()<30){
            subStr = quote.getText().length();
        }
        return quote.getQuoteId() + ":" + quote.getText().substring(0,subStr);
    }
}
