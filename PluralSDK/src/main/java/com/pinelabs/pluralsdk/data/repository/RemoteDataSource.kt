package com.pinelabs.pluralsdk.data.repository

import com.pinelabs.pluralsdk.data.utils.RetrofitBuilder

class RemoteDataSource () {
    suspend fun fetchData(token:String) = RetrofitBuilder.apiService.fetchData(token)
}