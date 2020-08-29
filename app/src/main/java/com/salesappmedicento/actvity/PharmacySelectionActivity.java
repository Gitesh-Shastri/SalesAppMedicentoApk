package com.salesappmedicento.actvity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.salesappmedicento.FetchMedicineService;
import com.salesappmedicento.R;
import com.salesappmedicento.helperData.Constants;
import com.salesappmedicento.helperData.MyRecyclerViewAdapter;
import com.salesappmedicento.networking.data.SalesPerson;
import com.salesappmedicento.networking.data.SalesPharmacy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salesappmedicento.networking.util.MedicentoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static com.salesappmedicento.MedicentoUtils.isMyServiceRunning;

public class PharmacySelectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   MyRecyclerViewAdapter.ItemClickListener {

    ArrayList<SalesPharmacy> mSalesPharmacyDetails, tempSalesPharmacies;
    MyRecyclerViewAdapter pharmacyAreaArrayAdapter;
    SharedPreferences mSharedPreferences;
    SwipeRefreshLayout swipe_refresh;
    NavigationView mNavigationView;
    ProgressDialog progressDialog;
    ActionBarDrawerToggle toggle;
    SalesPharmacy salesPharmacy;
    RecyclerView pharmacy_rv;
    ProgressBar mProgressBar;
    SalesPerson salesPerson;
    SearchView searchView;
    String versionUpdate;
    DrawerLayout drawer;
    boolean is_loading;
    Toolbar mToolbar;
    Gson gson;
    JSONObject activityObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_selection);

        Paper.init(this);

        String cache = Paper.book().read("user");

        if (cache != null && !cache.isEmpty()) {

            activityObject = new JSONObject();

            is_loading = false;
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);

            initializeViews();

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            setSupportActionBar(mToolbar);

            toggle = new ActionBarDrawerToggle(
                    this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawer.addDrawerListener(toggle);
            toggle.syncState();

            mNavigationView.setNavigationItemSelectedListener(this);

            addSalesPersonDetailsToNavDrawer();

            checkCacheForPharmacies();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(mSalesPharmacyDetails != null
                            && tempSalesPharmacies != null
                            && tempSalesPharmacies.size() > 0 ) {
                        mSalesPharmacyDetails.clear();
                        for (SalesPharmacy salesPharmacy : tempSalesPharmacies) {
                            if (salesPharmacy.getPharmacyName().toLowerCase().startsWith(newText)) {
                                mSalesPharmacyDetails.add(salesPharmacy);
                            }
                        }
                        if (pharmacyAreaArrayAdapter != null) {
                            pharmacyAreaArrayAdapter.notifyDataSetChanged();
                        }
                    }
                    return true;
                }
            });
            swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(is_loading) {

                    } else {
                        tempSalesPharmacies.clear();
                        mSalesPharmacyDetails.clear();
                        pharmacyAreaArrayAdapter.notifyDataSetChanged();
                        new GetNames().execute();
                    }
                    swipe_refresh.setRefreshing(false);
                }
            });

            if (!isMyServiceRunning(FetchMedicineService.class, this)) {
                Context context = getApplicationContext();
                Intent intent1;
                intent1 = new Intent(context, FetchMedicineService.class);
                context.startService(intent1);
            }

        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        mProgressBar = findViewById(R.id.area_pharma_fetch_progress);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        mNavigationView = findViewById(R.id.nav_view);
        pharmacy_rv = findViewById(R.id.pharmacy_rv);
        drawer = findViewById(R.id.drawer_layout);
        searchView = findViewById(R.id.search);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar.setVisibility(View.GONE);
    }

    private void checkCacheForPharmacies() {
        gson = new Gson();

        String jsonP = mSharedPreferences.getString("savedPharmas", null);

        Type type1 = new TypeToken<ArrayList<SalesPharmacy>>() {
        }.getType();

        mSalesPharmacyDetails = gson.fromJson(jsonP, type1);
        tempSalesPharmacies = new ArrayList<>();

        if (mSalesPharmacyDetails == null) {
            Log.d("list is null", "list is null");
            new GetNames().execute();
        } else if (mSalesPharmacyDetails.size() == 0) {
            Log.d("list is empty", "list is empty");
            new GetNames().execute();
        } else {
            tempSalesPharmacies.addAll(mSalesPharmacyDetails);
            new GetNames().execute();
//            setRecyclerViewPharmacy();
        }
    }

    private void setRecyclerViewPharmacy() {
        pharmacy_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        pharmacyAreaArrayAdapter = new MyRecyclerViewAdapter(this, mSalesPharmacyDetails);
        pharmacy_rv.setAdapter(pharmacyAreaArrayAdapter);

        pharmacyAreaArrayAdapter.setClickListener(this);
    }

    /*This method adds totalSales,orders, returns, earnings to
     *the nav menu of the app
     */
    private void addSalesPersonDetailsToNavDrawer() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView navHeaderSalesmanName = headerView.findViewById(R.id.username_header);
        TextView navHeaderSalesmanEmail = headerView.findViewById(R.id.user_email_header);
        navHeaderSalesmanName.setText(salesPerson.getName());
        navHeaderSalesmanEmail.setText(salesPerson.getUsercode());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sign_out) {
            clearUserDetails();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        salesPharmacy = mSalesPharmacyDetails.get(position);
        startActivity(new Intent(this, MedicineSelectionActivity.class).putExtra("pharmacy", salesPharmacy));
    }

    private class GetNames extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            is_loading = true;
            progressDialog = new ProgressDialog(PharmacySelectionActivity.this);
            progressDialog.setMessage("Loading Pharmacies Please Wait");
            progressDialog.show();
            mSalesPharmacyDetails = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RequestQueue requestQueue = Volley.newRequestQueue(PharmacySelectionActivity.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    Constants.BASE_URL + "pharmacy/pharmacy_by_salesperson/?sales_id="+salesPerson.getId(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Gitesh", "Response from url: " + response);
                            if (mSalesPharmacyDetails == null) {
                                mSalesPharmacyDetails = new ArrayList<>();
                            }
                            mSalesPharmacyDetails.clear();
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                JSONArray pharmacy = jsonObject.getJSONArray("pharmacy");
                                for (int j=0;j<pharmacy.length();j++) {
                                    JSONObject area = pharmacy.getJSONObject(j);
                                    JSONArray pharmaArray = area.getJSONArray("pharmacy");
                                    for (int i = 0; i < pharmaArray.length(); i++) {
                                        JSONObject pharmacyObject = pharmaArray.optJSONObject(i);
                                        SalesPharmacy salesPharmacy = new SalesPharmacy(
                                                pharmacyObject.getString("name"),
                                                pharmacyObject.getString("address"),
                                                pharmacyObject.getString("id"),
                                                pharmacyObject.getInt("area") + ""
                                        );
                                        salesPharmacy.setmArea(area.getString("name"));
                                        salesPharmacy.setCreditsAvailable(10000);
                                        mSalesPharmacyDetails.add(salesPharmacy);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PharmacySelectionActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }

                            Gson gson = new Gson();

                            String json1 = gson.toJson(mSalesPharmacyDetails);

                            SharedPreferences.Editor editor = mSharedPreferences.edit();

                            editor.putString("savedPharmas", json1);
                            editor.apply();

                            if (mSalesPharmacyDetails.size() > 0) {
                                salesPharmacy = mSalesPharmacyDetails.get(0);
                                tempSalesPharmacies.clear();
                                tempSalesPharmacies.addAll(mSalesPharmacyDetails);
                                setRecyclerViewPharmacy();
                            }
                            is_loading = false;
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(PharmacySelectionActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
            );
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }


    private void clearUserDetails() {
        Paper.book().delete("user");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            activityObject.put("end_time", System.currentTimeMillis() + "");
            activityObject.put("android_id", androidId);
            if (salesPerson != null && salesPerson.getId() != null) {
                activityObject.put("sales_id", salesPerson.getId());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/api/app/record_activity/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        MedicentoUtils.showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.d("data", "getParams: " + activityObject.toString());
                try {
                    params.put("activity_name", activityObject.getString("activity_name"));
                    params.put("start_time", activityObject.getString("start_time"));
                    params.put("end_time", activityObject.getString("end_time"));
                    params.put("android_id", activityObject.getString("android_id"));
                    params.put("sales_id", activityObject.getString("sales_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest str = new StringRequest(
                Request.Method.GET,
                "http://stage.medicento.com:8080/api/app/get_app_versioncode_sales_list_status/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject spo = new JSONObject(response);
                            if (spo.has("app_version")) {
                                versionUpdate = spo.getString("app_version");
                                if (!versionUpdate.equals(Constants.VERSION)) {
                                    alertDialogForUpdate();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("json_exception", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(str);

        try {
            activityObject.put("activity_name", "PharmacySelection");
            activityObject.put("start_time", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void alertDialogForUpdate() {

        final Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.update_available);

        Button cancel, update;
        cancel = dialog1.findViewById(R.id.cancel);
        update = dialog1.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.salesappmedicento"
                                )
                        )
                );
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        dialog1.show();
    }

}
