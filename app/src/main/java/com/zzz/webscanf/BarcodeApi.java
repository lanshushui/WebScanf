package com.zzz.webscanf;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BarcodeApi {
    @GET("api/barcode/goods/details")
    Call<BarcodeBean> getBarcode(@Query("barcode") String barcode);
}
