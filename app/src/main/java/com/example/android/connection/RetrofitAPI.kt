package com.example.android.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/user/login")
    fun login(@Body request: RetrofitClient.Requestlogin): Call<RetrofitClient.Responselogin>

    @GET("/mypage/main")
    fun getprofile(@Header("x-access-token") token: String): Call<RetrofitClient.Responsegetprofile>
}