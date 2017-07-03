package lazypay.sdk.API;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lazypay.sdk.Callback;
import lazypay.sdk.Config;
import lazypay.sdk.REST.Post;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class Eligibility {
    private static final String url = "api/lazypay/v0/payment/eligibility";

    Context context;

    ApplicationInfo app;

    Bundle bundle;

    Callback callback;

    public Eligibility(Context context) {
        this.context = context;
        app = null;
        try {
            app = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bundle = app.metaData;

    }

    public void check(Callback callback, JSONObject jsonObject) {
        EligibilityAsynch asynch = new EligibilityAsynch(callback, jsonObject);
        asynch.execute();
    }

    private class EligibilityAsynch extends AsyncTask<Void, Void, String> {
        Callback callback;
        JSONObject jsonObject;
        public EligibilityAsynch(Callback callback, JSONObject jsonObject) {
            this.callback = callback;
            this.jsonObject = jsonObject;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            Post checkPost = new Post();
            try {
                response = checkPost.postdata(Config.TEST + url, jsonObject.toString(), bundle.getString(Config.ACCESS_KEY_NAME), "");
            } catch (IOException e) {
                e.printStackTrace();
                JSONObject error = new JSONObject();
                try {
                    error.put("Code", "IOException");
                    callback.onResponse(error.toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            callback.onResponse(response);
        }
    }
}
