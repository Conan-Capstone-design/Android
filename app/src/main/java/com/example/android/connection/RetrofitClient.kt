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

    data class ResponseProfile(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: ResponseProfileResult
    )

    data class ResponseProfileResult(
        @SerializedName("isSuccess")
        val isSuccess: Boolean,
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String,
        @SerializedName("result")
        val result: Any // 또는 실제 결과가 어떤 형식인지에 따라 적절한 클래스 사용
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
        val result: Message
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