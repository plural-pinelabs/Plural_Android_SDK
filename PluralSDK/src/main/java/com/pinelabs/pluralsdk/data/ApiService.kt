package com.pinelabs.pluralsdk.data

import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("fetch/data")
    suspend fun fetchData(@Query("token", encoded = true) token:String): Response<FetchResponse>

    @POST("process/payment")
    suspend fun processPayment  (@Query("token", encoded = true) token:String, @Body request: ProcessPaymentRequest): Response<ProcessPaymentResponse>
}