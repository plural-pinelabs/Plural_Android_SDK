package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pinelabs.pluralsdk.data.model.TransactionStatusResponse
import com.pinelabs.pluralsdk.data.repository.Repository
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import kotlinx.coroutines.launch

class RetryViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {
    private val transactionStatus: MutableLiveData<NetWorkResult<TransactionStatusResponse>?> =
        MutableLiveData()
    val transaction_status_response: MutableLiveData<NetWorkResult<TransactionStatusResponse>?> =
        transactionStatus

    fun getTransactionStatus(token: String?) = viewModelScope.launch() {
        repository.transactionStatus(getApplication(), token).collect { values ->
            transactionStatus.value = values
        }
    }
}