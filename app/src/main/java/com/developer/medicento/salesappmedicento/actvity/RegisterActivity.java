package com.developer.medicento.salesappmedicento.actvity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developer.medicento.salesappmedicento.R;
import com.developer.medicento.salesappmedicento.networking.data.SalesArea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Spinner state, city;

    ProgressDialog pDialog;

    Button login, terms, create;

    EditText address, driving , pan, email, salesId,  password, phone, name;

    AlertDialog alert;

    ArrayList<SalesArea> areas;
    ArrayList<String> state_name, city_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        state = findViewById(R.id.stateSpinner);
        city = findViewById(R.id.citySpinner);

        login = findViewById(R.id.login);
        address = findViewById(R.id.address);
        driving = findViewById(R.id.driving);
        pan  = findViewById(R.id.pan);
        salesId = findViewById(R.id.salesId);
        password = findViewById(R.id.password);
        phone  = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        create = findViewById(R.id.create);

        terms = findViewById(R.id.terms);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(state_name.size() > 0) {
                    state_name.get(i);
                    city_name = new ArrayList<>();
                    for (SalesArea salesArea : areas) {
                        if (salesArea.getState().equals(state_name.get(i))) {
                            if (!city_name.contains(salesArea.getCity())) {
                                city_name.add(salesArea.getCity());
                            }
                        }
                    }

                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(RegisterActivity.this, R.layout.support_simple_spinner_dropdown_item, city_name);
                    city.setAdapter(arrayAdapter1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, SignInActivity.class));
                finish();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address.getText().toString().isEmpty()||
                        driving.getText().toString().isEmpty()||
                        pan.getText().toString().isEmpty()||
                        salesId.getText().toString().isEmpty()||
                        password.getText().toString().isEmpty()||
                        phone.getText().toString().isEmpty()||
                        name.getText().toString().isEmpty()
                ) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Details Properly", Toast.LENGTH_SHORT).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Creatin Please wait");
                progressDialog.show();
                RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        "https://retailer-app-api.herokuapp.com/user/saleslogin",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("email_from_response", response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle("Congratulation ");
                                    builder.setIcon(R.mipmap.ic_launcher_new);
                                    builder.setCancelable(false);
                                    builder.setMessage("You have Successfully Regeistered !! ")
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    alert.dismiss();
                                                }
                                            });
                                    alert = builder.create();
                                    alert.show();
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("email", error.getMessage());
                        Toast.makeText(RegisterActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to login url
                        Map<String, String> params = new HashMap<>();
                        params.put("pharma_name", name.getText().toString());
                        params.put("state", state.getSelectedItem().toString());
                        params.put("city", city.getSelectedItem().toString());
                        params.put("pharma_address", address.getText().toString());
                        params.put("phone", phone.getText().toString());
                        params.put("password", password.getText().toString());
                        params.put("pan", pan.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("driving", driving.getText().toString());
                        params.put("salesId", salesId.getText().toString());


                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        fetchArea();
    }


    private void fetchArea() {

        areas = new ArrayList<>();

        city_name = new ArrayList<>();
        state_name = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String  url = "https://retailer-app-api.herokuapp.com/area";

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading Area");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("area", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("areas");
                            for(int i=0; i<jsonArray.length();i++) {
                                JSONObject area = jsonArray.getJSONObject(i);
                                areas.add(new SalesArea(
                                        area.getString("area_name"),
                                        area.getString("area_city"),
                                        area.getString("area_state"),
                                        area.getString("area_pincode"),
                                        area.getString("_id")
                                ));
                            }
                            Log.d("size_of_array", ""+areas.size());
                            state_name.add(areas.get(0).getState());
                            for(SalesArea salesArea: areas) {
                                if(!state_name.contains(salesArea.getState())) {
                                    state_name.add(salesArea.getState());
                                }
                                if(salesArea.getState().equals(areas.get(0).getState())) {
                                    if(!city_name.contains(salesArea.getCity())) {
                                        city_name.add(salesArea.getCity());
                                    }
                                }
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterActivity.this, R.layout.support_simple_spinner_dropdown_item, state_name);
                            state.setAdapter(arrayAdapter);


                            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(RegisterActivity.this, R.layout.support_simple_spinner_dropdown_item, city_name);
                            city.setAdapter(arrayAdapter1);


                            pDialog.dismiss();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                        } catch (JSONException e) {
                            e.printStackTrace();

                            pDialog.dismiss();
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }
        );

        requestQueue.add(stringRequest);
    }


}
