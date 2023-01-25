package com.example.ar_natk.utils

import com.example.ar_natk.presentation.collection.CollectionFragment
import com.example.ar_natk.presentation.core.MainActivity

fun CollectionFragment.navigateToInfo(id: Int) {
    (activity as MainActivity).navigateCollectionToInfo(id)
}