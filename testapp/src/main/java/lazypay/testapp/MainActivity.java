package lazypay.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import lazypay.app.Lazypay;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, Lazypay.class);

        intent.putExtra("email", "shardul@authme.io");

        intent.putExtra("mobile", "9591953812");

        intent.putExtra("amount", "1.00");

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
}
