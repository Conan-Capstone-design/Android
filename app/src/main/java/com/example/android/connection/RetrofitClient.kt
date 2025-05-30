package com.example.android.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {
    data class Requestlogin(
        @SerializedName("email")
        val member_id: String,
        @SerializedName("password")
        val password: String
    )

    data class Responselogin(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: String
    )

    data class RequestSignup(
        @SerializedName("email")
        val member_id: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("option")
        val option: Int,
        @SerializedName("image")
        val image: String? = null
    )

    data class ResponseSignup(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String
    )

    data class ResponseWithdraw(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: WithdrawResult
    )

    data class WithdrawResult(
        @SerializedName("message")
        val message: String
    )
}