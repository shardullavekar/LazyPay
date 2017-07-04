package lazypay.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import lazypay.app.API.Eligibility;
import lazypay.app.API.Initiate;

public class Lazypay extends AppCompatActivity {
    ApplicationInfo app;

    Bundle bundle;

    String accessKey, email, mobile, amountstr;

    JSONObject address, userDetails, amount;

    JSONArray productDetails;

    WebView webView;

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

        userDetails = new JSONObject();

        try {
            userDetails.put("email", email);
            userDetails.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initStaticjsons();

        checkEligibility();

        setContentView(R.layout.activity_lazypay);

        webView = (WebView) this.findViewById(R.id.lazypaywebview);

        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void checkEligibility() {
        Eligibility eligibility = new Eligibility();

        JSONObject jsonObject = getEligibilityJson();

        Signature signature = new Signature();

        eligibility.check(new Callback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean txnEligibility = jsonResponse.getBoolean("txnEligibility");
                    boolean userEligibility = jsonResponse.getBoolean("userEligibility");
                    String code = jsonResponse.getString("code");

                    processEligibility(txnEligibility, userEligibility, code);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, jsonObject, accessKey, signature.eligibilitySign(email, mobile, amountstr));
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

        String merchanttxnId = UUID.randomUUID().toString();

        JSONObject jsonObject = getInitPayJson(merchanttxnId);

        Signature signature = new Signature();

        initiatePay.init(new Callback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    String code = jsonResponse.getJSONArray("paymentModes").toString();

                    if (code.contains("CREDIT_CARD")) {
                        String weburl = jsonResponse.getString("checkoutPageUrl");
                        processCheckout(weburl);
                    }

                    if (code.contains("OTP") || code.contains("AUTO_DEBIT")) {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, jsonObject, accessKey, signature.initPaysign(accessKey, merchanttxnId, amountstr));
    }

    private void processCheckout(String url) {
        webView.loadUrl(url);
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

    private JSONObject getInitPayJson(String merchanttxnId) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("userDetails", userDetails);
            jsonObject.put("amount", amount);
            jsonObject.put("address", address);
            jsonObject.put("source", R.string.app_name);
            jsonObject.put("productSkuDetails", productDetails);
            jsonObject.put("isRedirectFlow", false);
            jsonObject.put("returnUrl", "https:test");
            jsonObject.put("notifyUrl", "https:test");

            jsonObject.put("merchantTxnId", merchanttxnId);
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
            productone.put("description", "description");
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
