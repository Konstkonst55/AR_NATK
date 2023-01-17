package com.example.ar_natk.presentation.camera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ar_natk.R
import com.example.ar_natk.data.models.ItemModel
import com.example.ar_natk.databinding.FragmentCameraBinding
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.utils.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Sceneform
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.InstructionsController
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.CompletableFuture


class CameraFragment :
    Fragment(),
    BaseArFragment.OnSessionConfigurationListener {

    private lateinit var binding: FragmentCameraBinding

    private val fileModelItemPath = "model_item.json"

    private var itemCollectionList: ArrayList<ItemModel> = ArrayList()
    private val futures: List<CompletableFuture<Void>> = ArrayList()
    private var arFragment: ArFragment? = null
    private var modelDetected = false
    private var database: AugmentedImageDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        (requireActivity() as MainActivity).supportActionBar?.hide()

        if (Sceneform.isSupported(context)) {
            initItems()
            arFragment = childFragmentManager.findFragmentById(R.id.ArFragment) as ArFragment
            arFragment!!.setOnSessionConfigurationListener(this)
        }

        return binding.root
    }

    private fun initItems() {
        itemCollectionList = Json.decodeFromString(readAssets())
    }

    private fun readAssets(): String {
        return requireContext().assets.open(fileModelItemPath)
            .bufferedReader()
            .use { it.readText() }
    }

    override fun onSessionConfiguration(session: Session?, config: Config?) {
        config!!.planeFindingMode = Config.PlaneFindingMode.DISABLED

        database = AugmentedImageDatabase(session)

        itemCollectionList.toList().forEach { item ->
            database!!.addImage(
                item.targetImageTag,
                item.targetImage.toBitmap(requireContext())
            )
        }

        config.setAugmentedImageDatabase(database)

        arFragment!!.setOnAugmentedImageUpdateListener(this::onAugmentedImageTrackingUpdate)
    }

    private fun onAugmentedImageTrackingUpdate(augmentedImage: AugmentedImage) {
        if (augmentedImage.trackingState == TrackingState.TRACKING
            && augmentedImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
        ) {
            if (modelDetected) {
                arFragment!!.instructionsController.setEnabled(
                    InstructionsController.TYPE_AUGMENTED_IMAGE_SCAN, false
                )
                return
            }

            val anchorNode = AnchorNode(augmentedImage.createAnchor(augmentedImage.centerPose))
            var currentItemCollection: ItemModel? = null

            itemCollectionList.toList().forEach { item ->
                if (augmentedImage.name.equals(item.targetImageTag)) {
                    currentItemCollection = item
                    Snackbar.make(
                        arFragment?.requireView() ?: binding.root,
                        "Tag ${currentItemCollection!!.targetImageTag} detected",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            if (!modelDetected) { //это поправить
                modelDetected = true
                clearFutures()
                binding.fabAdd.isEnabled = true
                binding.fabAdd.setOnClickListener {
                    Snackbar.make(
                        arFragment?.requireView() ?: binding.root,
                        "item ${currentItemCollection!!.targetImageTag} added",
                        Toast.LENGTH_LONG
                    ).show()
                }
                anchorNode.worldScale = Vector3(0.2f, 0.2f, 0.2f)
                anchorNode.localRotation = Quaternion.axisAngle(Vector3(1.0f, 0f, 0f), 90f)
                arFragment!!.arSceneView.scene.addChild(anchorNode)
                futures.toMutableList().add(
                    ModelRenderable.builder()
                        .setSource(
                            context,
                            Uri.parse(currentItemCollection?.modelPath ?: "nullModel.glb")
                        )
                        .setIsFilamentGltf(true)
                        .build()
                        .thenAccept { model: ModelRenderable? ->
                            val modelNode = TransformableNode(
                                arFragment!!.transformationSystem
                            )
                            modelNode.renderable = model
                            anchorNode.addChild(modelNode)
                        }
                        .exceptionally {
                            Snackbar.make(
                                arFragment?.requireView() ?: binding.root,
                                "Unable to load model",
                                Toast.LENGTH_LONG
                            ).show()
                            null
                        }
                )
            }
        } else if (augmentedImage.trackingState == TrackingState.PAUSED
            || augmentedImage.trackingState == TrackingState.STOPPED
        ) {
            clearFutures()
            modelDetected = false
        }
    }

    private fun clearFutures() {
        futures.toList().forEach { future ->
            if (!future.isDone) future.cancel(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFutures()
    }
}