package lazypay.sdk;

/**
 * Created by shardullavekar on 03/07/17.
 */

public interface LazypayResult {
    void onSuccess(String response);
    void onFailure(String response);
}
