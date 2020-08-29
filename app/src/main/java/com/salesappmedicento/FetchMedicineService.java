package com.salesappmedicento;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.salesappmedicento.networking.Api;
import com.salesappmedicento.networking.data.GetMedicineResponse;
import com.salesappmedicento.networking.data.Medicine;
import com.salesappmedicento.networking.data.MedicineResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchMedicineService extends Service {

    private static final String TAG = "FetchMedicine";

    private boolean isLoading;
    SharedPreferences mSharedPreferences;
    private ArrayList<Medicine> medicines;

    public FetchMedicineService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isLoading = false;
        new GetMedicine().execute();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");
        super.onTaskRemoved(rootIntent);
    }

    private class GetMedicine extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                        Gson gson = new Gson();

                        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String json = gson.toJson(medicines);
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("medicines_saved", json);
                        editor.apply();
                        Log.d("Data", "doInBackground: " + System.currentTimeMillis());

                        stopSelf();
                    }

                    @Override
                    public void onFailure(Call<GetMedicineResponse> call, Throwable t) {
                        Log.d("Data", "onFailure: " + Log.getStackTraceString(t));
                        isLoading = false;
                        stopSelf();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
