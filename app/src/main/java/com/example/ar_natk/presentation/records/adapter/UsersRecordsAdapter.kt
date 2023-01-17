package com.example.ar_natk.presentation.records.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ar_natk.R
import com.example.ar_natk.data.models.UserModel

class UsersRecordsAdapter(
    private val userList: ArrayList<UserModel>
) : RecyclerView.Adapter<UsersRecordsAdapter.ViewHolder>() {

    lateinit var view: View

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        with(holder) {
            userName.text = user.userName
            userScore.text = user.userScore.toString()
            userPlace.text = (position + 1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.record_item_layout,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder internal constructor(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tvUserName)
        val userScore: TextView = view.findViewById(R.id.tvUserScore)
        val userPlace: TextView = view.findViewById(R.id.tvUserPlace)
    }

}