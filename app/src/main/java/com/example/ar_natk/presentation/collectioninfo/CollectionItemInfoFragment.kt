package com.example.ar_natk.presentation.collectioninfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.ar_natk.data.models.ItemModel
import com.example.ar_natk.databinding.FragmentCollectionItemInfoBinding
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.utils.Constants
import com.example.ar_natk.utils.toBitmap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CollectionItemInfoFragment : Fragment() {
    private lateinit var binding: FragmentCollectionItemInfoBinding
    private val args: CollectionItemInfoFragmentArgs by navArgs()
    private var currentItem: ItemModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionItemInfoBinding.inflate(inflater, container, false)

        getItemModel(args.itemId)
        setToolbar()

        with(binding) {
            tvCollectionHeaderInfo.text = currentItem?.infoHeader
            tvCollectionInfo.text = currentItem?.infoText
            ivImageCollectionInfo.setImageBitmap(currentItem?.previewImage?.toBitmap(requireContext()))
        }

        binding.bOpenWiki.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentItem?.wikiUrl)))
        }

        binding.bBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        return binding.root
    }

    private fun getItemModel(id: Int) {
        val items: ArrayList<ItemModel> = Json.decodeFromString(readAssets())
        currentItem = items.find { it.id == id }
    }

    private fun readAssets(): String {
        return requireContext().assets.open(Constants.MODEL_ITEM_FILE_PATH)
            .bufferedReader()
            .use { it.readText() }
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).supportActionBar?.run {
            title = currentItem?.infoHeader
            setHomeButtonEnabled(true)
            show()
        }
    }
}