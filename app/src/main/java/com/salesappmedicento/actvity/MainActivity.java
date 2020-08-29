package com.salesappmedicento.actvity;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.salesappmedicento.R;
import com.salesappmedicento.networking.data.SalesPerson;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.salesappmedicento.MedicentoUtils.showVolleyError;

public class MainActivity extends AppCompatActivity {

    private String token = "";
    private SalesPerson salesPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            token = task.getResult().getToken();

                            sentFirebaseToken();
                        }
                    });
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
            Intent intent = new Intent(this, PharmacySelectionActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Paper.init(this);
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            Intent intent = new Intent(this, PharmacySelectionActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    private void sentFirebaseToken() {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String finalAndroidId = androidId;
        StringRequest stringRequest1 = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/register_device/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dev_id", finalAndroidId+"sales");
                params.put("reg_id", token);
                params.put("sales_id", salesPerson.getId());
                return params;
            }
        };
        requestQueue.add(stringRequest1);

    }
}
