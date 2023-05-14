package com.sopan.augmented_reality.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.fragment.CloudAnchorFragment
import com.sopan.augmented_reality.helpers.AnchorManager
import com.sopan.augmented_reality.helpers.AnchorState
import com.sopan.augmented_reality.helpers.loadRenderable

class CloudAnchorActivity : BaseArActivity() {

    override val arFragment = CloudAnchorFragment()
    private var cloudAnchor: Anchor? = null
    private var state = AnchorState.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment {
            setupPlaneDiscovery()
            hostCloudAnchor()
            arFragment.arSceneView.scene.addOnUpdateListener {
                checkState()
            }
        }

        loadRenderable(this, onSuccess = ::renderable.setter)
        renderCloudView()
    }

    private fun setupPlaneDiscovery() {
        arFragment.planeDiscoveryController.hide()
        arFragment.planeDiscoveryController.setInstructionView(binding.instructionView)
        arFragment.planeDiscoveryController.show()
    }

    private fun renderCloudView() {
        val view = layoutInflater.inflate(R.layout.cloud_anchor, binding.container)

        val btnClear = view.findViewById<ExtendedFloatingActionButton>(R.id.btnClear)
        btnClear.setOnClickListener {
            clearCloudAnchor()
        }

        val btnRetrieve = view.findViewById<ExtendedFloatingActionButton>(R.id.btnRetrieve)
        btnRetrieve.setOnClickListener {
            resolveCloudAnchor()
        }
    }

    private fun cloudAnchor(newAnchor: Anchor?) {
        cloudAnchor?.detach()
        cloudAnchor = newAnchor
        state = AnchorState.NONE
    }

    private fun hostCloudAnchor() {
        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING)
                return@setOnTapArPlaneListener

            val anchor = arFragment.arSceneView.session?.hostCloudAnchor(hitResult.createAnchor())
                ?: return@setOnTapArPlaneListener
            cloudAnchor(anchor)
            state = AnchorState.HOSTING
            showMessage(R.string.message_hosting_anchor)
            placeNode(anchor)
        }
    }

    private fun resolveCloudAnchor() {
        if (cloudAnchor != null) {
            showMessage(R.string.error_clear_anchor)
            return
        }

        buildDialog()
    }

    private fun buildDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_input, null)
        val inputCode = view.findViewById<ExtendedFloatingActionButton>(R.id.inputCode)

        AlertDialog.Builder(this, R.style.Theme_MaterialComponents_DayNight_Dialog)
            .setTitle(R.string.title_resolve_anchor)
            .setMessage(R.string.text_resolve_anchor)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                startResolving(inputCode.text.toString())
                dialog.dismiss()
            }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }.show()
    }

    private fun startResolving(code: String) {
        try {
            val cloudId = AnchorManager.getCloudAnchorId(code)
            val resolvedCloudAnchor =
                arFragment.arSceneView.session?.resolveCloudAnchor(cloudId) ?: return
            cloudAnchor(resolvedCloudAnchor)
            state = AnchorState.RESOLVING
            showMessage(R.string.message_resolving_anchor)
            placeNode(resolvedCloudAnchor)
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(R.string.error_resolved_anchor)
        }
    }

    private fun clearCloudAnchor() {
        cloudAnchor(null)
        binding.txtMessage.visibility = View.GONE
    }

    private fun checkState() {
        val anchorState = cloudAnchor?.cloudAnchorState
        when (state) {
            AnchorState.RESOLVING -> {
                if (anchorState?.isError == true) {
                    showMessage(R.string.error_resolved_anchor)
                } else if (anchorState == Anchor.CloudAnchorState.SUCCESS) {
                    state = AnchorState.RESOLVED
                    showMessage(R.string.message_resolved_anchor)
                }
            }
            AnchorState.HOSTING -> {
                if (anchorState?.isError == true) {
                    showMessage(R.string.error_hosted_anchor)
                } else if (anchorState == Anchor.CloudAnchorState.SUCCESS) {
                    val id = cloudAnchor?.cloudAnchorId ?: return
                    val code = AnchorManager.saveCloudAnchorId(id)
                    state = AnchorState.HOSTED
                    showMessage(getString(R.string.message_host_anchor, code))
                }
            }
            else -> return
        }
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, CloudAnchorActivity::class.java))
    }
}