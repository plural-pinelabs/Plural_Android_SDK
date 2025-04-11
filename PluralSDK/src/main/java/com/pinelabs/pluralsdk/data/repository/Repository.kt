package com.pinelabs.pluralsdk.data.repository

import android.content.Context
import com.pinelabs.pluralsdk.data.model.BinResponse
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequest
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequestList
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponse
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.CustomerInfoResponse
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.OTPResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.model.RewardResponse
import com.pinelabs.pluralsdk.data.model.SavedCardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import com.pinelabs.pluralsdk.data.utils.toResultFlow
import kotlinx.coroutines.flow.Flow

class Repository(private val remoteDataSource: RemoteDataSource) {

    suspend fun fetchData(context: Context, token: String?): Flow<NetWorkResult<FetchResponse>> {
        return toResultFlow(context) {
            remoteDataSource.fetchData(token)
        }
    }

    suspend fun processPayment(
        context: Context,
        token: String?,
        paymentData: ProcessPaymentRequest?
    ): Flow<NetWorkResult<ProcessPaymentResponse>> {
        return toResultFlow(context) {
            remoteDataSource.processPayment(token, paymentData)
        }
    }

    suspend fun rewardPayment(
        context: Context,
        token: String,
        rewardData: RewardRequest
    ): Flow<NetWorkResult<RewardResponse>> {
        return toResultFlow(context) {
            remoteDataSource.reward(token, rewardData)
        }
    }

    suspend fun transactionStatus(
        context: Context,
        token: String?
    ): Flow<NetWorkResult<TransactionStatusResponse>> {
        return toResultFlow(context) {
            remoteDataSource.transactionStatus(token)
        }
    }

    suspend fun cancelTransaction(
        context: Context,
        token: String,
        cancelPayment: Boolean
    ): Flow<NetWorkResult<CancelTransactionResponse>> {
        return toResultFlow(context) {
            remoteDataSource.cancelTransaction(token, cancelPayment)
        }
    }

    suspend fun binData(
        context: Context,
        token: String,
        cardData: CardBinMetaDataRequestList
    ): Flow<NetWorkResult<CardBinMetaDataResponse>> {
        return toResultFlow(context) {
            remoteDataSource.binData(token, cardData)
        }
    }

    suspend fun generateOTP(
        context: Context,
        token: String,
        otpRequest: OTPRequest
    ): Flow<NetWorkResult<OTPResponse>> {
        return toResultFlow(context) {
            remoteDataSource.generateOTP(token, otpRequest)
        }
    }

    suspend fun submitOTP(
        context: Context,
        token: String,
        otpRequest: OTPRequest
    ): Flow<NetWorkResult<OTPResponse>> {
        return toResultFlow(context) {
            remoteDataSource.submitOTP(token, otpRequest)
        }
    }

    suspend fun resendOTP(
        context: Context,
        token: String,
        otpRequest: OTPRequest
    ): Flow<NetWorkResult<OTPResponse>> {
        return toResultFlow(context) {
            remoteDataSource.resendOTP(token, otpRequest)
        }
    }

    suspend fun sendOTPCustomer(
        context: Context,
        token: String?,
        otpRequest: OTPRequest?
    ): Flow<NetWorkResult<SavedCardResponse>> {
        return toResultFlow(context) {
            remoteDataSource.sendOTPCustomer(token, otpRequest)
        }
    }

    suspend fun validateOTPCustomer(
        context: Context,
        token: String?,
        otpRequest: OTPRequest?
    ): Flow<NetWorkResult<SavedCardResponse>> {
        return toResultFlow(context) {
            remoteDataSource.validateOTPCustomer(token, otpRequest)
        }
    }

    suspend fun createInactive(
        context: Context,
        token: String?,
        customerInfo: CustomerInfo?
    ): Flow<NetWorkResult<CustomerInfo>> {
        return toResultFlow(context) {
            remoteDataSource.createInactive(token, customerInfo)
        }
    }

    suspend fun validateUpdateOrder(
        context: Context,
        token: String?,
        otpRequest: OTPRequest?
    ): Flow<NetWorkResult<CustomerInfoResponse>> {
        return toResultFlow(context) {
            remoteDataSource.validateUpdateOrder(token, otpRequest)
        }
    }

}