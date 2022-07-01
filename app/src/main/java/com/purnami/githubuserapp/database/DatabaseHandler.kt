package com.purnami.githubuserapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.purnami.githubuserapp.model.Notes
import com.purnami.githubuserapp.model.Profile
import com.purnami.githubuserapp.model.User

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "github_user"
        const val DATABASE_VERSION = 1

        const val TABLE_USER="Users"
        const val USER_ID = "id_user"
        const val USER_USERNAME = "username_user"
        const val USER_AVATAR = "avatar_user"
        const val USER_LINK = "link_user"

        const val TABLE_PROFILE="Profile"
        const val PROFILE_ID = "id_profile"
        const val PROFILE_USERNAME = "username_profile"
        const val PROFILE_AVATAR = "avatar_profile"
        const val PROFILE_FOLLOWERS = "followers_profile"
        const val PROFILE_FOLLOWING = "following_profile"
        const val PROFILE_COMPANY = "company_profile"
        const val PROFILE_BLOG = "blog_profile"

        const val TABLE_NOTES="Notes"
        const val NOTES_ID = "id_notes"
        const val NOTES_NOTES = "notes_notes"

        private lateinit var db: SQLiteDatabase

        private const val SQL_CREATE_TABLE_USER = ("CREATE TABLE $TABLE_USER" +
                " ($USER_ID INTEGER PRIMARY KEY, "+
                " $USER_USERNAME TEXT, "+
                " $USER_AVATAR TEXT, "+
                " $USER_LINK TEXT);")

        private const val SQL_CREATE_TABLE_PROFILE = ("CREATE TABLE $TABLE_PROFILE" +
                " ($PROFILE_ID INTEGER PRIMARY KEY, "+
                " $PROFILE_USERNAME TEXT, "+
                " $PROFILE_AVATAR TEXT, "+
                " $PROFILE_FOLLOWERS INTEGER, "+
                " $PROFILE_FOLLOWING INTEGER, "+
                " $PROFILE_COMPANY TEXT, "+
                " $PROFILE_BLOG TEXT);")

        private const val SQL_CREATE_TABLE_NOTES = ("CREATE TABLE $TABLE_NOTES" +
                " ($NOTES_ID INTEGER PRIMARY KEY, "+
                " $NOTES_NOTES TEXT);")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SQL_CREATE_TABLE_USER)
        db.execSQL(SQL_CREATE_TABLE_PROFILE)
        db.execSQL(SQL_CREATE_TABLE_NOTES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS '$TABLE_USER'")
        db!!.execSQL("DROP TABLE IF EXISTS '$TABLE_PROFILE'")
        db!!.execSQL("DROP TABLE IF EXISTS '$TABLE_NOTES'")
    }

    fun open(){
        db = this.writableDatabase
    }

    fun insertUser(values: ContentValues?): Long {
        return db.insert(TABLE_USER, null, values)
    }

    fun updateUser(id: Int, values: ContentValues?): Int {
        return db.update(TABLE_USER, values, "$USER_ID = ?", arrayOf(id.toString()))
    }

    fun deleteUser(id: Int) : Int{
        return db.delete(TABLE_USER, "$USER_ID = ?", arrayOf(id.toString()))
    }

    val allUsers: ArrayList<User>
        @SuppressLint("Range")
        get(){
            val userArrayList = ArrayList<User>()
            val selectQuery = "SELECT * FROM $TABLE_USER"
            val db = this.readableDatabase
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    val id=c.getInt(c.getColumnIndex(USER_ID))
                    val username = c.getString(c.getColumnIndex(USER_USERNAME))
                    val avatar = c.getString(c.getColumnIndex(USER_AVATAR))
                    val link = c.getString(c.getColumnIndex(USER_LINK))
                    userArrayList.add(User(id.toString(), username,avatar,link))
                } while (c.moveToNext())
            }
            return userArrayList
        }

    fun insertProfile(values: ContentValues?): Long {
        return db.insert(TABLE_PROFILE, null, values)
    }

    fun updateProfile(id: Int, values: ContentValues?): Int {
        return db.update(TABLE_PROFILE, values, "$PROFILE_ID = ?", arrayOf(id.toString()))
    }

    fun deleteProfile(id: Int) : Int{
        return db.delete(TABLE_PROFILE, "$PROFILE_ID = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun getProfile(username: String): Profile{
//        var list: MutableList<User> = ArrayList()
        var profile:Profile?=null
        val db = this.readableDatabase
        val query = "Select * from $TABLE_PROFILE where $PROFILE_USERNAME = '$username'"
        val result = db.rawQuery(query, null)
        result.moveToFirst()
        Log.d("DatabaseHandler", "id "+result.getInt(result.getColumnIndex(PROFILE_ID)))
        if(result.moveToFirst()){
            do{
                val id=result.getInt(result.getColumnIndex(PROFILE_ID))
                val username = result.getString(result.getColumnIndex(PROFILE_USERNAME))
                val avatar = result.getString(result.getColumnIndex(PROFILE_AVATAR))
                val followers=result.getInt(result.getColumnIndex(PROFILE_FOLLOWERS))
                val following=result.getInt(result.getColumnIndex(PROFILE_FOLLOWING))
                var company= result.getString(result.getColumnIndex(PROFILE_COMPANY))
                var blog= result.getString(result.getColumnIndex(PROFILE_BLOG))
                if(company==null){
                    company="-"
                }
                if(blog==null){
                    blog="-"
                }
                profile= Profile(id.toString(), username, avatar, followers, following, company, blog)

            }while (result.moveToNext())
        }
        return profile!!
    }

    fun insertNotes(values: ContentValues?): Long {
        return db.insert(TABLE_NOTES, null, values)
    }

    fun updateNotes(id: Int, values: ContentValues?): Int {
        return db.update(TABLE_NOTES, values, "$NOTES_ID = ?", arrayOf(id.toString()))
    }

    fun deleteNotes(id: Int) : Int{
        return db.delete(TABLE_NOTES, "$NOTES_ID = ?", arrayOf(id.toString()))
    }

    @SuppressLint("Range")
    fun getNotes(id: Int): Notes{
        var notes:Notes?=null
        val db = this.readableDatabase
        val query = "Select * from $TABLE_NOTES where $NOTES_ID = '$id'"
        val result = db.rawQuery(query, null)
        result.moveToFirst()
        Log.d("DatabaseHandler", "notesid "+result.getInt(result.getColumnIndex(NOTES_ID)))
        val id=result.getInt(result.getColumnIndex(NOTES_ID))
        val note= result.getString(result.getColumnIndex(NOTES_NOTES))
        notes = Notes(id.toString(), note)
        return notes
    }

    @SuppressLint("Range")
    fun searchUsers(keyword: String): ArrayList<User> {
        val userArrayList = ArrayList<User>()
        val selectQuery = "SELECT * FROM Users INNER JOIN Notes ON id_user=id_notes WHERE username_user LIKE '%$keyword%' OR notes_notes LIKE '%$keyword%'"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)
        if (c.moveToFirst()) {
            do {
                val id=c.getInt(c.getColumnIndex(USER_ID))
                val username = c.getString(c.getColumnIndex(USER_USERNAME))
                val avatar = c.getString(c.getColumnIndex(USER_AVATAR))
                val link = c.getString(c.getColumnIndex(USER_LINK))
                userArrayList.add(User(id.toString(), username,avatar,link))
            } while (c.moveToNext())
        }
        return userArrayList
    }
}