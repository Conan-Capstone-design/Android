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
}