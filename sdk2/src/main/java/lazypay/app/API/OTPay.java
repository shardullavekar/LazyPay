package lazypay.app.API;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import lazypay.app.Callback;
import lazypay.app.Config;
import lazypay.app.REST.Post;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class OTPay {
    private static final String url = "api/lazypay/v0/payment/pay";

    public OTPay() {

    }

    public void pay(Callback callback, JSONObject jsonObject, String accessKey, String signature) {
        OTPayAsynch asynch = new OTPayAsynch(callback, jsonObject, accessKey, signature);
        asynch.execute();
    }

    private class OTPayAsynch extends AsyncTask<Void, Void, String> {
        Callback callback;
        JSONObject jsonObject;
        String signature, accessKey;
        public OTPayAsynch(Callback callback, JSONObject jsonObject, String accessKey, String signature) {
            this.callback = callback;
            this.jsonObject = jsonObject;
            this.signature = signature;
            this.accessKey = accessKey;
        }

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            Post checkPost = new Post();
            try {
                response = checkPost.postdata(Config.TEST + url, jsonObject.toString(), accessKey, signature);
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
    }
}
