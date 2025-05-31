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
    data class Responsegetprofile(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: GetprofileResult
    )

    data class GetprofileResult(
        @SerializedName("user")
        val user: User,
        @SerializedName("commentList")
        val commentList: List<commentList>,
    )

    data class User(
        @SerializedName("email")
        val email: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("profile_img")
        val profileImg: String
    )

    data class commentList(
        @SerializedName("character")
        val character: String,
        @SerializedName("created_at")
        val created_at: String
    )

    data class ResponseStartChat(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: ChatRoomResult
    )

    data class ChatRoomResult(
        @SerializedName("chat_id")
        val chatId: Int,
        @SerializedName("is_new")
        val isNew: Boolean,
        @SerializedName("messages")
        val messages: List<Message>,
    )

    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    ) : java.io.Serializable

    data class Message2(
        @SerializedName("role")
        val role: String,
        @SerializedName("message")
        val message: String
    ) : java.io.Serializable

    data class RequestMessage(
        @SerializedName("chat_id")
        val chatId: Int,
        @SerializedName("message")
        val message: String
    )

    data class ResponseMessage(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: Message2
    )

    data class ResponseCharacterChat(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: String,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: List<ChatList>
    )

    data class ChatList(
        @SerializedName("character")
        val character: String,
        @SerializedName("updated_at")
        val updated_at: String
    )

}