package com.pinelabs.pluralsdk.data.repository

import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequest
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequestList
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.utils.RetrofitBuilder

class RemoteDataSource() {
    suspend fun fetchData(token: String?) = RetrofitBuilder.apiService.fetchData(token)
    suspend fun processPayment(token: String, paymentData: ProcessPaymentRequest) =
        RetrofitBuilder.apiService.processPayment(token, paymentData)

    suspend fun reward(token: String, rewardData: RewardRequest) =
        RetrofitBuilder.apiService.checkReward(token, rewardData)

    suspend fun transactionStatus(token: String?) =
        RetrofitBuilder.apiService.statusOfTransaction(token)

    suspend fun cancelTransaction(token: String) =
        RetrofitBuilder.apiService.cancelTransaction(token, true)

    suspend fun binData(token: String, cardData: CardBinMetaDataRequestList) =
        RetrofitBuilder.apiService.getBinData(token, cardData)

    suspend fun generateOTP(token: String, otpRequest: OTPRequest) =
        RetrofitBuilder.apiService.initiateOTP(token, otpRequest)

    suspend fun submitOTP(token: String, otpRequest: OTPRequest) =
        RetrofitBuilder.apiService.submitOTP(token, otpRequest)

    suspend fun resendOTP(token: String, otpRequest: OTPRequest) =
        RetrofitBuilder.apiService.resendOTP(token, otpRequest)

    suspend fun sendOTPCustomer(token: String?, otpRequest: OTPRequest?) =
        RetrofitBuilder.apiService.sendOTPCustomer(token, otpRequest)

    suspend fun validateOTPCustomer(token: String?, otpRequest: OTPRequest?) =
        RetrofitBuilder.apiService.validateOTPCustomer(token, otpRequest)

    suspend fun createInactive(token: String?, customerInfo: CustomerInfo?) =
        RetrofitBuilder.apiService.createInactive(token, customerInfo)

    suspend fun validateUpdateOrder(token: String?, otpRequest: OTPRequest?) =
        RetrofitBuilder.apiService.validateUpdateOrder(token, otpRequest)

}