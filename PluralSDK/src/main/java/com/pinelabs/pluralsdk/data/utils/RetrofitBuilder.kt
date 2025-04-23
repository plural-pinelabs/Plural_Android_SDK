package com.pinelabs.pluralsdk.data.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.pinelabs.pluralsdk.BuildConfig
import com.pinelabs.pluralsdk.data.ApiService
import com.pinelabs.pluralsdk.data.model.PaymentModeDeserialiser
import com.pinelabs.pluralsdk.utils.Constants.Companion.BASE_CHECKOUTBFF
import com.pinelabs.pluralsdk.utils.Constants.Companion.BASE_URL_PROD
import com.pinelabs.pluralsdk.utils.Constants.Companion.BASE_URL_QA
import com.pinelabs.pluralsdk.utils.Constants.Companion.BASE_URL_UAT
import com.pinelabs.pluralsdk.utils.Constants.Companion.HTTPS
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    val client = OkHttpClient()
    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val clientBuilder: OkHttpClient.Builder = createBuilder()

    val gson = GsonBuilder()
        .registerTypeAdapter(Any::class.java, PaymentModeDeserialiser())
        .create()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HTTPS + BASE_URL_PROD + BASE_CHECKOUTBFF)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder.build())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createBuilder(): OkHttpClient.Builder {

        val sha256QA = String(Base64.getDecoder().decode(BuildConfig.SHA256_QA))
        val sha256UAT = String(Base64.getDecoder().decode(BuildConfig.SHA256_UAT))
        val sha256PROD = String(Base64.getDecoder().decode(BuildConfig.SHA256_PROD))

        val certificatePinner_QA = CertificatePinner.Builder()
            .add(BASE_URL_QA, sha256QA)
            .build()

        val certificatePinner_UAT = CertificatePinner.Builder()
            .add(BASE_URL_UAT, sha256UAT)
            .build()

        val certificatePinner_PROD = CertificatePinner.Builder()
            .add(BASE_URL_PROD, sha256PROD)
            .build()

        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) clientBuilder.addInterceptor(interceptor)

        clientBuilder.certificatePinner(certificatePinner_QA)
        clientBuilder.certificatePinner(certificatePinner_UAT)
        clientBuilder.certificatePinner(certificatePinner_PROD)
        clientBuilder.connectTimeout(60, TimeUnit.SECONDS)

        clientBuilder.readTimeout(60, TimeUnit.SECONDS)

        clientBuilder.writeTimeout(60, TimeUnit.SECONDS)

        return clientBuilder
    }
}