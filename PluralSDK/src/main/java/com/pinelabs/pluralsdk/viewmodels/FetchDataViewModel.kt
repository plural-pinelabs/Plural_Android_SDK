package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pinelabs.pluralsdk.data.model.BinResponse
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequest
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataRequestList
import com.pinelabs.pluralsdk.data.model.CardBinMetaDataResponse
import com.pinelabs.pluralsdk.data.model.CustomerInfo
import com.pinelabs.pluralsdk.data.model.CustomerInfoResponse
import com.pinelabs.pluralsdk.data.model.DCCDetails
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.OTPResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.model.RewardResponse
import com.pinelabs.pluralsdk.data.model.SavedCardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.repository.Repository
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import com.pinelabs.pluralsdk.data.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.coroutines.CoroutineContext


class FetchDataViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    private val fetchDataResponse: MutableLiveData<NetWorkResult<FetchResponse>> = MutableLiveData()
    private val responseProcessPayment: MutableLiveData<NetWorkResult<ProcessPaymentResponse>> =
        MutableLiveData()
    private val rewardDataPayment: MutableLiveData<NetWorkResult<RewardResponse>> =
        MutableLiveData()
    private val transactionStatus: MutableLiveData<NetWorkResult<TransactionStatusResponse>?> =
        MutableLiveData()
    private val cancelTransaction: MutableLiveData<NetWorkResult<CancelTransactionResponse>> =
        MutableLiveData()
    private val binData: MutableLiveData<NetWorkResult<CardBinMetaDataResponse>> =
        MutableLiveData()
    private val generateOtp: MutableLiveData<NetWorkResult<OTPResponse>> = MutableLiveData()
    private val submitOtp: MutableLiveData<NetWorkResult<OTPResponse>> = MutableLiveData()
    private val resendOtp: MutableLiveData<NetWorkResult<OTPResponse>> = MutableLiveData()
    private val savedCardRequestOtp: MutableLiveData<NetWorkResult<SavedCardResponse>> =
        MutableLiveData()
    private val savedCardCreateInactive: MutableLiveData<NetWorkResult<CustomerInfo>> =
        MutableLiveData()
    private val savedCardValidateUpdateOrder: MutableLiveData<NetWorkResult<CustomerInfoResponse>> =
        MutableLiveData()

    var dccAmount: MutableLiveData<String> = MutableLiveData()
    var dccAmountMessage: MutableLiveData<DCCDetails> = MutableLiveData()

    var paymentId: MutableLiveData<String> = MutableLiveData()
    var pbpAmount: MutableLiveData<Int> = MutableLiveData()
    var otpId: MutableLiveData<String> = MutableLiveData()
    var mobileNumberValidate: MutableLiveData<Boolean> = MutableLiveData()

    val fetch_data_response: LiveData<NetWorkResult<FetchResponse>> = fetchDataResponse
    val process_payment_data: NetWorkResult<ProcessPaymentResponse>? = null
    val process_payment_response: LiveData<NetWorkResult<ProcessPaymentResponse>> =
        responseProcessPayment
    val reward_response: LiveData<NetWorkResult<RewardResponse>> = rewardDataPayment
    val transaction_status_response: MutableLiveData<NetWorkResult<TransactionStatusResponse>?> =
        transactionStatus
    val cancel_transaction_response: LiveData<NetWorkResult<CancelTransactionResponse>> =
        cancelTransaction
    val bin_data_response: LiveData<NetWorkResult<CardBinMetaDataResponse>> =
        binData
    val generate_otp_response: LiveData<NetWorkResult<OTPResponse>> = generateOtp
    val submit_otp_response: LiveData<NetWorkResult<OTPResponse>> = submitOtp
    val resend_otp_response: LiveData<NetWorkResult<OTPResponse>> = resendOtp
    val saved_card_request_otp_response: LiveData<NetWorkResult<SavedCardResponse>> =
        savedCardRequestOtp
    val saved_card_create_inactive_response: LiveData<NetWorkResult<CustomerInfo>> =
        savedCardCreateInactive
    val saved_card_validate_update_order_response: LiveData<NetWorkResult<CustomerInfoResponse>> =
        savedCardValidateUpdateOrder
    var savedCardOtpError: MutableLiveData<String> = MutableLiveData()

    private val exceptionHandler: CoroutineContext =
        CoroutineExceptionHandler { context, throwable ->
            /*val clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context)
            CleverTapUtil.CT_EVENT_SDK_ERROR(
                clevertapDefaultInstance,
                throwable.javaClass.toString(),
                throwable.printStackTrace().toString()
            )*/
            //throwable.printStackTrace()
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()

            Utils.println("Exception  in viewmodel ${throwable.message} ${exceptionAsString}")
        }

    fun fetchData(token: String?) = viewModelScope.launch(exceptionHandler) {
        repository.fetchData(getApplication(), token).collect { values ->
            fetchDataResponse.value = values
        }
    }

    fun processPayment(token: String?, paymentData: ProcessPaymentRequest?) =
        viewModelScope.launch(exceptionHandler) {
            repository.processPayment(getApplication(), token, paymentData).collect { values ->
                responseProcessPayment.value = values
                //process_payment_data = values
            }
        }

    fun clearProcessPayment() {
        //responseProcessPayment.postValue(null)
    }

    fun rewardData(token: String, rewardData: RewardRequest) =
        viewModelScope.launch(exceptionHandler) {
            repository.rewardPayment(getApplication(), token, rewardData).collect { values ->
                rewardDataPayment.value = values
            }
        }

    fun getTransactionStatus(token: String?) = viewModelScope.launch(exceptionHandler) {
        repository.transactionStatus(getApplication(), token).collect { values ->
            transactionStatus.value = values
        }
    }

    fun clearTransactionStatus() {
        transactionStatus.value = null
    }

    fun cancelTransaction(token: String, cancelPayment: Boolean) =
        viewModelScope.launch(exceptionHandler) {
            repository.cancelTransaction(getApplication(), token, cancelPayment).collect { values ->
                cancelTransaction.value = values
            }
        }

    fun getBinData(token: String, cardData: CardBinMetaDataRequestList) =
        viewModelScope.launch(exceptionHandler) {
            repository.binData(getApplication(), token, cardData).collect { values ->
                binData.value = values
            }
        }

    fun generatOtp(token: String, otpRequest: OTPRequest) =
        viewModelScope.launch(exceptionHandler) {
            repository.generateOTP(getApplication(), token, otpRequest).collect { values ->
                generateOtp.value = values
            }
        }

    fun submitOtp(token: String, otpRequest: OTPRequest) =
        viewModelScope.launch(exceptionHandler) {
            repository.submitOTP(getApplication(), token, otpRequest).collect { values ->
                submitOtp.value = values
            }
        }

    fun resendOtp(token: String, otpRequest: OTPRequest) =
        viewModelScope.launch(exceptionHandler) {
            repository.resendOTP(getApplication(), token, otpRequest).collect { values ->
                resendOtp.value = values
            }
        }

    fun sendOTPCustomer(token: String?, otpRequest: OTPRequest?) =
        viewModelScope.launch(exceptionHandler) {
            repository.sendOTPCustomer(getApplication(), token, otpRequest).collect { values ->
                savedCardRequestOtp.value = values
            }
        }

    fun createInactive(token: String?, customerInfo: CustomerInfo?) =
        viewModelScope.launch(exceptionHandler) {
            repository.createInactive(getApplication(), token, customerInfo).collect { values ->
                savedCardCreateInactive.value = values
            }
        }

    fun validateUpdateOrder(token: String?, otpRequest: OTPRequest?) =
        viewModelScope.launch(exceptionHandler) {
            repository.validateUpdateOrder(getApplication(), token, otpRequest).collect { values ->
                savedCardValidateUpdateOrder.value = values
            }
        }

}