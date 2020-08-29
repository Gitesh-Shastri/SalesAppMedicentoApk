package com.salesappmedicento.actvity;

import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.salesappmedicento.R;
import com.salesappmedicento.helperData.Constants;
import com.salesappmedicento.networking.SalesDataLoader;
import com.salesappmedicento.networking.data.SalesPerson;
import com.salesappmedicento.recover;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity{

    EditText mEmailEditTv, mPasswordEditTv;
    TextView forget;
    String mUserCode, mUserPassWord;
    private final static int RC_SIGN_IN = 2;

    Button sign;

    SalesPerson sp;
    ProgressBar mProgressBar;
    Animation mAnimation;
    ImageView mLogo;

    RequestQueue requestQueue;

    RelativeLayout relativeLayout;

    Snackbar snackbar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mLogo = findViewById(R.id.medicento_logo);
        forget = findViewById(R.id.forgetpassword);
        sign = findViewById(R.id.createaccount);
        Button acc = findViewById(R.id.createaccount);
        mEmailEditTv = findViewById(R.id.email_edit_tv);
        Button btn = findViewById(R.id.sign_in_btn);
        mProgressBar = findViewById(R.id.sign_in_progress);
        mProgressBar.setVisibility(View.GONE);
        mPasswordEditTv = findViewById(R.id.password_edit_tv);
        relativeLayout = findViewById(R.id.signInLayout);

        Paper.init(this);

        requestQueue = Volley.newRequestQueue(this);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserCode = mEmailEditTv.getText().toString();
                mUserPassWord = mPasswordEditTv.getText().toString();
                if(!amIConnect()) {
                    return;
                }
                if (mUserCode.isEmpty()&&mUserPassWord.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter data for sign in!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                snackbar = Snackbar.make(relativeLayout, "Please Wait Checking Details ...", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
                loginUser();
            }
        });


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgetCode();
                /*
                Intent i = new Intent(SignInActivity.this,recover.class);
                SignInActivity.this.finish();
                startActivity(i);*/
            }
        });
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(2000);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mLogo.startAnimation(mAnimation);
    }


    private void forgetCode() {

        final Dialog dialog = new Dialog(SignInActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_pharma_code);

        final EditText phone = dialog.findViewById(R.id.email);

        Button submit = dialog.findViewById(R.id.submit);

        Button back = dialog.findViewById(R.id.back);
        Button create = dialog.findViewById(R.id.create);

        dialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(phone.getText().toString().isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please Provide Email / Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        "https://medicento-api.herokuapp.com/user/forget?email=" + phone.getText().toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if(jsonObject.getString("message").equals("User Found")) {

                                        final Dialog dialog1 = new Dialog(SignInActivity.this);
                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog1.setContentView(R.layout.congrats);

                                        TextView email = dialog1.findViewById(R.id.phone);
                                        email.setText("  Your Registered Mobile Number: "+jsonObject.getString("phone"));

                                        TextView code = dialog1.findViewById(R.id.code);
                                        code.setText("Your password: "+jsonObject.getString("password"));

                                        Button back1 = dialog1.findViewById(R.id.back);
                                        back1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog1.show();

                                    } else {

                                        final Dialog dialog1 = new Dialog(SignInActivity.this);
                                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog1.setContentView(R.layout.no_user_found);

                                        Button retry, login;
                                        retry = dialog1.findViewById(R.id.cancel);
                                        login = dialog1.findViewById(R.id.login);

                                        retry.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                            }
                                        });

                                        login.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog1.show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignInActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignInActivity.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                    }
                });

                requestQueue.add(stringRequest);
            }
        });



    }

    private void loginUser() {

        String url = Constants.BASE_URL + "sales_person_login/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        snackbar.dismiss();
                        try {
                            JSONObject baseObject = new JSONObject(response);
                            if(baseObject.has("message") && baseObject.getString("message").equals("User Logged In")) {
                                JSONObject user = baseObject.getJSONObject("salesPerson");
                                sp = new SalesPerson(user.getString("name"),
                                        user.getLong("Total_sales"),
                                        user.getInt("No_of_order"),
                                        user.getInt("Returns"),
                                        user.getLong("Earnings"),
                                        user.getInt("id")+"",
                                        user.getInt("area")+"",
                                        "123"
                                        );
                                onLoadFinished();
                            } else {
                                snackbar = Snackbar.make(relativeLayout, "Invalid Details!", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            snackbar = Snackbar.make(relativeLayout, "JSON Parsing Error ", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            if (error == null || error.networkResponse == null) {
                                return;
                            }
                            String body;
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Log.e("error_message", "showVolleyError: " + body);
                            } catch (UnsupportedEncodingException e) {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mProgressBar.setVisibility(View.GONE);
                        snackbar.dismiss();
                        snackbar = Snackbar.make(relativeLayout, "Something Went Wrong  Try Again .. ", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<>();
                params.put("user_name", mUserCode);
                params.put("password", mUserPassWord);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }


    public void onLoadFinished() {
            sp.setUsercode(mUserCode);
            Paper.book().write("user", new Gson().toJson(sp));
            Intent intent = new Intent(this, PharmacySelectionActivity.class);
            startActivity(intent);
            finish();
    }

    private boolean amIConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}