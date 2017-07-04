package lazypay.app;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class Config {
    public static final String TEST = "https://sboxapi.lazypay.in/";
    public static final String PROD = "https://api.lazypay.in/";
    public static final String ACCESS_KEY_NAME = "in.sdk.lazypay";

    private static final String SECRET_KEY = "becec050531547703395a6f2c43c7cf7e34bb74f";


    public static String sha1(String s) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((SECRET_KEY).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));

        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
