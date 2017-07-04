package lazypay.sdk.API;

import android.os.AsyncTask;

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

    public Eligibility() {
    }

    public void check(Callback callback, JSONObject jsonObject, String accessKey) {
        EligibilityAsynch asynch = new EligibilityAsynch(callback, jsonObject, accessKey);
        asynch.execute();
    }

    private class EligibilityAsynch extends AsyncTask<Void, Void, String> {
        Callback callback;
        JSONObject jsonObject;
        String accessKey;
        public EligibilityAsynch(Callback callback, JSONObject jsonObject, String accessKey) {
            this.callback = callback;
            this.jsonObject = jsonObject;
            this.accessKey = accessKey;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            Post checkPost = new Post();
            try {
                response = checkPost.postdata(Config.TEST + url, jsonObject.toString(), accessKey, "");
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
