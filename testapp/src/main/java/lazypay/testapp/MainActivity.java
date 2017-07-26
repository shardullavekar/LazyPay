package lazypay.testapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lazypay.app.Lazypay;

public class MainActivity extends AppCompatActivity {
    private final static int READ_SMS_PERMISSION = 11;

    EditText email, mobile;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) this.findViewById(R.id.email);

        mobile = (EditText) this.findViewById(R.id.mobile);

        button = (Button) this.findViewById(R.id.lazypay);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLazyPay(email.getText().toString(), mobile.getText().toString(), "1.00");
            }
        });

        askforSMSPermission();
    }

    private void callLazyPay(String email, String mobile, String amount) {
        Intent intent = new Intent(MainActivity.this, Lazypay.class);

        intent.putExtra("email", email);

        intent.putExtra("mobile", mobile);

        intent.putExtra("amount", amount);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                switch (resultCode) {
                    case Lazypay.LAZYPAY_SUCCESS :
                        Toast.makeText(getApplicationContext(), "Transaction Successfull", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case Lazypay.LAZYPAY_FAILED:
                        Toast.makeText(getApplicationContext(), "Transaction Failed", Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void askforSMSPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(getApplicationContext(), "SMS Permission for enable auto OTP reading", Toast.LENGTH_LONG)
                        .show();

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSION);
            }
        }

    }

}
