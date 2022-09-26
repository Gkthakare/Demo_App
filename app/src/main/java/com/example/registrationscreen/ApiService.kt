package com.example.registrationscreen

import com.example.registrationscreen.classes.Constants
import com.example.registrationscreen.data.LoginResponse
import com.example.registrationscreen.data.SignupResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST(Constants.LOGIN_URL)
    @FormUrlEncoded
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @POST(Constants.SIGNUP_URL)
    @FormUrlEncoded
    fun signUP(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>

}