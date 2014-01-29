package org.imemorize.android.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by briankurzius on 1/28/14.
 */
public class UpdateManager {

    public static final String UPDATE_DATA_RECEIVED = "update data received";
    public static final String UPDATE_DATA_KEY = "updateData";
    public static final String KEY_CONFIG_VERSION = "versionNumber";
    public static final String KEY_CONFIG_UPDATE_DETAILS = "latestVersionDetails";
    public static final String KEY_UPDATE_URL = "latestVersionURL";
    private static final String TAG = "UpdateManager";
    private Context mContext;

    public UpdateManager() {
    }

    public void updateConfig(Context context, String url){
        mContext = context;
        new GetUpdateService().execute(url);
    }

    // -----------------------------------------------------
    // Private classes
    // -----------------------------------------------------

    // ------ AsyncTasks for getting the POIs ---------- //
    class GetUpdateService extends AsyncTask<String,Integer,JSONObject> {
        @Override
        protected JSONObject doInBackground(String... url){
            Utils.logger(TAG,"GetUpdateService:doInBackground()");
            // send message that data has been requested
            //JSONObject updateData = new JSONObject();
            JSONObject updateData = Json.getJSONfromURL(url[0]);
            if(null == updateData){
                Utils.logger(TAG,"GetUpdateService:error getting detail");
            }
            return updateData;
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // not used
        }

        @Override
        protected void onPostExecute(JSONObject result){
            HashMap<String,String> updateData = new HashMap<String,String>();
            try {
                if(result != null){
                    JSONObject configObj = result.getJSONObject("config");
                    updateData.put(UpdateManager.KEY_CONFIG_VERSION, configObj.getString(UpdateManager.KEY_CONFIG_VERSION));
                    updateData.put(UpdateManager.KEY_CONFIG_UPDATE_DETAILS, configObj.getString(UpdateManager.KEY_CONFIG_UPDATE_DETAILS));
                    updateData.put(UpdateManager.KEY_UPDATE_URL, configObj.getString(UpdateManager.KEY_UPDATE_URL));
                    Utils.logger(TAG,"updateData.size(): " + updateData.size());
                    //only send the update if we have data
                    Intent intent = new Intent(UpdateManager.UPDATE_DATA_RECEIVED);
                    intent.putExtra(UpdateManager.UPDATE_DATA_KEY, updateData);
                    mContext.sendBroadcast(intent);
                }else{
                    updateData = null;
                }

            } catch (JSONException e) {
                Utils.logger(TAG,"onPostExecute(): error ");
                e.printStackTrace();

            }
        }
    }
}
