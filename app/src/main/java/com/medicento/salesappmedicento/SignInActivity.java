package com.medicento.salesappmedicento;

import android.app.Activity;
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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.medicento.salesappmedicento.helperData.Constants;
import com.medicento.salesappmedicento.networking.SalesDataLoader;
import com.medicento.salesappmedicento.networking.data.SalesPerson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.paperdb.Paper;

public class SignInActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    EditText mEmailEditTv, mPasswordEditTv;
    TextView forget;
    String mUserCode;
    private final static int RC_SIGN_IN = 2;

    SalesPerson sp;
    ProgressBar mProgressBar;
    Animation mAnimation;
    ImageView mLogo;
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
        Button acc = findViewById(R.id.createaccount);
        mEmailEditTv = findViewById(R.id.email_edit_tv);
        Button btn = findViewById(R.id.sign_in_btn);
        mProgressBar = findViewById(R.id.sign_in_progress);
        mProgressBar.setVisibility(View.GONE);
        mPasswordEditTv = findViewById(R.id.password_edit_tv);

        Paper.init(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserCode = mEmailEditTv.getText().toString();
                if(!amIConnect()) {
                    return;
                }
                if (isInputEmpty(mUserCode)&&isInputEmpty(mPasswordEditTv.getText().toString())) {
                    Toast.makeText(SignInActivity.this, "Please enter data for sign in!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                getLoaderManager().initLoader(Constants.LOG_IN_LOADER_ID, null, SignInActivity.this);
            }
        });


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this,recover.class);
                SignInActivity.this.finish();
                startActivity(i);
            }
        });
        mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(2000);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mLogo.startAnimation(mAnimation);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }



    private boolean isInputEmpty(String userCode) {
        return userCode.isEmpty();
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        mAnimation.setDuration(200);
        mLogo.startAnimation(mAnimation);
        if (id == Constants.LOG_IN_LOADER_ID) {
            Uri baseUri = Uri.parse(Constants.USER_LOGIN_URL);
            Uri.Builder builder = baseUri.buildUpon();

            builder.appendQueryParameter("email", mUserCode);
            builder.appendQueryParameter("password", mPasswordEditTv.getText().toString());

            return new SalesDataLoader(this, builder.toString(), getString(R.string.login_action));
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        mProgressBar.setVisibility(View.GONE);
        SalesPerson salesPerson = (SalesPerson) data;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (salesPerson != null) {
            salesPerson.setUsercode(mUserCode);
            Paper.book().write("user", new Gson().toJson(salesPerson));
            Intent intent = new Intent();
            if (getParent() == null) {
                setResult(Activity.RESULT_OK, intent);
            } else {
                getParent().setResult(RESULT_OK);
            }
            finish();
        } else {
            mEmailEditTv.setText("");
            Toast.makeText(this, "Invalid Details!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
    }
    private boolean amIConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}