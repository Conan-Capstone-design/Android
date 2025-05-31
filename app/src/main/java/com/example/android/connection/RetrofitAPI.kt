package com.example.android.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("/mypage/voice-list/byChar")
    fun mypageTTSList(
        @Header("x-access-token") token: String,
        @Query("characterName") characterName: String
    ): Call<RetrofitClient.ResponseMypageTTS>

    @DELETE("mypage/voice-list/byChar/delete")
    fun mypageTTSDelete(
        @Header("x-access-token") token: String,
        @Query("characterName") characterName: String,
        @Query("voiceId") voiceId: Int
    ): Call<RetrofitClient.ResponseMypageTTSDelete>

    @GET("mypage/voice-list")
    fun MypageTTSlistGet(@Header("x-access-token") token: String): Call<RetrofitClient.ResponseTTSListGet>

}