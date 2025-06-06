package com.example.android.connection

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface RetrofitAPI {
    @POST("/user/login")
    fun login(@Body request: RetrofitClient.Requestlogin): Call<RetrofitClient.Responselogin>

    @GET("/mypage/main")
    fun getprofile(@Header("x-access-token") token: String): Call<RetrofitClient.Responsegetprofile>

    @Multipart
    @PUT("/mypage/profile")
    fun editProfile(@Header("x-access-token") token: String,
                    @Part("nickname") nickname: RequestBody?,
                    @Part image: MultipartBody.Part?,
                    @Part("password") password: RequestBody): Call<RetrofitClient.ResponseProfile>

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

    @POST("/user/sign-up")
    fun signup(@Body request: RetrofitClient.RequestSignup): Call<RetrofitClient.ResponseSignup>

    @POST("/user/withdraw")
    fun withdrawUser(@retrofit2.http.Header("x-access-token") token: String): Call<RetrofitClient.ResponseWithdraw>

}