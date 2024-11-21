package com.pinelabs.plural_sdk_test_app.api

import com.pinelabs.plural_sdk_test_app.api.model.OrderRequest
import com.pinelabs.plural_sdk_test_app.api.model.OrderResponse
import com.pinelabs.plural_sdk_test_app.api.model.TokenRequest
import com.pinelabs.plural_sdk_test_app.api.model.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServiceMock {

    @GET("585e699c-9078-4983-b811-9ad24e429ef3")
    fun generateToken(): Call<TokenResponse>

    @GET("e3377b7c-ad00-45d3-808e-9b66bb6f2316")
    fun createOrder(/*@Header("Authorization") authHeader:String, @Body request: OrderRequest*/): Call<OrderResponse>

}