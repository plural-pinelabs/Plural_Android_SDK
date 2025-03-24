package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pinelabs.pluralsdk.data.model.OTPRequest
import com.pinelabs.pluralsdk.data.model.SavedCardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.repository.Repository
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.coroutines.CoroutineContext

class SavedCardViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    private val savedCardRequestOtp: MutableLiveData<NetWorkResult<SavedCardResponse>> =
        MutableLiveData()
    private val savedCardValidateOtp: MutableLiveData<NetWorkResult<SavedCardResponse>> =
        MutableLiveData()

    val saved_card_request_otp_response: LiveData<NetWorkResult<SavedCardResponse>> =
        savedCardRequestOtp
    val saved_card_validate_otp_response: LiveData<NetWorkResult<SavedCardResponse>> =
        savedCardValidateOtp

    var otpId: MutableLiveData<String> = MutableLiveData()
    var skipSavedCard: MutableLiveData<Boolean> = MutableLiveData()

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

            println("Exception  in viewmodel ${throwable.message} ${exceptionAsString}")
        }

    fun sendOTPCustomer(token: String?, otpRequest: OTPRequest?) =
        viewModelScope.launch(exceptionHandler) {
            repository.sendOTPCustomer(getApplication(), token, otpRequest).collect { values ->
                savedCardRequestOtp.value = values
            }
        }

    fun validateOTPCustomer(token: String?, otpRequest: OTPRequest?) =
        viewModelScope.launch(exceptionHandler) {
            repository.validateOTPCustomer(getApplication(), token, otpRequest).collect { values ->
                savedCardValidateOtp.value = values
            }
        }

}