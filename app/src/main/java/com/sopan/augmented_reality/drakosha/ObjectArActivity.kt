package com.sopan.augmented_reality.drakosha

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.vision.VisionActivity

class ObjectArActivity : AppCompatActivity() {
    private var arFragment: ArFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_ar)
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment?
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            val anchor = hitResult.createAnchor()

            // adding model to the scene
            ModelRenderable.builder()
                .setSource(this, Uri.parse("TocoToucan.sfb"))
                .build()
                .thenAccept { modelRenderable: ModelRenderable ->
                    addModelToScene(
                        anchor,
                        modelRenderable
                    )
                }
        }
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val node = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment!!.transformationSystem) //  for moving, resizing object
        transformableNode.setParent(node) // need to attach to parent
        transformableNode.renderable = modelRenderable
        arFragment!!.arSceneView.scene.addChild(node) // adding only parent node, so the child nodes will be added automatically
        transformableNode.select()
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, ObjectArActivity::class.java))
    }
}