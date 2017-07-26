package lazypay.app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import lazypay.app.API.AutoDebit;
import lazypay.app.API.Eligibility;
import lazypay.app.API.Initiate;
import lazypay.app.API.OTPay;
import lazypay.app.SMS.ReadSms;
import lazypay.app.SMS.SMSListener;
import lazypay.app.storage.Oauth;

public class Lazypay extends AppCompatActivity {

    public static final int LAZYPAY_SUCCESS = 10;

    public static final int LAZYPAY_FAILED = 20;

    private ProgressDialog dialog;

    private EditText smsEdit;

    private Button paybutton;

    ApplicationInfo app;

    Bundle bundle;

    String accessKey, email, mobile, amountstr, signatureUrl;

    JSONObject address, userDetails, amount, OtpPayment;

    JSONArray productDetails;

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = null;

        dialog = new ProgressDialog(Lazypay.this);

        try {
            app = getApplicationContext().getPackageManager()
                  .getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bundle = app.metaData;

        accessKey = bundle.getString(Config.ACCESS_KEY_NAME);

        signatureUrl = bundle.getString(Config.SIGNATURE_URL);

        email = getIntent().getStringExtra("email");

        mobile = getIntent().getStringExtra("mobile");

        amountstr = getIntent().getStringExtra("amount");

        if (TextUtils.isEmpty(accessKey)) {
            Toast.makeText(getApplicationContext(), "Invalid AccessKey", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(signatureUrl)) {
            Toast.makeText(getApplicationContext(), "Invalid Signature URL", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (!Config.isValidEmail(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(amountstr)) {
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

        smsEdit = (EditText) this.findViewById(R.id.smseditbox);

        paybutton = (Button) this.findViewById(R.id.paybutton);

        WebViewClient webViewClient = new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        };

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(webViewClient);

        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void checkEligibility() {
        final Eligibility eligibility = new Eligibility();

        final JSONObject jsonObject = getEligibilityJson();

        Signature signature = new Signature(new Callback() {
            @Override
            public void onResponse(String response) {
                eligibility.check(new Callback() {
                    @Override
                    public void onResponse(String response) {
                        dismissDialogue();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            boolean txnEligibility = jsonResponse.getBoolean("txnEligibility");
                            boolean userEligibility = jsonResponse.getBoolean("userEligibility");
                            String code = jsonResponse.getString("code");
                            String eligibiltyCode = jsonResponse.getString("eligibilityResponseId");

                            processEligibility(txnEligibility, userEligibility, code, eligibiltyCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            endActivity(LAZYPAY_FAILED);
                        }

                    }
                }, jsonObject, accessKey, response);
            }
        });

        signature.eligibilitySign(email, mobile, amountstr, signatureUrl);

        showDialogue();

    }

    private void processEligibility(boolean txn, boolean user, String code, String eligibilityCode) {
        if (txn && user) {
            if (TextUtils.equals(code, "LP_ELIGIBLE")) {
                Oauth oauth = new Oauth(getApplicationContext());

                String token = oauth.getToken();

                if (TextUtils.isEmpty(token)) {
                    initPay();
                }

                else {
                    processAutoDebit(token, eligibilityCode);
                }

            }

            else if (TextUtils.equals(code, "LP_SIGNUP_AVAILABLE")) {
                initPay();
            }

            else if (TextUtils.equals(code, "LP_USER_INELIGIBLE")) {
                endActivity(LAZYPAY_FAILED);
            }

        }
    }

    private void initPay() {
        final Initiate initiatePay = new Initiate();

        final String merchanttxnId = UUID.randomUUID().toString();

        final JSONObject jsonObject = getInitPayJson(merchanttxnId);

        Signature signature = new Signature(new Callback() {
            @Override
            public void onResponse(String signature) {
                dismissDialogue();
                initiatePay.init(new Callback() {
                    @Override
                    public void onResponse(String response) {
                        dismissDialogue();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                            String code = jsonResponse.getJSONArray("paymentModes").toString();

                            if (code.contains("CREDIT_CARD")) {
                                String weburl = jsonResponse.getString("checkoutPageUrl");
                                processCheckout(weburl);
                            }

                            if (code.contains("OTP") || code.contains("AUTO_DEBIT")) {

                                initSMSListener();

                                processOTP(jsonResponse.getString("txnRefNo"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, jsonObject, accessKey, signature);
                showDialogue();
            }
        });

        signature.initPaysign(merchanttxnId, amountstr, signatureUrl);

        showDialogue();


    }

    private void processCheckout(String url) {
        webView.loadUrl(url);
    }

    private void processOTP(String txnRefnum) {
        try {
            OtpPayment.put("paymentMode", "OTP");
            OtpPayment.put("txnRefNo", txnRefnum);

            if (ContextCompat.checkSelfPermission(Lazypay.this,
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                showSMSeditbox();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postOTPTxn(final JSONObject jsonObject) {
        final OTPay otPay = new OTPay();
        Signature signature = new Signature(new Callback() {
            @Override
            public void onResponse(String response) {
                dismissDialogue();
                showDialogue();
                otPay.pay(new Callback() {
                    @Override
                    public void onResponse(String response) {
                        dismissDialogue();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String token = jsonResponse.getString("token");
                            Oauth oauth = new Oauth(getApplicationContext());
                            oauth.storeToken(token);
                            endActivity(LAZYPAY_SUCCESS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, jsonObject, accessKey, response);
                showDialogue();

            }
        });

        try {
            signature.otpsign(OtpPayment.getString("txnRefNo"), signatureUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showDialogue();
    }

    private void processAutoDebit(final String token, String eligibilitycode) {

        final String merchantTxnid = UUID.randomUUID().toString();

        final JSONObject jsonObject = getAutoDebitJson(merchantTxnid, eligibilitycode);

        final AutoDebit autoDebit = new AutoDebit();

        Signature signature = new Signature(new Callback() {
            @Override
            public void onResponse(String response) {
                dismissDialogue();
                showDialogue();
                autoDebit.start(new Callback() {
                    @Override
                    public void onResponse(String response) {
                        dismissDialogue();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject jsonResponeData = jsonResponse.getJSONObject("responseData");
                            JSONObject autodebitJson = jsonResponeData.getJSONObject("AUTO_DEBIT");
                            String status = autodebitJson.getString("status");

                            if (TextUtils.equals(status, "SUCCESS")) {
                                endActivity(LAZYPAY_SUCCESS);
                            }

                            else {
                                endActivity(LAZYPAY_FAILED);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, jsonObject, accessKey, response, token);

            }
        });

        signature.autoDebitsign(merchantTxnid, amountstr, signatureUrl);
        showDialogue();

    }

    private void initSMSListener() {
        OtpPayment = new JSONObject();

        ReadSms.bindListener(new SMSListener() {
            @Override
            public void onOTPReceived(String otp) {
                try {
                    if (TextUtils.isEmpty(otp)) {
                        return;
                    }
                    OtpPayment.put("otp", otp);
                    postOTPTxn(OtpPayment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

    private JSONObject getAutoDebitJson(String merchantTxnid, String eligibilityCode) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("eligibilityResposneId", eligibilityCode);
            jsonObject.put("userDetails", userDetails);
            jsonObject.put("amount", amount);
            jsonObject.put("address", address);
            jsonObject.put("source", R.string.app_name);
            jsonObject.put("productSkuDetails", productDetails);
            jsonObject.put("merchantTxnId", merchantTxnid);
            jsonObject.put("redirectFlow", true);
            jsonObject.put("returnUrl", "https://test");
            jsonObject.put("notifyUrl", "https://test");
            jsonObject.put("paymentMode", "AUTO_DEBIT");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void showSMSeditbox() {
        smsEdit.setVisibility(View.VISIBLE);
        paybutton.setVisibility(View.VISIBLE);

        paybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OtpPayment.put("otp", smsEdit.getText().toString());
                    postOTPTxn(OtpPayment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialogue() {
        dialog.setMessage("Please wait..");
        dialog.show();
    }

    private void dismissDialogue() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    private void endActivity(int resultCode) {
        setResult(resultCode);
        Lazypay.this.finish();
    }

}
