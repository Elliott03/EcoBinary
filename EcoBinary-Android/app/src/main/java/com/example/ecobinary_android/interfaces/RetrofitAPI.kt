package com.example.ecobinary_android.interfaces

import com.example.ecobinary_android.models.CommandModel
import com.example.ecobinary_android.models.OutputModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {

    @POST("/execute")
    fun postData(@Body commandModel: CommandModel): Call<String>?
}