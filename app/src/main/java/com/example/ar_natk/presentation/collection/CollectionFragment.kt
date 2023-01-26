package com.example.ar_natk.presentation.collection

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ar_natk.data.models.ItemModel
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.FragmentCollectionBinding
import com.example.ar_natk.presentation.collection.adapter.CollectionAdapter
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.utils.Constants
import com.example.ar_natk.utils.navigateToInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentCollectionBinding
    private lateinit var itemCollectionList: ArrayList<ItemModel>
    private lateinit var sortedItemCollectionList: ArrayList<ItemModel>
    private lateinit var collectionAdapter: CollectionAdapter
    private var fullItemCount = 0
    private var itemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).supportActionBar?.show()
        showStatusBar()
        setAdapter()
        checkFullCollection()

        (activity as MainActivity).supportActionBar?.subtitle = "${itemCount}/${fullItemCount}"

        return binding.root
    }

    private fun checkFullCollection() {
        binding.iCongratulate.root.visibility =
            if (itemCount == fullItemCount) View.VISIBLE else View.GONE
    }

    private fun setAdapter() {
        initItems()
        sortedItemCollectionList = ArrayList()

        itemCollectionList.forEach { item ->
            if (UserDataStorage(requireContext()).collection?.contains(item.id.toString()) == true) {
                sortedItemCollectionList.add(item)
            }
        }

        itemCount = sortedItemCollectionList.size
        collectionAdapter = CollectionAdapter { id -> navigateToInfo(id) }
        collectionAdapter.submitList(sortedItemCollectionList)

        with(binding.rvItems) {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = collectionAdapter
        }
    }

    private fun initItems() {
        itemCollectionList = ArrayList()
        itemCollectionList = Json.decodeFromString(readAssets())
        fullItemCount = itemCollectionList.size
    }

    private fun readAssets(): String {
        return requireContext().assets.open(Constants.MODEL_ITEM_FILE_PATH)
            .bufferedReader()
            .use { it.readText() }
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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).supportActionBar?.subtitle = ""
    }
}