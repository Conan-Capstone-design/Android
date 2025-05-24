package com.example.android.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/user/login")
    fun login(@Body request: RetrofitClient.Requestlogin): Call<RetrofitClient.Responselogin>
}