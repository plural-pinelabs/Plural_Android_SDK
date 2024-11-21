package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pinelabs.pluralsdk.data.model.CancelTransactionResponse
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.model.ProcessPaymentRequest
import com.pinelabs.pluralsdk.data.model.ProcessPaymentResponse
import com.pinelabs.pluralsdk.data.model.RewardRequest
import com.pinelabs.pluralsdk.data.model.RewardResponse
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.repository.Repository
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import kotlinx.coroutines.launch

class FetchDataViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    private val response: MutableLiveData<NetWorkResult<FetchResponse>> = MutableLiveData()
    private val responseProcessPayment: MutableLiveData<NetWorkResult<ProcessPaymentResponse>> =
        MutableLiveData()
    private val rewardDataPayment: MutableLiveData<NetWorkResult<RewardResponse>> =
        MutableLiveData()
    private val transactionStatus: MutableLiveData<NetWorkResult<TransactionStatusResponse>> =
        MutableLiveData()
    private val cancelTransaction: MutableLiveData<NetWorkResult<CancelTransactionResponse>> =
        MutableLiveData()
    var pbpAmount: MutableLiveData<Int> = MutableLiveData()

    val fetch_response: LiveData<NetWorkResult<FetchResponse>> = response
    val process_payment_response: LiveData<NetWorkResult<ProcessPaymentResponse>> =
        responseProcessPayment
    val reward_response: LiveData<NetWorkResult<RewardResponse>> = rewardDataPayment
    val transaction_status_response: LiveData<NetWorkResult<TransactionStatusResponse>> =
        transactionStatus
    val cancel_transaction_response: LiveData<NetWorkResult<CancelTransactionResponse>> =
        cancelTransaction

    fun fetchData(token: String) = viewModelScope.launch {
        repository.fetchData(getApplication(), token).collect { values ->
            response.value = values
        }
    }

    fun processPayment(token: String, paymentData: ProcessPaymentRequest) = viewModelScope.launch {
        repository.processPayment(getApplication(), token, paymentData).collect { values ->
            responseProcessPayment.value = values
        }
    }

    fun rewardData(token: String, rewardData: RewardRequest) = viewModelScope.launch {
        repository.rewardPayment(getApplication(), token, rewardData).collect { values ->
            rewardDataPayment.value = values
        }
    }

    fun getTransactionStatus(token: String) = viewModelScope.launch {
        repository.transactionStatus(getApplication(), token).collect { values ->
            transactionStatus.value = values
        }
    }

    fun cancelTransaction(token: String) = viewModelScope.launch {
        repository.cancelTransaction(getApplication(), token).collect { values ->
            cancelTransaction.value = values
        }
    }

    fun updateAmount(amount: Int) {
        fetch_response.value!!.data!!.paymentData!!.originalTxnAmount!!.amount = amount
        response.value = fetch_response.value
    }

}