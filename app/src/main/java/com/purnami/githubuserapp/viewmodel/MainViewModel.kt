package com.purnami.githubuserapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.purnami.githubuserapp.api.ApiConfig
import com.purnami.githubuserapp.api.ProfileResponse
import com.purnami.githubuserapp.api.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    companion object{
        private const val TAG = "MainViewModel"
    }

    private val _userList= MutableLiveData<List<UserResponse>>()
    val userList: LiveData<List<UserResponse>> = _userList

    private val _profileUser= MutableLiveData<ProfileResponse>()
    val profileUser: LiveData<ProfileResponse> = _profileUser

    /**
     * This method is called to get the user list from the API
     * [_userList] will containing the body of the response
     */
    fun getUserList(){
        val client : Call<List<UserResponse>> = ApiConfig.getApiService().getUserList("0")
        client.enqueue(object :  Callback<List<UserResponse>>{
            override fun onResponse(
                call: Call<List<UserResponse>>,
                response: Response<List<UserResponse>>
            ) {
                Log.d(TAG, "response.code "+response.code())
                if (response.isSuccessful) {
                    _userList.value=response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _userList.value=null
            }
        })
    }

    /**
     * This method is called to get the profile user from the API
     * [_profileUser] will containing the body of the response
     * @param username - that will be retrieved the profile data
     */
    fun getProfileUser(username: String){
        Log.d(TAG, "username "+username)
        val client : Call<ProfileResponse> = ApiConfig.getApiService().getProfileUser(username)
        client.enqueue(object :  Callback<ProfileResponse>{
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                Log.d(TAG, "response.code "+response.code())
                if (response.isSuccessful) {
                    _profileUser.value=response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _profileUser.value=null
            }
        })
    }
}