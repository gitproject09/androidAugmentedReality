package com.sopan.augmented_reality.drakosha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.sceneform.ux.ArFragment

class GameArFragment : ArFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = super.onCreateView(inflater, container, savedInstanceState) as FrameLayout?

        // for hiding image with phone for detecting plane
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return layout
    }
}