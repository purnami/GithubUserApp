package com.purnami.githubuserapp.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.purnami.githubuserapp.database.DatabaseHandler
import com.purnami.githubuserapp.databinding.ActivityDetailBinding
import com.purnami.githubuserapp.databinding.ActivityMainBinding
import com.purnami.githubuserapp.model.Profile
import com.purnami.githubuserapp.viewmodel.MainViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var mainViewModel: MainViewModel? = null
    private var databaseHandler: DatabaseHandler? = null

    companion object {
        const val USERNAME = "username"
        const val TAG="DetailActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val username=intent.getStringExtra(USERNAME)
        Log.d(TAG, "username "+username)

        databaseHandler = DatabaseHandler(this)
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        databaseHandler!!.open()
        var profileUser:Profile?=null
        try {
            profileUser= databaseHandler!!.getProfile(username!!)
            Log.d(TAG, "profileUser.userName "+profileUser.userName)
            if(profileUser!=null){
                binding.tvNameHeader.text=profileUser.userName
                binding.tvName.text="Name : "+profileUser.userName
                binding.tvFollower.text="Followers : "+profileUser.followers
                binding.tvFollowing.text="Following : "+profileUser.following
                binding.tvCompany.text="Company : "+profileUser.company
                binding.tvBlog.text="Blog : "+profileUser.blog
                Glide.with(this)
                    .load(profileUser.avatar)
                    .into(binding.imgAvatar)
                showNotes(profileUser.id.toInt())
                saveToNotes(profileUser.id.toInt())
            }
        }catch (e:Exception){
            Log.e(TAG, e.toString())
        }
    }

    /**
     * this methos is called to show the notes
     */
    fun showNotes(id: Int){
        try{
            val notes=databaseHandler!!.getNotes(id)
            binding.etNotes.setText(notes.notes)
        }catch (e:Exception){
            Log.e(TAG, e.toString())
        }

    }

    /**
     * this methos is called to save the notes to the database
     */
    fun saveToNotes(id:Int){
        binding.btnSaveNote.setOnClickListener {
            val notes=binding.etNotes.text.toString().trim()
            if(!notes.equals("")){
                val values = ContentValues()
                values.put(DatabaseHandler.NOTES_ID, id)
                values.put(DatabaseHandler.NOTES_NOTES, notes)
                try{
                    val notes=databaseHandler!!.getNotes(id)
                    if(!notes.equals("")){
                        databaseHandler!!.updateNotes(id, values)
                        Toast.makeText(this@DetailActivity, "Success to update notes", Toast.LENGTH_SHORT).show()
                    }else{
                        databaseHandler!!.insertNotes(values)
                        Toast.makeText(this@DetailActivity, "Success to save notes", Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Log.e(TAG, e.toString())
                    databaseHandler!!.insertNotes(values)
                    Toast.makeText(this@DetailActivity, "Success to save notes", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@DetailActivity, "Failed to save notes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}