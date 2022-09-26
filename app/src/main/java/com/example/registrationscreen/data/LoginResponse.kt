package com.example.registrationscreen.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginResponse(
    @SerializedName("result")
    var result: String,

    @SerializedName("data")
    var data: AuthData
)

data class AuthData(val token: String): Serializable