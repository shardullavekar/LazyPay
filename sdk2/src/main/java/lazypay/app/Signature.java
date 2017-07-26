package lazypay.app;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import lazypay.app.REST.Post;

/**
 * Created by shardullavekar on 04/07/17.
 */

public class Signature {
    Callback callback;

    public Signature(Callback callback) {
        this.callback = callback;
    }

    public void eligibilitySign(String email, String mobile, String amount, String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "elligibility");
            jsonObject.put("email", email);
            jsonObject.put("amount", amount);
            jsonObject.put("mobile", mobile);
            jsonObject.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SignatureAsych signatureAsych = new SignatureAsych();
        signatureAsych.execute(new JSONObject[]{jsonObject});
    }

    public void initPaysign(String txnId, String amount, String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "initpay");
            jsonObject.put("txnid", txnId);
            jsonObject.put("amount", amount);
            jsonObject.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SignatureAsych signatureAsych = new SignatureAsych();
        signatureAsych.execute(new JSONObject[]{jsonObject});

    }

    public void autoDebitsign(String txnId, String amount, String url) {
        initPaysign(txnId, amount, url);
    }


    public void otpsign(String txReferencenum, String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "otpsign");
            jsonObject.put("txnref", txReferencenum);
            jsonObject.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SignatureAsych signatureAsych = new SignatureAsych();
        signatureAsych.execute(new JSONObject[]{jsonObject});

    }

    private class SignatureAsych extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            Post post = new Post();
            return post.formPost(jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            callback.onResponse(s);
        }
    }
}
