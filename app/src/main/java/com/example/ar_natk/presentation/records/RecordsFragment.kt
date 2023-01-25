package com.example.ar_natk.presentation.records

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.ar_natk.R
import com.example.ar_natk.data.models.UserModel
import com.example.ar_natk.databinding.FragmentRecordsBinding
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.presentation.records.adapter.UsersRecordsAdapter
import com.example.ar_natk.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
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
        showStatusBar()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showStatusBar() {
        (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (activity as MainActivity).window.insetsController
                ?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            (activity as MainActivity).window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE
        }
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
                                count = document.data[Constants.FIRE_DOC_USER_SCORE].toString()
                                    .toInt(),
                                registrationDate = dateFromTimeStamp(document.data[Constants.FIRE_DOC_USER_DATE] as Timestamp),
                                id = document.id
                            )
                        )
                        Log.i(Constants.LOG_FIREBASE, document.data.toString())
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    showSnackbar("Здесь пусто")
                    Log.i(Constants.LOG_FIREBASE, "Сообщение из рекордов: База пустует")
                }

                hideProgress()
            }
            .addOnFailureListener {
                hideProgress()
                Log.e(Constants.LOG_FIREBASE, it.message.toString())
                //todo добавить layout connection error
                showSnackbar(getString(R.string.t_connection_error))
            }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun dateFromTimeStamp(timeStamp: Timestamp): Date {
        return timeStamp.toDate()
    }
}