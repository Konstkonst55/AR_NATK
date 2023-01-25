package com.example.ar_natk.presentation.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.ar_natk.R
import com.example.ar_natk.data.models.ItemModel
import com.example.ar_natk.data.storage.UserDataStorage
import com.example.ar_natk.databinding.FragmentCameraBinding
import com.example.ar_natk.presentation.core.MainActivity
import com.example.ar_natk.utils.Constants
import com.example.ar_natk.utils.toBitmap
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class CameraFragment :
    Fragment(),
    BaseArFragment.OnSessionConfigurationListener {

    private val nullModelPath = "nullModel.glb"

    private var modelDetected = false
    private var itemCollectionList: ArrayList<ItemModel> = ArrayList()
    private var currentItemCollection: ItemModel? = null

    private lateinit var arFragment: ArFragment
    private lateinit var anchorNode: AnchorNode
    private lateinit var binding: FragmentCameraBinding
    private lateinit var database: AugmentedImageDatabase
    private lateinit var bsbInfo: BottomSheetBehavior<ConstraintLayout>

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        bsbInfo = BottomSheetBehavior.from(binding.incBottomSheet.root)
        bsbInfo.state = BottomSheetBehavior.STATE_HIDDEN
        buttonsIsEnabled(false)

        (requireActivity() as MainActivity).supportActionBar?.hide()

        if (Sceneform.isSupported(context)) {
            initItems()
            arFragment = childFragmentManager.findFragmentById(R.id.ArFragment) as ArFragment
            arFragment.setOnSessionConfigurationListener(this)
        }

        binding.bAdd.setOnClickListener {
            showProgress()
            saveItem(currentItemCollection?.id)
        }

        binding.bInfo.setOnClickListener {
            with(binding.incBottomSheet) {
                tvHeaderInfo.text = currentItemCollection?.infoHeader
                tvInfo.text = currentItemCollection?.infoText
                ivImageInfo.setImageBitmap(
                    currentItemCollection?.previewImage?.toBitmap(
                        requireContext()
                    )
                )
            }

            arFragment.pause()
            //binding.ArFragment.visibility = View.INVISIBLE

            bsbInfo.state = if (bsbInfo.state == BottomSheetBehavior.STATE_EXPANDED)
                BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
        }

        bsbInfo.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    arFragment.resume()
                    //binding.ArFragment.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.incBottomSheet.iLink.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentItemCollection?.wikiUrl)))
        }

        return binding.root
    }

    private fun saveItem(id: Int?) { //todo переделать на локальное
        val setIds = HashSet<String>()
        setIds.addAll(UserDataStorage(requireContext()).collection!!)
        setIds.add(id.toString())
        UserDataStorage(requireContext()).collection = setIds
        saveToFirestore(setIds)
    }

    private fun saveToFirestore(hashSet: Set<String>?) {
        Firebase.firestore.collection(Constants.FIRE_COLLECTION_USER)
            .document(UserDataStorage(requireContext()).userId.toString())
            .update(
                mapOf(
                    Constants.FIRE_DOC_USER_COLLECTION to hashSet?.toList(),
                    Constants.FIRE_DOC_USER_SCORE to hashSet?.size.toString()
                )
            )
            .addOnSuccessListener {
                hideProgress()
                showSnackbar("Добавлено")
                Log.i(Constants.LOG_FIREBASE, "Добавлен лист $hashSet")
            }
            .addOnFailureListener {
                hideProgress()
                showSnackbar("Что-то пошло не так :(")
                Log.i(Constants.LOG_FIREBASE, it.message.toString())
            }
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showProgress() {
        arFragment.pause()
        binding.pbCameraLoader.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        arFragment.resume()
        binding.pbCameraLoader.visibility = View.GONE
    }

    private fun initItems() {
        itemCollectionList = Json.decodeFromString(readAssets())
    }

    private fun readAssets(): String {
        return requireContext().assets.open(Constants.MODEL_ITEM_FILE_PATH)
            .bufferedReader()
            .use { it.readText() }
    }

    override fun onSessionConfiguration(session: Session?, config: Config?) {
        config!!.planeFindingMode = Config.PlaneFindingMode.DISABLED
        config.lightEstimationMode = Config.LightEstimationMode.DISABLED

        database = AugmentedImageDatabase(session)

        itemCollectionList.toList().forEach { item ->
            database.addImage(
                item.targetImageTag,
                item.targetImage.toBitmap(requireContext())
            )
        }

        config.augmentedImageDatabase = database

        arFragment.setOnAugmentedImageUpdateListener(this::onAugmentedImageTrackingUpdate)
    }

    private fun onAugmentedImageTrackingUpdate(augmentedImage: AugmentedImage) {
        if (augmentedImage.trackingState == TrackingState.TRACKING
            && augmentedImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING
        ) {
            if (modelDetected) {
                arFragment.instructionsController.setEnabled(
                    InstructionsController.TYPE_AUGMENTED_IMAGE_SCAN,
                    false
                )
                return
            }

            itemCollectionList.toList().forEach { item ->
                if (augmentedImage.name.equals(item.targetImageTag)) {
                    currentItemCollection = item
                    buttonsIsEnabled(true)

                    Snackbar.make(
                        arFragment.requireView(),
                        "Tag ${currentItemCollection?.targetImageTag} detected",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            anchorNode = AnchorNode(augmentedImage.createAnchor(augmentedImage.centerPose))

            if (!modelDetected) { //это поправить
                modelDetected = true

                anchorNode.worldScale = Vector3(0.2f, 0.2f, 0.2f)
                anchorNode.localRotation = Quaternion.axisAngle(Vector3(1.0f, 0f, 0f), 90f)
                arFragment.arSceneView.scene.addChild(anchorNode)

                ModelRenderable.builder()
                    .setSource(
                        context,
                        Uri.parse(
                            currentItemCollection?.modelPath ?: nullModelPath
                        ) //todo сделать нулевую модельку
                    )
                    .setIsFilamentGltf(true)
                    .build()
                    .thenAccept { model: ModelRenderable? ->
                        val modelNode = TransformableNode(arFragment.transformationSystem)
                        modelNode.renderable = model
                        anchorNode.addChild(modelNode)
                    }
                    .exceptionally {
                        Snackbar.make(
                            arFragment.requireView(),
                            "Unable to load model",
                            Snackbar.LENGTH_LONG
                        ).show()
                        null
                    }
            }
        } else if (augmentedImage.trackingState == TrackingState.PAUSED
            || augmentedImage.trackingState == TrackingState.STOPPED
        ) {
            modelDetected = false
        }
    }

    private fun buttonsIsEnabled(enabled: Boolean) {
        with(binding) {
            bInfo.isEnabled = enabled
            bAdd.isEnabled = enabled
        }
    }

}