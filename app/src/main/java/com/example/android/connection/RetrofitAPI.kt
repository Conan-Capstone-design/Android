package com.example.android.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/user/login")
    fun login(@Body request: RetrofitClient.Requestlogin): Call<RetrofitClient.Responselogin>

    @POST("/user/sign-up")
    fun signup(@Body request: RetrofitClient.RequestSignup): Call<RetrofitClient.ResponseSignup>

    @POST("/user/withdraw")
    fun withdrawUser(@retrofit2.http.Header("x-access-token") token: String): Call<RetrofitClient.ResponseWithdraw>

}