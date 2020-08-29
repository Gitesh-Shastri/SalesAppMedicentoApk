package com.salesappmedicento.actvity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.gson.reflect.TypeToken;
import com.salesappmedicento.FetchMedicineService;
import com.salesappmedicento.JsonParser;
import com.salesappmedicento.NewArea;
import com.salesappmedicento.NewPharmacy;
import com.salesappmedicento.R;
import com.salesappmedicento.SalesPersonDetails;
import com.salesappmedicento.fragments.PlaceOrderAreaFragment;
import com.salesappmedicento.fragments.PlaceOrderSelectMedicineFragment;
import com.salesappmedicento.helperData.Constants;
import com.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.salesappmedicento.networking.data.Medicine;
import com.salesappmedicento.networking.data.SalesArea;
import com.salesappmedicento.networking.data.SalesPerson;
import com.salesappmedicento.networking.data.SalesPharmacy;
import com.salesappmedicento.networking.util.MedicentoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

import static android.view.View.GONE;
import static com.salesappmedicento.MedicentoUtils.isMyServiceRunning;

public class PlaceOrderNewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PlaceOrderAreaFragment.OnPlaceOrderChangeListener, PlaceOrderSelectMedicineFragment.OnPlaceOrderAreaChangeListener {

    public static ArrayList<Medicine> mMedicineDataList;
    SalesPerson salesPerson;

    public static int code;

    public static Menu menu;

    int Count;

    Spinner dateSpinner, slotSpinner;

    String versionUpdate, strcode;

    Gson gson;

    boolean isLoading;

    RadioGroup radioGroup;

    NavigationView mNavigationView;
    ArrayList<SalesArea> mSalesAreaDetails;
    ArrayList<SalesPharmacy> mSalesPharmacyDetails;

    SharedPreferences mSharedPreferences;
    BottomSheetBehavior sheetBehavior;

    Snackbar mSnackbar;

    boolean isInOrder;

    CardView cardView;

    Button choose_slot;

    FragmentManager mFragmentManager;


    public static String url1 = "https://medicento-api.herokuapp.com/pharma/updateSalesApp";

    String[] salesPersonDetailsLabel = {
            "Total Sales  : ₹ ",
            "Total Orders :",
            "Returns : ",
            "Earnings : ₹ "
    };

    Toolbar mToolbar;
    View mLoadingWaitView;
    ProgressBar mProgressBar;
    Button next, prev;

    CoordinatorLayout coordinatorLayout;

    SalesPharmacy salesPharmacy;
    JSONObject activityObject;

    Button total_sales, no_of_orders, returns, earnings, profile;

    PlaceOrderAreaFragment placeOrderAreaFragment;
    PlaceOrderSelectMedicineFragment placeOrderSelectMedicineFragment;

