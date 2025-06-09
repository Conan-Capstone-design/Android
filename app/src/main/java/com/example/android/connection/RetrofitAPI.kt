package com.example.android.connection

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

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
    fun withdrawUser(@Header("x-access-token") token: String): Call<RetrofitClient.ResponseWithdraw>

    @GET("/tts/voice-play/{voice_id}")
    fun playVoice(
        @Header("x-access-token") token: String,
        @Path("voice_id") voiceId: Int): Call<RetrofitClient.ResponseVoicePlay>

    @POST("/tts/voice-save")
    fun voiceSave(
        @Header("x-access-token") token: String,
        @Body request: RetrofitClient.RequestVoiceSave): Call<RetrofitClient.ResponseVoiceSave>

    @DELETE("/tts/voice-delete/{voice_id}")
    fun deleteVoice(
        @Header("x-access-token") token: String,
        @Path("voice_id") voiceId: Int): Call<RetrofitClient.ResponseVoiceDelete>

    @GET("/tts/voice-all")
    fun getAllVoices(@Header("x-access-token") token: String): Call<RetrofitClient.ResponseVoiceAll>

    @Multipart
    @POST("/llvc/video-convert")
    @Streaming
    fun convertVideo(
        @Header("x-access-token") token: String,
        @Part video: MultipartBody.Part,
        @Part("targetIndex") targetIndex: RequestBody): Call<ResponseBody>

}