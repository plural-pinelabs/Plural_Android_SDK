package com.pinelabs.plural_sdk_test_app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pinelabs.plural_sdk_test_app.api.ApiService
import com.pinelabs.plural_sdk_test_app.api.RetrofitInstance
import com.pinelabs.plural_sdk_test_app.api.model.MockData
import com.pinelabs.plural_sdk_test_app.api.model.OrderFailure
import com.pinelabs.plural_sdk_test_app.api.model.OrderResponse
import com.pinelabs.plural_sdk_test_app.api.model.TokenFailure
import com.pinelabs.plural_sdk_test_app.api.model.TokenResponse
import com.pinelabs.plural_sdk_test_app.utils.Constants.Companion.BEARER
import com.pinelabs.plural_sdk_test_app.utils.Constants.Companion.ORDER
import com.pinelabs.pluralsdk.PluralSDKManager
import com.pinelabs.pluralsdk.callback.PaymentResultCallBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class TestAppActivity : AppCompatActivity(), PaymentResultCallBack{

    private lateinit var apiInterface: ApiService

    private lateinit var btn_startPayment : Button

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_app)

        btn_startPayment = findViewById(R.id.btn_start_sdk)
        btn_startPayment.setOnClickListener {
            generateToken()
        }
    }

    private fun getApiInterface() {
        apiInterface = RetrofitInstance.getInstance().create(ApiService::class.java)
    }

    private fun generateToken(){
        getApiInterface()

        val call = apiInterface.generateToken(MockData.tokenRequest)
        call.enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {

                if (response.isSuccessful && response.body()!=null){
                    println("Token response ${response.body()!!.access_token}")
                    getOrderData(response.body()!!.access_token)
                } else {
                    try {
                        if (response.errorBody()!=null) {
                            val type = object : TypeToken<TokenFailure>() {}.type
                            var errorResponse: TokenFailure? = Gson().fromJson(response.errorBody()!!.charStream(), type)
                            println("Error response -> ${errorResponse!!.message}")
                            Toast.makeText(this@TestAppActivity, errorResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e:Exception) {
                        Toast.makeText(this@TestAppActivity, "Exception occured while generating token", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }

    private fun getOrderData(accessToken: String){

        var orderRequest = MockData.orderUAT
        orderRequest.merchant_order_reference = ORDER+ UUID.randomUUID().toString()

        val call = apiInterface.createOrder( "$BEARER $accessToken", orderRequest)
        call.enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {

                if (response.isSuccessful && response.body()!=null){
                    token = response.body()!!.token
                    PluralSDKManager().startPayment(this@TestAppActivity, token)
                } else {
                    try {
                        if (response.errorBody()!=null) {
                            val type = object : TypeToken<OrderFailure>() {}.type
                            var errorResponse: OrderFailure? = Gson().fromJson(response.errorBody()!!.charStream(), type)
                            println("Error response -> ${errorResponse!!.code}")
                            Toast.makeText(this@TestAppActivity, errorResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }catch (e:Exception) {
                        Toast.makeText(this@TestAppActivity, "Exception occured while creating order", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }

    override fun onErrorOccured(message: String?) {
        Toast.makeText(this@TestAppActivity, "Error in app ${message}", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessOccured() {
        Toast.makeText(this@TestAppActivity, "Yay Payment Successful", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionResponse() {
        Toast.makeText(this@TestAppActivity, "Success in app", Toast.LENGTH_SHORT).show()
    }

}