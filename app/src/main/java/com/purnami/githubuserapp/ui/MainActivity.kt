package com.purnami.githubuserapp.ui

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.purnami.githubuserapp.database.DatabaseHandler
import com.purnami.githubuserapp.databinding.ActivityMainBinding
import com.purnami.githubuserapp.model.User
import com.purnami.githubuserapp.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG="MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private var mainViewModel: MainViewModel? = null
    private var databaseHandler: DatabaseHandler? = null
    private var userAdapter: UserAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHandler = DatabaseHandler(this)
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        databaseHandler!!.open()

        userAdapter = UserAdapter()
        userAdapter!!.onItemClick = { selectedData ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.USERNAME, selectedData.userName)
            startActivity(intent)
        }

        val userListDb= databaseHandler!!.allUsers
        if(userListDb.size!=0){
            binding.progressBar.visibility = View.GONE
            setAdapter(userListDb)
        }else{
            mainViewModel!!.getUserList()
            mainViewModel!!.userList.observe(this,{userList->
                if(userList!=null){
                    binding.progressBar.visibility = View.GONE
                    val listUser= ArrayList<User>()
                    if(userListDb.size==0){
                        for (user in userList) {
                            //insert to User Tabel
                            var values = ContentValues()
                            values.put(DatabaseHandler.USER_ID, user.id)
                            values.put(DatabaseHandler.USER_USERNAME, user.login)
                            values.put(DatabaseHandler.USER_AVATAR, user.avatarUrl)
                            values.put(DatabaseHandler.USER_LINK, user.htmlUrl)
                            databaseHandler!!.insertUser(values)

                            //insert to Notes Tabel
                            values = ContentValues()
                            values.put(DatabaseHandler.NOTES_ID, user.id)
                            values.put(DatabaseHandler.NOTES_NOTES, "")
                            databaseHandler!!.insertNotes(values)

                            //insert to Profile Tabel
                            mainViewModel!!.getProfileUser(user.login)
                            mainViewModel!!.profileUser.observe(this, {profile->
                                if (profile!=null){
                                    val values = ContentValues()
                                    values.put(DatabaseHandler.PROFILE_ID, profile.id)
                                    values.put(DatabaseHandler.PROFILE_USERNAME, profile.login)
                                    values.put(DatabaseHandler.PROFILE_AVATAR, profile.avatarUrl)
                                    values.put(DatabaseHandler.PROFILE_FOLLOWERS, profile.followers)
                                    values.put(DatabaseHandler.PROFILE_FOLLOWING, profile.following)
                                    values.put(DatabaseHandler.PROFILE_COMPANY, profile.company)
                                    values.put(DatabaseHandler.PROFILE_BLOG, profile.blog)
                                    databaseHandler!!.insertProfile(values)
                                }
                            })
                        }
                    }
                    for (user in userList) {
                        listUser.add(User(user.id.toString(), user.login, user.avatarUrl, user.htmlUrl))
                    }
                    setAdapter(listUser)
                }else{
                    Log.d(TAG,"offline")
                    binding.progressBar.visibility = View.GONE
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setCancelable(false)
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->

                        })

                    val alert = dialogBuilder.create()
                    alert.setTitle("No Internet Connection")
                    alert.show()
                }
            })
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.progressBar.visibility=View.GONE
                Log.d(TAG, "binding.etSearch.text "+binding.etSearch.text.toString())
                val searchUser=databaseHandler!!.searchUsers(binding.etSearch.text.toString())
                setAdapter(searchUser)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }


    /**
     * this method is called to set adapter
     */
    fun setAdapter(listUser: ArrayList<User>){
        userAdapter!!.setUsers(listUser)
        userAdapter!!.setDatabase(databaseHandler!!)

        binding.rvUser.setLayoutManager(LinearLayoutManager(applicationContext))
        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.setAdapter(userAdapter)
    }

}