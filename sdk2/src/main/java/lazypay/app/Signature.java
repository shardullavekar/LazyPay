package lazypay.app;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by shardullavekar on 04/07/17.
 */

public class Signature {

    public Signature() {

    }

    public String eligibilitySign(String email, String mobile, String amount) {
        String hashbefore = mobile+email+amount + "INR";
        try {
            return Config.hmacSha1(hashbefore);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String initPaysign(String accessKey, String txnId, String amount) {
        String hashbefore = "merchantAccessKey=" + accessKey + "&" + "transactionId=" + txnId + "&"
                + "amount=" + amount;
        try {
            return Config.hmacSha1(hashbefore);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }

    }

    public String autoDebitsign(String accessKey, String txnId, String amount) {
        return initPaysign(accessKey, txnId, amount);
    }



}
