package com.salesappmedicento.networking;

import com.salesappmedicento.networking.data.GetMedicineResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://stage.medicento.com:8080/";

    @GET("distributor/get_distributor_medicines_sales/")
    Call<GetMedicineResponse> getMedicineResponseCall();

    @GET("distributor/get_distributor_medicines/")
    Call<GetMedicineResponse> getMedicineResponseCall(@Query("query") String query, @Query("page_no") String page_no);
}
