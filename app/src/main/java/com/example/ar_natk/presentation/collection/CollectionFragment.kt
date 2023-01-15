package com.example.ar_natk.presentation.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ar_natk.databinding.FragmentCollectionBinding
import com.example.ar_natk.presentation.core.MainActivity

class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).supportActionBar?.show()

        return binding.root
    }
}