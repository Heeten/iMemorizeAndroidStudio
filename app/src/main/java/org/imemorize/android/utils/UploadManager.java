package org.imemorize.android.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.imemorize.model.Quote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by briankurzius on 1/18/14.
 */
public class UploadManager{
    private Context mContext;
    public static final String KEY_TEXT = "text";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_REFERENCE = "reference";
    public static final String KEY_LANGUAGE = "language";
    public static final String URL_UPLOAD = "http://imemorize.org/insert_quote_new.php";
    private LoadListener mLoadListener;
    private Quote mQuote;

    public interface LoadListener{
        public void onQuoteUploadComplete(String id);
        public void onQuoteUploadError(Exception e);
    }

    public UploadManager(LoadListener listener){
        mContext = (Context) listener;
        mLoadListener = listener;
    }

    public void uploadQuote(Quote quote){
        mQuote = quote;
        // new UploadTask
        new UploadTask().execute(quote);

    }



    private class UploadTask extends AsyncTask<Quote, Integer, String> {

        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Quote... quote) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL_UPLOAD);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair(KEY_TEXT, Utils.cleanupTextForUpload(quote[0].getText())));
                nameValuePairs
                        .add(new BasicNameValuePair(KEY_AUTHOR, Utils.cleanupTextForUpload(quote[0].getAuthor())));
                nameValuePairs
                        .add(new BasicNameValuePair(KEY_REFERENCE, Utils.cleanupTextForUpload(quote[0].getReference())));
                nameValuePairs
                        .add(new BasicNameValuePair(KEY_LANGUAGE, Utils.cleanupTextForUpload(quote[0].getLanguage())));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

                Log.d("UPLOAD MANAGER", "Utils.cleanupTextForUpload(quote[0].getText())):" + Utils.cleanupTextForUpload(quote[0].getText()));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                if (response != null) {
                    InputStream in = response.getEntity().getContent();
                    String responseContent = inputStreamToString(in);
                    responseContent = responseContent.replace("id:","");
                    responseContent = responseContent.trim();
                    return responseContent;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                mLoadListener.onQuoteUploadError(e);
            } catch (IOException e) {
                e.printStackTrace();
                mLoadListener.onQuoteUploadError(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            mLoadListener.onQuoteUploadComplete(result);
            // process the result
            super.onPostExecute(result);
        }



        private String inputStreamToString(InputStream is) throws IOException {
            String line = "";
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            // Read response until the end
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }

            // Return full string
            return total.toString();
        }

    }



}
