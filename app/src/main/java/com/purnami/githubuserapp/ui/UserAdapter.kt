package com.purnami.githubuserapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.purnami.githubuserapp.database.DatabaseHandler
import com.purnami.githubuserapp.databinding.ItemListUserBinding
import com.purnami.githubuserapp.model.User

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    companion object {
        const val TAG="UserAdapter"
    }

    private var listUser = ArrayList<User>()
    private var db:DatabaseHandler?=null
    var onItemClick: ((User) -> Unit)? = null


    /**
     * This method is called for add the [user] to [listUser]
     * @param user is the list that contain the user data
     */
    fun setUsers(user: List<User>) {
        this.listUser.clear()
        this.listUser.addAll(user)
        notifyDataSetChanged()
    }

    fun setDatabase(db:DatabaseHandler){
        this.db=db
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemListUserBinding = ItemListUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(itemListUserBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = listUser[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = listUser.size


    inner class UserViewHolder(private val binding: ItemListUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(user.avatar)
                    .into(imgAvatar)
                tvUsername.text = user.userName
                tvLinkUser.text=user.linkUser
                try{
                    val getNotes=db!!.getNotes(user.id.toInt())
                    Log.d(TAG, ""+getNotes.id+" "+getNotes.notes)
                    if(!getNotes.notes.equals("")){
                        imgNote.visibility= View.VISIBLE
                    }else{
                        imgNote.visibility= View.GONE
                    }
                }catch (e:Exception){
                    Log.e(TAG, e.toString())
                }

            }
        }
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listUser[adapterPosition])
            }
        }
    }
}