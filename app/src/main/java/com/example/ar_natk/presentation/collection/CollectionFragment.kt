package com.example.ar_natk.presentation.collection

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.ar_natk.databinding.FragmentCollectionBinding
import com.example.ar_natk.presentation.core.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).supportActionBar?.show()
        showStatusBar()

        return binding.root
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
}