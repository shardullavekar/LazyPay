package lazypay.app;

import android.text.TextUtils;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class Config {
    public static final String TEST = "https://sboxapi.lazypay.in/";
    public static final String PROD = "https://api.lazypay.in/";
    public static final String ACCESS_KEY_NAME = "in.sdk.lazypay";
    public static final String SIGNATURE_URL = "in.sdk.signature";


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
