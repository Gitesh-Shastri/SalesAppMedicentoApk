package com.salesappmedicento.actvity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.salesappmedicento.JsonParser;
import com.salesappmedicento.R;
import com.salesappmedicento.helperData.AutoCompleteAdapter;
import com.salesappmedicento.helperData.Constants;
import com.salesappmedicento.helperData.CountDrawable;
import com.salesappmedicento.helperData.MyRecyclerViewAdapter;
import com.salesappmedicento.helperData.OrderedMedicine;
import com.salesappmedicento.helperData.OrderedMedicineAdapter;
import com.salesappmedicento.networking.Api;
import com.salesappmedicento.networking.SalesDataLoader;
import com.salesappmedicento.networking.data.GetMedicineResponse;
import com.salesappmedicento.networking.data.Medicine;
import com.salesappmedicento.networking.data.MedicineAuto;
import com.salesappmedicento.networking.data.MedicineResponse;
import com.salesappmedicento.networking.data.SalesPerson;
import com.salesappmedicento.networking.data.SalesPharmacy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salesappmedicento.networking.util.JsonUtils;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static com.salesappmedicento.networking.util.MedicentoUtils.showVolleyError;

public class MedicineSelectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OrderedMedicineAdapter.OverallCostChangeListener,
        LoaderManager.LoaderCallbacks<Object> {

    OrderedMedicineAdapter mOrderedMedicineAdapter;
    AutoCompleteTextView autoCompleteTextView;
    SharedPreferences mSharedPreferences;
    SwipeRefreshLayout swipe_refresh;
    NavigationView mNavigationView;
    ActionBarDrawerToggle toggle;
    SalesPharmacy salesPharmacy;
    SalesPerson salesPerson;
    DrawerLayout drawer;
    boolean is_loading;
    Toolbar mToolbar;
    Gson gson;

    TextView mTotalTv, cartSub,
            pharmaName;

    ArrayList<Medicine> medicines;
    AutoCompleteAdapter medicineAdapter;
    RecyclerView mOrderedMedicinesListView;
    Animation mAnimation;

    ArrayList<MedicineAuto> medicineAuto;

    boolean isLoading;

    ProgressDialog progressDialog;

    String slot, date;

    JSONObject pharmacy_and_saved_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_selection);

        Paper.init(this);

        if (getIntent() != null && getIntent().hasExtra("pharmacy")) {
            String cache = Paper.book().read("user");
            if (cache != null && !cache.isEmpty()) {
                initializeViews();

                medicines = new ArrayList<>();
                medicineAuto = new ArrayList<>();

                salesPharmacy = (SalesPharmacy) getIntent().getSerializableExtra("pharmacy");

                pharmaName.setText(getString(R.string.selected_pharmacy, salesPharmacy.getPharmacyName()));

                is_loading = false;
                salesPerson = new Gson().fromJson(cache, SalesPerson.class);

                String pharmacy_and_saved_items_cache = Paper.book().read("pharmacy_and_saved_items");

                if (pharmacy_and_saved_items_cache != null && !pharmacy_and_saved_items_cache.isEmpty()) {
                    try {
                        pharmacy_and_saved_items = new JSONObject(pharmacy_and_saved_items_cache);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pharmacy_and_saved_items = new JSONObject();
                    Paper.book().write("pharmacy_and_saved_items", pharmacy_and_saved_items.toString());
                }

                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                setSupportActionBar(mToolbar);
                toggle = new ActionBarDrawerToggle(
                        this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

                drawer.addDrawerListener(toggle);
                toggle.syncState();

                mNavigationView.setNavigationItemSelectedListener(this);

                addSalesPersonDetailsToNavDrawer();

                swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (is_loading) {
                        } else {
                            new GetMedicine().execute();
                        }
                        swipe_refresh.setRefreshing(false);
                    }
                });

                cartSub.setText(getString(R.string.cart_subtotal_0_items, 0 + ""));
                setCount(MedicineSelectionActivity.this);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        autoCompleteTextView.setText("");
                        Medicine medicine = null;
                        for (Medicine med : medicines) {
                            if (med.getMedicentoName().equals(medicineAdapter.getItem(position).getName())) {
                                medicine = med;
                                break;
                            }
                        }
                        if (mOrderedMedicineAdapter.checkMedicineQuantity(medicine)) {
                            final Dialog dialog1 = new Dialog(MedicineSelectionActivity.this);
                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog1.setContentView(R.layout.maximum_limit);

                            Button back1 = dialog1.findViewById(R.id.okay);
                            back1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                }
                            });

                            dialog1.show();
                            return;
                        }
                        float cost = Float.parseFloat(mTotalTv.getText().toString().substring(1));
                        float overall = cost + (medicine != null ? medicine.getPrice() : 0);
                        if (salesPharmacy.getCreditsAvailable() < overall) {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(MedicineSelectionActivity.this);
                            builder.setMessage("Credits Limit Exceed For this pharmacy")
                                    .setCancelable(false)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Credit Limit Exceeded");
                            alert.show();
                        } else {
                            mOrderedMedicineAdapter.add(new OrderedMedicine(medicine.getMedicentoName(),
                                    medicine.getCompanyName(),
                                    1,
                                    medicine.getPrice(),
                                    medicine.getCode(),
                                    medicine.getPrice(),
                                    medicine.getMstock(),
                                    medicine.getPacking(),
                                    medicine.getMrp(),
                                    medicine.getOffer_qty(),
                                    medicine.getDiscount(),
                                    medicine.getOffer_qty()));
                            mTotalTv.setText(String.format("₹%.2f", overall));
                            cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
                            setCount(MedicineSelectionActivity.this);
                            mOrderedMedicinesListView.smoothScrollToPosition(0);
                        }
                    }
                });
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                            viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = (int) viewHolder.itemView.getTag();
                        mOrderedMedicineAdapter.remove(pos);
                        cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
                        setCount(MedicineSelectionActivity.this);
                    }
                }).attachToRecyclerView(mOrderedMedicinesListView);
                mAnimation = new AlphaAnimation(1, 0);
                mAnimation.setDuration(200);
                mAnimation.setInterpolator(new LinearInterpolator());
                mAnimation.setRepeatCount(Animation.INFINITE);
                mAnimation.setRepeatMode(Animation.REVERSE);

                String jsonM = mSharedPreferences.getString("medicines_saved", null);

                Type type = new TypeToken<ArrayList<Medicine>>() {
                }.getType();

                gson = new Gson();

                medicines = gson.fromJson(jsonM, type);
                setMedicineAdapter();

                if (medicines == null) {
//                    new GetMedicine().execute();
                } else if (medicines.size() == 0) {
//                    new GetMedicine().execute();
                } else {

                }

            } else {
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(this, PharmacySelectionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        autoCompleteTextView = findViewById(R.id.medicine_edit_tv);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        pharmaName = findViewById(R.id.pharmacy_edit_tv);
        mNavigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        mTotalTv = findViewById(R.id.total_cost);
        mToolbar = findViewById(R.id.toolbar);
        cartSub = findViewById(R.id.tv2);
        mOrderedMedicinesListView = findViewById(R.id.ordered_medicines_list);
    }

    private void setMedicineAdapter() {

        if (medicines != null) {
            for (Medicine med : medicines) {
                medicineAuto.add(new MedicineAuto(med.getMedicentoName(), med.getCompanyName(), med.getOffer_qty(), med.getMstock() + "", med.getPacking(), med.getPrice()));
            }
        }

        medicineAdapter = new AutoCompleteAdapter(this, medicineAuto);
        autoCompleteTextView.setAdapter(medicineAdapter);
        autoCompleteTextView.setEnabled(true);

        if (mOrderedMedicineAdapter == null) {

            mOrderedMedicineAdapter = new OrderedMedicineAdapter(new ArrayList<OrderedMedicine>());

            mOrderedMedicineAdapter.setContext(this);
            mOrderedMedicinesListView.setLayoutManager(new LinearLayoutManager(this));
            mOrderedMedicinesListView.setHasFixedSize(true);
            mOrderedMedicinesListView.setAdapter(mOrderedMedicineAdapter);
            mOrderedMedicineAdapter.setOverallCostChangeListener(this);

            Gson gson = new Gson();
            try {
                final String json = pharmacy_and_saved_items.getString(salesPharmacy.getId());
                Type type = new TypeToken<ArrayList<OrderedMedicine>>() {
                }.getType();
                ArrayList<OrderedMedicine> medicineDataList = gson.fromJson(json, type);
                if (medicineDataList != null) {
                    if (mOrderedMedicineAdapter != null) {
                        mOrderedMedicineAdapter.reset();
                        for (OrderedMedicine orderedMedicine : medicineDataList) {
                            mOrderedMedicineAdapter.add(orderedMedicine);
                        }
                        mOrderedMedicineAdapter.setOverallCostChangeListener(this);
                        float cost = 0;
                        for (OrderedMedicine medicine : mOrderedMedicineAdapter.getList()) {
                            cost += medicine.getQty() * medicine.getRate();
                        }
                        mTotalTv.setText(String.format("₹%.2f", cost));
                        cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
                        setCount(MedicineSelectionActivity.this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
        } else if (id == R.id.dash) {
            Intent intent = new Intent(this, PharmacySelectionActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void clearUserDetails() {
        Paper.book().delete("user");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCostChanged(float newCost, int qty, String type) {
        float cost = 0;
        for (OrderedMedicine medicine : mOrderedMedicineAdapter.getList()) {
            cost += medicine.getQty() * medicine.getRate();
        }
        mTotalTv.setText(String.format("₹%.2f", cost));
        cartSub.setText(getString(R.string.cart_subtotal_0_items, mOrderedMedicineAdapter.getItemCount() + ""));
        setCount(this);
    }

    private class GetMedicine extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MedicineSelectionActivity.this);
            progressDialog.setMessage("Loading Medicines Please Wait");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (!isLoading) {

                isLoading = true;
                medicines = new ArrayList<>();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Api api = retrofit.create(Api.class);
                Log.d("Data", "doInBackground: " + System.currentTimeMillis());
                Call<GetMedicineResponse> getMedicineResponseCall = api.getMedicineResponseCall();

                getMedicineResponseCall.enqueue(new Callback<GetMedicineResponse>() {
                    @Override
                    public void onResponse(Call<GetMedicineResponse> call, retrofit2.Response<GetMedicineResponse> response) {

                        try {
                            GetMedicineResponse getMedicineResponse = response.body();

                            for (MedicineResponse medicineResponse : getMedicineResponse.getMedicineResponses()) {
                                if (medicineResponse.getQty() > 0) {
                                    medicines.add(new Medicine(medicineResponse.getItem_name(),
                                            medicineResponse.getManfc_name(),
                                            medicineResponse.getPtr(),
                                            medicineResponse.getId() + "",
                                            medicineResponse.getItem_code(),
                                            medicineResponse.getQty(),
                                            medicineResponse.getPacking(),
                                            medicineResponse.getMrp(),
                                            medicineResponse.getScheme(),
                                            medicineResponse.getDiscount(),
                                            medicineResponse.getOffer_qty()
                                    ));
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isLoading = false;

                        medicineAuto.clear();
                        Gson gson = new Gson();

                        String json = gson.toJson(medicines);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("savedMedicines", json);
                        editor.apply();
                        setMedicineAdapter();
                        progressDialog.hide();

                        Log.d("Data", "doInBackground: " + System.currentTimeMillis());
                    }

                    @Override
                    public void onFailure(Call<GetMedicineResponse> call, Throwable t) {
                        Log.d("Data", "onFailure: " + Log.getStackTraceString(t));


                        isLoading = false;

                        Gson gson = new Gson();

                        String json = gson.toJson(medicines);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("isMyServiceRunning", json);
                        editor.apply();
                        setMedicineAdapter();
                        progressDialog.hide();
                    }
                });
            }
//            JsonParser sh = new JsonParser();
//            String url = Constants.MEDICINE_DATA_URL;
//            String jsonStr = sh.makeServiceCall(url);
//            Log.e("Gitesh", "Response from url: " + jsonStr);
//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//
//                    // Getting JSON Array node
//                    JSONArray medicine = jsonObj.getJSONArray("products");
//
//                    for (int i = 0; i < medicine.length(); i++) {
//                        JSONObject c = medicine.getJSONObject(i);
//                        medicines.add(new Medicine(
//                                c.getString("medicento_name"),
//                                c.getString("company_name"),
//                                c.getInt("price"),
//                                c.getString("_id"),
//                                c.getString("item_code"),
//                                c.getInt("stock"),
//                                c.getString("packing"),
//                                c.getString("discount"),
//                                c.getString("offer_qty")
//                        ));
//
//                        medicineResponses.add(new Medicine(medicineResponse.getItem_name(),
//                                medicineResponse.getManfc_name(),
//                                medicineResponse.getPtr(),
//                                medicineResponse.getId() + "",
//                                medicineResponse.getItem_code(),
//                                medicineResponse.getQty(),
//                                medicineResponse.getPacking(),
//                                medicineResponse.getMrp(),
//                                medicineResponse.getScheme(),
//                                medicineResponse.getDiscount(),
//                                medicineResponse.getOffer_qty()
//                        ));
//                    }
//                } catch (final JSONException e) {
//                    Log.e("Gitesh", "Json parsing error: " + e.getMessage());
//                    Toast.makeText(MedicineSelectionActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
//                }
//                if (medicines.size() > 0) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            setMedicineAdapter();
//                        }
//                    });
//                }
//            } else {
//                Log.e("Gitesh", "Couldn't get json from server.");
//                Toast.makeText(getApplicationContext(),
//                        "server error. Check LogCat for possible errors!",
//                        Toast.LENGTH_LONG)
//                        .show();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    String order_id = "";

    private boolean isPlacingOrder = false;

    private void placeOrder() {

        RequestQueue requestQueue = Volley.newRequestQueue(MedicineSelectionActivity.this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                "http://stage.medicento.com:8080/orders/place_order/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = JsonUtils.getJsonValueFromKey(jsonObject, "message");
                            String order_id = JsonUtils.getJsonValueFromKey(jsonObject, "order_id");

                            if (message.equals("Order Placed")) {

                                try {
                                    pharmacy_and_saved_items.put(salesPharmacy.getId(), "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Paper.book().write("pharmacy_and_saved_items", pharmacy_and_saved_items.toString());

                                Intent intent = new Intent(MedicineSelectionActivity.this, OrderConfirmed.class);
                                intent.putExtra("id", order_id);
                                intent.putExtra("date", date);
                                intent.putExtra("pharmacy", salesPharmacy.getPharmacyName());
                                intent.putExtra("orderDetails", mOrderedMedicineAdapter.getList());
                                intent.putExtra("slots", slot);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(MedicineSelectionActivity.this, "Order Cannot Be Placed Try Again.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MedicineSelectionActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                        }

                        isPlacingOrder = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        isPlacingOrder = false;
                        Toast.makeText(MedicineSelectionActivity.this, "Error In The Network.", Toast.LENGTH_SHORT).show();
                        showVolleyError(error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                JSONObject jsonObject = new JSONObject();
                JSONArray orderItems = new JSONArray();
                try {
                    for (OrderedMedicine orderedMedicine : mOrderedMedicineAdapter.getList()) {
                        JSONObject object = new JSONObject();
                        object.put("medicine_name", orderedMedicine.getMedicineName());
                        object.put("company_name", orderedMedicine.getMedicineCompany());
                        object.put("Itemcode", orderedMedicine.getCode());
                        object.put("Quantity", orderedMedicine.getQty());
                        object.put("price", orderedMedicine.getRate());
                        object.put("cost", orderedMedicine.getCost());
                        object.put("mrp", orderedMedicine.getMrp());
                        try {
                            object.put("scheme", orderedMedicine.getOffer_qty());
                        } catch (Exception e) {
                            object.put("scheme", "-");
                            e.printStackTrace();
                        }
                        orderItems.put(object);
                    }

                    jsonObject.put("items", orderItems);
                    params.put("order_items", jsonObject.toString());
                    params.put("pharmacy_id", salesPharmacy.getId());
                    params.put("sales_id", salesPerson.getId());

                    String order_cache = Paper.book().read("order_id");
                    if (order_cache != null && !order_cache.isEmpty()) {
                        order_id = order_cache;
                        params.put("order_id", order_id);
                    }

                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);

                    Date today;
                    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    today = calendar.getTime();
                    slot = "Till 1PM";
                    if (hour >= 8) {
                        if (hour < 16) {
                            slot = "Till 7PM";
                        } else {
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            today = calendar.getTime();
                        }
                    }
                    date = dateFormat.format(today);

                    params.put("date", date);
                    params.put("slot_time", slot);
                    params.put("source", "Sales App");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        if (!isPlacingOrder) {

            progressDialog = new ProgressDialog(MedicineSelectionActivity.this);
            progressDialog.setMessage("Placing Order Please Wait");
            progressDialog.show();

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    3,
                    2));
            requestQueue.add(stringRequest);
        }
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String todayAsString = dateFormat.format(today);

        String date = todayAsString;
        String slot1 = "Till 7-pm";
        slot = date + " " + slot1;

        Log.i("orderPlacing", mOrderedMedicineAdapter.getList().get(0).getCode());

        progressDialog = new ProgressDialog(MedicineSelectionActivity.this);
        progressDialog.setMessage("Placing Order Please Wait");
        progressDialog.show();

        return new SalesDataLoader(this, Constants.PLACE_ORDER_URL, getString(R.string.place_order_action), salesPharmacy.getId(), salesPerson.getId(), slot, mOrderedMedicineAdapter.getList());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        progressDialog.hide();

        String[] output = (String[]) data;

        Intent intent = new Intent(this, OrderConfirmed.class);
        intent.putExtra("id", output[0]);
        intent.putExtra("date", slot);
        intent.putExtra("pharmacy", salesPharmacy.getPharmacyName());
        intent.putExtra("orderDetails", mOrderedMedicineAdapter.getList());
        intent.putExtra("slots", slot);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_proceed) {
            if (mOrderedMedicineAdapter.getList().size() > 0) {
//                getLoaderManager().initLoader(Constants.PLACE_ORDER_LOADER_ID, null, this);

                placeOrder();
            } else {
                Toast.makeText(MedicineSelectionActivity.this, "Please Select Some Medicine First", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_orders, menu);
        this.menu = menu;
        setCount(MedicineSelectionActivity.this);
        return true;
    }

    public void setCount(Context context) {
        try {
            MenuItem menuItem = menu.findItem(R.id.action_proceed);
            LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

            CountDrawable badge;

            // Reuse drawable if possible
            Drawable reuse = icon.findDrawableByLayerId(R.id.counter);
            if (reuse != null && reuse instanceof CountDrawable) {
                badge = (CountDrawable) reuse;
            } else {
                badge = new CountDrawable(context);
            }

            badge.setCount(mOrderedMedicineAdapter.getItemCount() + "");
            icon.mutate();
            icon.setDrawableByLayerId(R.id.counter, badge);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mOrderedMedicineAdapter != null) {

            String json = new Gson().toJson(mOrderedMedicineAdapter.getList());
            try {
                pharmacy_and_saved_items.put(salesPharmacy.getId(), json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Paper.book().write("pharmacy_and_saved_items", pharmacy_and_saved_items.toString());
            Log.d("data", "setCount: " + pharmacy_and_saved_items.toString());
        }
    }
}
