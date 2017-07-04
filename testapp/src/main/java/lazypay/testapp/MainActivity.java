package lazypay.testapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        startActivity(intent);

    }
}
