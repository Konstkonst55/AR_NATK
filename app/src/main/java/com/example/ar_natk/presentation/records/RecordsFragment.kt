package com.example.ar_natk.presentation.records

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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecordsFragment : Fragment() {

    private lateinit var binding: FragmentRecordsBinding
    private lateinit var adapter: UsersRecordsAdapter
    private lateinit var db: FirebaseFirestore

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

    private fun addDecorator() {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()
        addDecorator()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setAdapter() {
        adapter = UsersRecordsAdapter(getUsers())
    }

    private fun getUsers(): ArrayList<UserModel> {
        val users: ArrayList<UserModel> = ArrayList()
        db = Firebase.firestore

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    users.add(
                        UserModel(
                            userName = document.data["name"].toString(),
                            userScore = document.data["count"].toString().toInt()
                        )
                    )
                }
                hideProgress()
            }
            .addOnFailureListener {
                hideProgress()
                Log.e("firebase", it.message.toString())
                Snackbar.make(
                    binding.root,
                    getString(R.string.t_connection_error),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        return users
    }
}