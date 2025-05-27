package com.example.android.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {
    @POST("/user/login")
    fun login(@Body request: RetrofitClient.Requestlogin): Call<RetrofitClient.Responselogin>

    @GET("/mypage/main")
    fun getprofile(@Header("x-access-token") token: String): Call<RetrofitClient.Responsegetprofile>

    @POST("/chat/start")
    fun startChat(
        @Header("x-access-token") token: String,
        @Query("character_name") characterName: String
    ): Call<RetrofitClient.ResponseStartChat>

    @POST("/chat/send")
    fun sendMessage(
        @Header("x-access-token") token: String,
        @Body messageRequest: RetrofitClient.RequestMessage
    ): Call<RetrofitClient.ResponseMessage>

    @GET("/chat/list")
    fun chatList(@Header("x-access-token") token: String): Call<RetrofitClient.ResponseCharacterChat>

}