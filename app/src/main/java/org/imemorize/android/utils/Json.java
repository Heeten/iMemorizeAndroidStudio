package org.imemorize.android.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by briankurzius on 1/28/14.
 */
public class Json {
    private static final String TAG = "Json";


    // get JSON data from web
    public static JSONObject getJSONfromURL(String url){
        // TODO -- put this in a separate thread
        //initialize
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Utils.logger(TAG, "Error in http connection "+e.toString());
            return null;
        }

        //convert response to string
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
            Utils.logger(TAG, "This is the json result: " + result);
        }catch(Exception e){
            Utils.logger(TAG, "Error converting result " + e.toString());
            return null;
        }
        //try parse the string to a JSON object
        // if we can't parse it because there are no results then
        // create an empty JSONObject
        // otherwise return null so we can display the error
        try{
            jArray = new JSONObject(result);
        }catch(JSONException e){
            Utils.logger(TAG, "Error parsing data " + e.toString());
            jArray = null;
        }
        return jArray;
    }

    public static JSONObject convertToJSON(String jString){
        JSONObject jArray = null;
        //try parse the string to a JSON object
        try{
            jArray = new JSONObject(jString);
        }catch(JSONException e){
            Utils.logger(TAG, "Error parsing data " + e.toString());
        }
        return jArray;
    }

    // given the json file and the root node, find the JSONObject by the index
    public static JSONObject getJsonObjectByIndex(JSONObject json, String rootNode, int index){
        JSONObject obj;
        try{
            JSONArray results = json.getJSONArray(rootNode);
            obj = results.getJSONObject(index);
        }catch(JSONException e){
            Utils.logger("log_tag", "Error parsing data "+ e.toString());
            obj = null;
        }
        return obj;
    }
}
