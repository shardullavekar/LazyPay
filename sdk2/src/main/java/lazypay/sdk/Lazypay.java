package lazypay.sdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import lazypay.sdk.API.Eligibility;

public class Lazypay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkEligibility();

        setContentView(R.layout.activity_lazypay);
    }

    private void checkEligibility() {
        Eligibility eligibility = new Eligibility(getApplicationContext());
        eligibility.check(new Callback() {
            @Override
            public void onResponse(String response) {
                //handle if else cases here
            }
        });
    }
}
