package com.developer.medicento.salesappmedicento.actvity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.developer.medicento.salesappmedicento.R;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        String cache = Paper.book().read("user");

        Log.d("in_main", "in_main");

        if (cache != null && !cache.isEmpty()) {
            Intent intent = new Intent(this, PlaceOrderNewActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("in_main", "in_main");
        Paper.init(this);

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {
            Intent intent = new Intent(this, PlaceOrderNewActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}
