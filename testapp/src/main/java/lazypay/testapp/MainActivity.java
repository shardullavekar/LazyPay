package lazypay.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lazypay.app.Lazypay;

public class MainActivity extends AppCompatActivity {
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
                callLazyPay();
            }
        });

    }

    private void callLazyPay() {
        Intent intent = new Intent(MainActivity.this, Lazypay.class);

        intent.putExtra("email", email.getText().toString());

        intent.putExtra("mobile", mobile.getText().toString());

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
