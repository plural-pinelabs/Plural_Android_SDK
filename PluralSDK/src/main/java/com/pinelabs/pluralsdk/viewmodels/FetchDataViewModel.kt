package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pinelabs.pluralsdk.data.model.FetchResponse
import com.pinelabs.pluralsdk.data.repository.Repository
import com.pinelabs.pluralsdk.data.utils.NetWorkResult
import kotlinx.coroutines.launch

class FetchDataViewModel (private val repository: Repository, application: Application): AndroidViewModel(application) {
    private val response: MutableLiveData<NetWorkResult<FetchResponse>> = MutableLiveData()

    val fetch_response: LiveData<NetWorkResult<FetchResponse>> = response

    fun fetchData(token: String) = viewModelScope.launch {
        repository.fetchData(getApplication(), token).collect { values ->
            response.value = values
        }
    }

}