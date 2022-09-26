package com.example.registrationscreen.data

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("result")
    var result: String,

    @SerializedName("data")
    var message: String
)