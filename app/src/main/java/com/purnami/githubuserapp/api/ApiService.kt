package com.purnami.githubuserapp.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users")
    fun getUserList(@Query("since") since: String): Call<List<UserResponse>>

    @GET("users/{login}")
    fun getProfileUser(@Path("login") login: String): Call<ProfileResponse>

}