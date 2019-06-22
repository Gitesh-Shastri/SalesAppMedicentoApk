package com.developer.medicento.salesappmedicento.actvity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.medicento.salesappmedicento.JsonParser;
import com.developer.medicento.salesappmedicento.R;
import com.developer.medicento.salesappmedicento.helperData.RecentOrderAdapter;
import com.developer.medicento.salesappmedicento.networking.data.RecentOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Recentorder extends AppCompatActivity  {
    ProgressDialog progressDialog;
    private static ArrayList<RecentOrder> mRecentOrder;
    TextView deli_id,deli_date;
    RelativeLayout rl,r2,r3;
    Button b1,b2,b3;
    SharedPreferences mSharedPreferences;
    String mOrderId, mDeliveryDate;
    private static String url = "https://medicento-api.herokuapp.com/product/recent_order/";

    RecyclerView recyclerView;

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_order);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRecentOrder = new ArrayList<>();
        mRecentOrder.add(new RecentOrder("10000011", "2018-11-28", 383, "Active"));
        mRecentOrder.add(new RecentOrder("10000010", "2018-11-15",  114, "Active"));
        mRecentOrder.add(new RecentOrder("10000003", "2018-11-14", 580, "Delivered"));
        mRecentOrder.add(new RecentOrder("10000005", "2018-11-15", 63, "Delivered"));

        recyclerView = findViewById(R.id.recentorders_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecentOrderAdapter recentOrderAdapter = new RecentOrderAdapter(this, mRecentOrder);
        recyclerView.setAdapter(recentOrderAdapter);
    }
    public class GetOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonParser sh = new JsonParser();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e("Gitesh", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray jsonArray = jsonObj.getJSONArray("orders");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject order = jsonArray.getJSONObject(i);
                        mRecentOrder.add(new RecentOrder(order.getString("_id"),
                                order.getString("created_at"),
                                order.getInt("grand_total")
                        ));

                    }
                } catch (final JSONException e) {
                    Toast.makeText(Recentorder.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Gitesh", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("Gitesh", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}
