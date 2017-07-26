package lazypay.app.REST;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class Post {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String SIGNATURE = "signature";
    private static final String ACCESS_KEY = "accessKey";
    private static final String OAUTH = "Authorization";

    OkHttpClient client = new OkHttpClient();

    public String postdata(String url, String json, String accessKey, String signature) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (!TextUtils.isEmpty(accessKey)) {
            builder.addHeader(ACCESS_KEY, accessKey);
        }
        if (!TextUtils.isEmpty(signature)) {
            builder.addHeader(SIGNATURE, signature);
        }
        builder.post(body);
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String postdatawithAuth(String url, String json, String accessKey, String signature, String token) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (!TextUtils.isEmpty(signature)) {
            builder.addHeader(SIGNATURE, signature);
        }
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(OAUTH, "Bearer " + token);
        }
        builder.post(body);
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String formPost(JSONObject formdata) {
        String type = null, url = null;
        try {
            type = formdata.getString("type");
            url = formdata.getString("url");
        if (TextUtils.equals(type, "elligibility")) {
            String email = formdata.getString("email");
            String amount = formdata.getString("amount");
            String mobile = formdata.getString("mobile");

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("amount", amount)
                    .add("mobile", mobile)
                    .add("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        else if (TextUtils.equals(type, "initpay")) {
            String txnId = formdata.getString("txnid");
            String amount = formdata.getString("amount");
            RequestBody formBody = new FormBody.Builder()
                    .add("txnId", txnId)
                    .add("amount", amount)
                    .add("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();

        }

        else if (TextUtils.equals(type, "autodebit")) {
            String txnId = formdata.getString("txnid");
            String amount = formdata.getString("amount");
            RequestBody formBody = new FormBody.Builder()
                    .add("txnId", txnId)
                    .add("amount", amount)
                    .add("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        else if (TextUtils.equals(type, "otpsign")) {
            String txnref = formdata.getString("txnref");
            RequestBody formBody = new FormBody.Builder()
                    .add("txnref", txnref)
                    .add("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        else {
            return "wrong signature type";
        }

        } catch (JSONException e) {
            e.printStackTrace();
            return "incorrect values passed";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
