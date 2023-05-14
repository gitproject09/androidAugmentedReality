package com.sopan.augmented_reality.drakosha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.AugmentedFaceNode
import com.sopan.augmented_reality.R

class CustomArActivity : AppCompatActivity() {
    private var modelRenderable: ModelRenderable? = null
    private var texture: Texture? = null
    private var isFaceAdded = false
    private var fragment: ArFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_ar)
        fragment = supportFragmentManager.findFragmentById(R.id.fragmentCustomAr) as ArFragment?
        ModelRenderable.builder().setSource(this, R.raw.fox_face).build()
            .thenAccept { modelRenderable1: ModelRenderable? ->
                modelRenderable = modelRenderable1
                modelRenderable!!.isShadowCaster = false
                modelRenderable!!.isShadowReceiver = false
            }
        Texture.builder().setSource(this, R.raw.fox_face_mesh_texture).build()
            .thenAccept { texture1: Texture? -> texture = texture1 }
        fragment!!.arSceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        fragment!!.arSceneView.scene.addOnUpdateListener { frameTime: FrameTime? ->
            val frame = fragment!!.arSceneView.arFrame
            val faces = frame!!.getUpdatedTrackables(AugmentedFace::class.java)
            for (face in faces) {
                if (isFaceAdded) return@addOnUpdateListener
                val faceNode = AugmentedFaceNode(face)
                faceNode.setParent(fragment!!.arSceneView.scene)
                faceNode.faceRegionsRenderable = modelRenderable
                faceNode.faceMeshTexture = texture
                isFaceAdded = true
            }
        }
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, CustomArActivity::class.java))
    }
}