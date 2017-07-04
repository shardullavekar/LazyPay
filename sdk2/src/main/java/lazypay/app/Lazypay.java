package lazypay.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lazypay.app.API.Eligibility;
import lazypay.app.API.Initiate;

public class Lazypay extends AppCompatActivity {
    ApplicationInfo app;

    Bundle bundle;

    String accessKey, email, mobile, amountstr;

    JSONObject address, userDetails, amount;

    JSONArray productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = null;
        try {
            app = getApplicationContext().getPackageManager()
                  .getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bundle = app.metaData;

        accessKey = bundle.getString(Config.ACCESS_KEY_NAME);

        email = getIntent().getStringExtra("email");

        mobile = getIntent().getStringExtra("mobile");

        amountstr = getIntent().getStringExtra("amount");

        if (TextUtils.isEmpty(accessKey)) {
            Toast.makeText(getApplicationContext(), "Invalid AccessKey", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(amountstr)) {
            Toast.makeText(getApplicationContext(), "Empty email,mobile or amount", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        amount = new JSONObject();

        try {
            amount.put("value", amountstr);
            amount.put("currency", "INR");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkEligibility();

        initStaticjsons();

        userDetails = new JSONObject();

        try {
            userDetails.put("email", email);
            userDetails.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_lazypay);
    }

    private void checkEligibility() {
        Eligibility eligibility = new Eligibility();

        JSONObject jsonObject = getEligibilityJson();

        Log.d("BiggerJson", jsonObject.toString());

//        eligibility.check(new Callback() {
//            @Override
//            public void onResponse(String response) {
//                JSONObject jsonResponse = null;
//                try {
//                    jsonResponse = new JSONObject(response);
//                    boolean txnEligibility = jsonResponse.getBoolean("txnEligibility");
//                    boolean userEligibility = jsonResponse.getBoolean("userEligibility");
//                    String code = jsonResponse.getString("code");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, jsonObject, accessKey);
    }

    private void processEligibility(boolean txn, boolean user, String code) {
        if (txn && user) {
            if (TextUtils.equals(code, "LP_ELIGIBLE")) {

            }

            else if (TextUtils.equals(code, "LP_SIGNUP_AVAILABLE")) {
                initPay();
            }

            else {

            }
        }
    }

    private void initPay() {
        Initiate initiatePay = new Initiate();

        JSONObject jsonObject = new JSONObject();

        initiatePay.init(new Callback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    String code = jsonResponse.getJSONArray("paymentModes").toString();

                    if (TextUtils.equals(code, "CREDIT_CARD")) {
                        String weburl = jsonResponse.getString("checkoutPageUrl");
                        processCheckout(weburl);
                    }

                    if (TextUtils.equals(code, "OTP,AUTO_DEBIT")) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, jsonObject, accessKey);
    }

    private void processCheckout(String url) {

    }

    private JSONObject getEligibilityJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userDetails", userDetails);
            jsonObject.put("amount", amount);
            jsonObject.put("source", R.string.app_name);
            jsonObject.put("address", address);
            jsonObject.put("productSkuDetails", productDetails);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void initStaticjsons() {
        address = new JSONObject();
        productDetails = new JSONArray();

        JSONObject attributes = new JSONObject();

        JSONArray skus = new JSONArray();
        JSONObject skusJsonObject = new JSONObject();

        try {
            skusJsonObject.put("skuId", "sku1");
            skusJsonObject.put("price", 10);
            skusJsonObject.put("attributes", attributes);
            skus.put(skusJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            attributes.put("size", "30");
            attributes.put("color", "blue");
            address.put("street1", "street1");
            address.put("street2", "street2");
            address.put("city", "pune");
            address.put("state", "MH");
            address.put("country", "IND");
            address.put("zip", "411045");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject productone = new JSONObject();
        try {
            productone.put("productId", "prod4");
            productone.put("attributes",attributes);
            productone.put("imageUrl","www.google.com");
            productone.put("shippable",true);
            productone.put("skus", skus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        productDetails.put(productone);

    }
}