    String slot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_new);

        activityObject = new JSONObject();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        cardView = findViewById(R.id.card_slot);
        choose_slot = findViewById(R.id.choose);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mProgressBar = findViewById(R.id.area_pharma_fetch_progress);
        mLoadingWaitView = findViewById(R.id.loading_wait_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        dateSpinner = findViewById(R.id.day_spinner);
        slotSpinner = findViewById(R.id.slot_spinner);

        total_sales = findViewById(R.id.total_sales);
        no_of_orders = findViewById(R.id.no_of_orders);
        returns = findViewById(R.id.returns);
        earnings = findViewById(R.id.earnings);
        profile = findViewById(R.id.profile);

        choose_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dateSpinner.getSelectedItem().toString();
                String slot1 = slotSpinner.getSelectedItem().toString();
                slot = date + slot1;
                if (placeOrderSelectMedicineFragment.mOrderedMedicineAdapter.getList().size() > 0) {
                    placeOrderSelectMedicineFragment.placeOrder(date + slot1);
                    cardView.setVisibility(GONE);
                } else {
                    Toast.makeText(PlaceOrderNewActivity.this, "Please Select Some Medicine First", Toast.LENGTH_SHORT).show();
                    cardView.setVisibility(GONE);
                }
            }
        });

        gson = new Gson();

        addSalesPersonDetailsToNavDrawer();

        mProgressBar.setVisibility(GONE);
        prev.setVisibility(GONE);
        mLoadingWaitView.setVisibility(GONE);

        placeOrderAreaFragment = new PlaceOrderAreaFragment();
        placeOrderAreaFragment.setAreaPharmaSelectListener(this);
        placeOrderSelectMedicineFragment = new PlaceOrderSelectMedicineFragment();
        placeOrderSelectMedicineFragment.setAreaPharmaSelectListener(this);

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction().replace(R.id.main_container, placeOrderAreaFragment).commit();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (salesPharmacy != null) {
                    new GetMedicine().execute();
                    mFragmentManager.beginTransaction().replace(R.id.main_container, placeOrderSelectMedicineFragment).commit();
                } else {
                    Toast.makeText(PlaceOrderNewActivity.this, "Please Select Pharmacy", Toast.LENGTH_SHORT).show();
                }
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<SalesPharmacy> pharmacyList = new ArrayList<>();
                for (SalesPharmacy salesPharmacy : mSalesPharmacyDetails) {
                    if (mSalesAreaDetails.get(0).getId().equals(salesPharmacy.getAreaId())) {
                        pharmacyList.add(salesPharmacy);
                    }
                }
                mFragmentManager.beginTransaction().replace(R.id.main_container, placeOrderAreaFragment).commit();
                new GetNames().execute();
            }
        });

        String json = mSharedPreferences.getString("savedArea", null);
        String jsonP = mSharedPreferences.getString("savedPharma", null);
        String jsonM = mSharedPreferences.getString("medicines_saved", null);
        Type type = new TypeToken<ArrayList<SalesArea>>() {
        }.getType();
        Type type1 = new TypeToken<ArrayList<SalesPharmacy>>() {
        }.getType();
        Type type2 = new TypeToken<ArrayList<Medicine>>() {
        }.getType();
        mSalesAreaDetails = gson.fromJson(json, type);
        mSalesPharmacyDetails = gson.fromJson(jsonP, type1);
        mMedicineDataList = gson.fromJson(jsonM, type2);
        new GetNames().execute();
    }

    /*This method adds totalSales,orders, returns, earnings to
     *the nav menu of the app
     */
    private void addSalesPersonDetailsToNavDrawer() {
        String cache = Paper.book().read("user");
        if (cache != null && !cache.isEmpty()) {
            salesPerson = new Gson().fromJson(cache, SalesPerson.class);
            View headerView = mNavigationView.getHeaderView(0);
            TextView navHeaderSalesmanName = headerView.findViewById(R.id.username_header);
            TextView navHeaderSalesmanEmail = headerView.findViewById(R.id.user_email_header);
            navHeaderSalesmanName.setText(salesPerson.getName());
            navHeaderSalesmanEmail.setText(salesPerson.getUsercode());
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sign_out) {
            clearUserDetails();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }  else  if (id == R.id.dash) {
            isInOrder = false;
            mFragmentManager.beginTransaction().replace(R.id.main_container, placeOrderAreaFragment).commit();
            new GetNames().execute();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_proceed) {
            if (isInOrder) {
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String todayAsString = dateFormat.format(today);

                String date = todayAsString;
                String slot1 = "Till 7 pm";
                slot = date + slot1;
                if (placeOrderSelectMedicineFragment.mOrderedMedicineAdapter.getList().size() > 0) {
                    placeOrderSelectMedicineFragment.placeOrder(date + slot1);
                    cardView.setVisibility(GONE);
                } else {
                    Toast.makeText(PlaceOrderNewActivity.this, "Please Select Some Medicine First", Toast.LENGTH_SHORT).show();
                    cardView.setVisibility(GONE);
                }
            } else {
                return true;
            }
            
//            if(isInOrder) {
//                Date today, tomorrow;
//                cardView.setVisibility(View.VISIBLE);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//                String str = sdf.format(new Date());
//
//                if((7 < Integer.valueOf(str.substring(0,2)))&&(Integer.valueOf(str.substring(0,2)) <= 18)){
//                    Calendar calendar = Calendar.getInstance();
//                    today = calendar.getTime();
//
//                    calendar.add(Calendar.DAY_OF_YEAR, 1);
//                    tomorrow = calendar.getTime();
//
//
//                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//
//                    String todayAsString = dateFormat.format(today);
//                    String tomorrowAsString = dateFormat.format(tomorrow);
//
//                    ArrayList<String> dates = new ArrayList<>();
//                    dates.add(todayAsString);
//                    dates.add(tomorrowAsString);
//
//                    ArrayAdapter<String> dates_adapter = new ArrayAdapter<String>(this,
//                            android.R.layout.simple_spinner_item, dates);
//
//                    dateSpinner.setAdapter(dates_adapter);
//
//                    dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            if(i == 0) {
//                                ArrayList<String> slots = new ArrayList<>();
//                                slots.add(" Till 7 PM");
//
//
//                                ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderNewActivity.this,
//                                        android.R.layout.simple_spinner_item, slots);
//
//                                slotSpinner.setAdapter(slots_adapter);
//                            } else {
//                                ArrayList<String> slots = new ArrayList<>();
//                                slots.add(" Till 1 PM");
//                                slots.add(" Till 7 PM");
//
//
//                                ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderNewActivity.this,
//                                        android.R.layout.simple_spinner_item, slots);
//
//                                slotSpinner.setAdapter(slots_adapter);
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });
//
//                } else {
//
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.DAY_OF_YEAR, 1);
//                    today = calendar.getTime();
//
//                    calendar.add(Calendar.DAY_OF_YEAR, 1);
//                    tomorrow = calendar.getTime();
//
//
//                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
//
//                    String todayAsString = dateFormat.format(today);
//                    String tomorrowAsString = dateFormat.format(tomorrow);
//
//                    ArrayList<String> dates = new ArrayList<>();
//                    dates.add(todayAsString);
//                    dates.add(tomorrowAsString);
//
//                    ArrayAdapter<String> dates_adapter = new ArrayAdapter<String>(this,
//                            android.R.layout.simple_spinner_item, dates);
//
//                    dateSpinner.setAdapter(dates_adapter);
//
//                    ArrayList<String> slots = new ArrayList<>();
//                    slots.add(" Till 1 PM");
//                    slots.add(" Till 7 PM");
//
//
//                    ArrayAdapter<String> slots_adapter = new ArrayAdapter<String>(PlaceOrderNewActivity.this,
//                            android.R.layout.simple_spinner_item, slots);
//
//                    slotSpinner.setAdapter(slots_adapter);
//
//                }
//                //       placeOrderSelectMedicineFragment.placeOrder();
//            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPharmaSelected(SalesPharmacy salesPharmacy) {
        isInOrder = true;
        placeOrderSelectMedicineFragment.addPharmacy(salesPharmacy, salesPerson.getId());
        if (!isMyServiceRunning(FetchMedicineService.class, this)) {
            new GetMedicine().execute();
        }
        mFragmentManager.beginTransaction().replace(R.id.main_container, placeOrderSelectMedicineFragment).commit();
    }

    @Override
    public void onOrderPlaced(OrderedMedicineAdapter adapter, String[] output, SalesPharmacy salesPharmacy) {
        isInOrder = false;
        Intent intent = new Intent(this, OrderConfirmed.class);
        intent.putExtra("id", output[0]);
        intent.putExtra("date", slot);
        intent.putExtra("pharmacy", salesPharmacy.getPharmacyName());
        intent.putExtra("orderDetails", adapter.getList());
        intent.putExtra("slots", slot);
        startActivity(intent);
    }


    private class GetNames extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PlaceOrderNewActivity.this);
            progressDialog.setMessage("Loading Please Wait");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            RequestQueue requestQueue = Volley.newRequestQueue(PlaceOrderNewActivity.this);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    Constants.BASE_URL + "pharma/area/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Gitesh", "Response from url: " + response);
                            try {
                                JSONObject baseObject = new JSONObject(response);
                                mSalesAreaDetails = new ArrayList<>();
                                JSONArray areasArray = baseObject.getJSONArray("areas");
                                for (int i = 0; i < areasArray.length(); i++) {
                                    JSONObject areaObject = areasArray.optJSONObject(i);
                                    mSalesAreaDetails.add(new SalesArea(
                                            areaObject.getString("area_name"),
                                            areaObject.getString("area_city"),
                                            areaObject.getString("area_state"),
                                            areaObject.getString("area_pincode"),
                                            areaObject.getString("_id")
                                    ));
                                }
                                mSalesPharmacyDetails = new ArrayList<>();
                                JSONArray pharmaArray = baseObject.getJSONArray("pharmas");
                                for (int i = 0; i < pharmaArray.length(); i++) {
                                    JSONObject areaObject = pharmaArray.optJSONObject(i);
                                    mSalesPharmacyDetails.add(new SalesPharmacy(
                                            areaObject.getString("pharma_name"),
                                            areaObject.getString("pharma_address"),
                                            areaObject.getString("_id"),
                                            areaObject.getString("area")
                                    ));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(PlaceOrderNewActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                            Gson gson = new Gson();
                            String json = gson.toJson(mSalesAreaDetails);
                            String json1 = gson.toJson(mSalesPharmacyDetails);
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("savedArea", json);
                            editor.putString("savedPharma", json1);
                            editor.apply();
                            placeOrderAreaFragment.addAreaAndPharma(mSalesAreaDetails, mSalesPharmacyDetails, PlaceOrderNewActivity.this);
                            progressDialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(PlaceOrderNewActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            requestQueue.add(stringRequest);
            return null;

//            JsonParser sh = new JsonParser();
//            if (mSalesPharmacyDetails == null || mSalesAreaDetails == null) {
//                String url = Constants.BASE_URL + "pharma/area";
//                String jsonStr = sh.makeServiceCall(url);
//                Log.e("Gitesh", "Response from url: " + jsonStr);
//                if (jsonStr != null) {
//                    try {
//                        JSONObject baseObject = new JSONObject(jsonStr);
//                            mSalesAreaDetails = new ArrayList<>();
//                            JSONArray areasArray = baseObject.getJSONArray("areas");
//                            for (int i = 0; i < areasArray.length(); i++) {
//                                JSONObject areaObject = areasArray.optJSONObject(i);
//                                mSalesAreaDetails.add(new SalesArea(
//                                        areaObject.getString("area_name"),
//                                        areaObject.getString("area_city"),
//                                        areaObject.getString("area_state"),
//                                        areaObject.getString("area_pincode"),
//                                        areaObject.getString("_id")
//                                ));
//                            }
//                            mSalesPharmacyDetails = new ArrayList<>();
//                            JSONArray pharmaArray = baseObject.getJSONArray("pharmas");
//                            for (int i = 0; i < pharmaArray.length(); i++) {
//                                JSONObject areaObject = pharmaArray.optJSONObject(i);
//                                mSalesPharmacyDetails.add(new SalesPharmacy(
//                                        areaObject.getString("pharma_name"),
//                                        areaObject.getString("pharma_address"),
//                                        areaObject.getString("_id"),
//                                        areaObject.getString("area")
//                                ));
//                            }
//                    } catch (final JSONException e) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(PlaceOrderNewActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                        Log.e("Gitesh", "Json parsing error: " + e.getMessage());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(),
//                                        "Json parsing error: " + e.getMessage(),
//                                        Toast.LENGTH_LONG)
//                                        .show();
//                            }
//                        });
//                    }
//                } else {
//                    Log.e("Gitesh", "Couldn't get json from server.");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(),
//                                    "Couldn't get json from server. Check LogCat for possible errors!",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                }
//            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private class GetMedicine extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            isLoading = true;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            JsonParser sh = new JsonParser();

            String url = Constants.MEDICINE_DATA_URL;
            if (mMedicineDataList == null) {
                mMedicineDataList = new ArrayList<>();
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);
                Log.e("Gitesh", "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray medicine = jsonObj.getJSONArray("products");
                        mMedicineDataList = new ArrayList<>();
                        // looping through All Contacts
                        for (int i = 0; i < medicine.length(); i++) {
                            JSONObject c = medicine.getJSONObject(i);
                            mMedicineDataList.add(new Medicine(
                                    c.getString("medicento_name"),
                                    c.getString("company_name"),
                                    c.getInt("price"),
                                    c.getString("_id"),
                                    c.getInt("stock"),
                                    c.getString("item_code")
                            ));
                        }
                    } catch (final JSONException e) {
                        Toast.makeText(PlaceOrderNewActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isLoading = false;
            Gson gson = new Gson();
            ArrayList<String> medicineList = new ArrayList<>();
            for (Medicine medicine : mMedicineDataList) {
                medicineList.add(medicine.getMedicentoName());
            }
            String json = gson.toJson(mMedicineDataList);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("medicines_saved", json);
            editor.apply();
            placeOrderSelectMedicineFragment.addMedicine(mMedicineDataList, PlaceOrderNewActivity.this);
            mProgressBar.setVisibility(View.GONE);
            mLoadingWaitView.setVisibility(View.GONE);
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
        final int[] count1 = new int[1];
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://medicento-api.herokuapp.com/pharma/updateSalesApp";
        StringRequest str = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Code1", response);
                            strcode = response;
                            JSONObject spo = new JSONObject(response);
                            JSONArray version = spo.getJSONArray("Version");
                            for (int i = 0; i < version.length(); i++) {
                                JSONObject v = version.getJSONObject(i);
                                versionUpdate = v.getString("version");
                            }
                            String versionName = Constants.VERSION;
                            if (!versionUpdate.equals("1.0.1")) {
                                alertDialogForUpdate();
                            }
                            code = spo.getInt("code");
                            count1[0] = spo.getInt("count");
                            int count = mSharedPreferences.getInt("count", 0);
                            if (code == 101 && count <= spo.getInt("count")) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putInt("count", count1[0] + 1);
                                editor.apply();
                                if (!isLoading) {
                                    new GetNames().execute();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("error_coce", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderNewActivity.this);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_orders, menu);
        return true;
    }



}
