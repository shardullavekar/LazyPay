package lazypay.app.storage;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shardullavekar on 04/07/17.
 */

public class Oauth {

    Context context;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    private static final String OAUTH = "OauthToken";

    public Oauth(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(OAUTH, 0);
        editor = sharedPreferences.edit();
    }

    public void storeToken(String token) {
        editor.putString("token", token);
        editor.commit();
        editor.apply();
    }

    public String getToken() {
        String token = sharedPreferences.getString("token", null);

        try {
            JSONObject jsonObject = new JSONObject(token);
            return jsonObject.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }


}
