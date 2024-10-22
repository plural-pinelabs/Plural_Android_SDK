package com.pinelabs.pluralsdk.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinelabs.pluralsdk.data.repository.RemoteDataSource
import com.pinelabs.pluralsdk.data.repository.Repository

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FetchDataViewModel(Repository(RemoteDataSource()), application) as T
    }
}