package lazypay.sdk.API;

import android.content.Context;

import lazypay.sdk.Callback;

/**
 * Created by shardullavekar on 03/07/17.
 */

public class Eligibility {
    private static final String url = "api/lazypay/v0/payment/eligibility";

    Context context;

    public Eligibility(Context context) {
        this.context = context;
    }

    public void check(Callback callback) {

    }
}
