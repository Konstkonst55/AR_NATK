package com.example.ar_natk.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModelItem(
    @SerialName("id") val id: Int,
    @SerialName("targetImageName") val targetImage: String,
    @SerialName("targetImageTag") val targetImageTag: String,
    @SerialName("modelPath") val modelPath: String? = "nullModel.glb",
    @SerialName("infoHeader") val infoHeader: String? = null,
    @SerialName("infoText") val infoText: String? = null
)

