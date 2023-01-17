package com.example.ar_natk.presentation.records

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ar_natk.R
import com.example.ar_natk.data.models.UserModel
import com.example.ar_natk.databinding.FragmentRecordsBinding
import com.example.ar_natk.presentation.records.adapter.UsersRecordsAdapter
import com.example.ar_natk.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class RecordsFragment : Fragment() {

    private lateinit var binding: FragmentRecordsBinding
    private lateinit var adapter: UsersRecordsAdapter
    private lateinit var users: ArrayList<UserModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsBinding.inflate(inflater, container, false)

        showProgress()

        return binding.root
    }

    private fun showProgress() {
        binding.pbUserLoader.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.pbUserLoader.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setAdapter() {
        getUsers()

        adapter = UsersRecordsAdapter(users)
        binding.rvUsers.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUsers() {
        users = ArrayList()

        FirebaseFirestore.getInstance().collection(Constants.FIRE_COLLECTION_USER)
            .orderBy(Constants.FIRE_DOC_USER_SCORE, Query.Direction.DESCENDING)
            .orderBy(Constants.FIRE_DOC_USER_DATE)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    for (document in result) {
                        users.add(
                            UserModel(
                                userName = document.data[Constants.FIRE_DOC_USER_NAME].toString(),
                                userScore = document.data[Constants.FIRE_DOC_USER_SCORE].toString()
                                    .toInt(),
                                registrationDate = dateFromTimeStamp(document.data[Constants.FIRE_DOC_USER_DATE] as Timestamp),
                                id = document.id
                            )
                        )
                        Log.i(Constants.LOG_FIREBASE, document.data.toString())
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    //todo добавить layout empty error
                    Snackbar.make(
                        binding.root,
                        "Здесь пусто",
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                hideProgress()
            }
            .addOnFailureListener {
                hideProgress()
                Log.e(Constants.LOG_FIREBASE, it.message.toString())
                //todo добавить layout connection error
                Snackbar.make(
                    binding.root,
                    getString(R.string.t_connection_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun dateFromTimeStamp(timeStamp: Timestamp): Date {
        return timeStamp.toDate()
    }
}