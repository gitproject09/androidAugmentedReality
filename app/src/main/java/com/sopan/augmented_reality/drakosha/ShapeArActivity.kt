package com.sopan.augmented_reality.drakosha

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.sopan.augmented_reality.R

class ShapeArActivity : AppCompatActivity() {
    private var fragment: ArFragment? = null
    private var cube: Button? = null
    private var sphere: Button? = null
    private var cylinder: Button? = null

    enum class ObjectType {
        CUBE, SPHERE, CYLINDER
    }

    var objectType = ObjectType.CUBE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shape_ar)
        fragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment?
        cube = findViewById(R.id.cube)
        cube?.setOnClickListener(View.OnClickListener { view: View? ->
            objectType = ObjectType.CUBE
        })
        sphere = findViewById(R.id.sphere)
        sphere?.setOnClickListener(View.OnClickListener { view: View? ->
            objectType = ObjectType.SPHERE
        })
        cylinder = findViewById(R.id.cylinder)
        cylinder?.setOnClickListener(View.OnClickListener { view: View? ->
            objectType = ObjectType.CYLINDER
        })
        fragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            when (objectType) {
                ObjectType.CUBE -> createCube(hitResult.createAnchor())
                ObjectType.SPHERE -> createSphere(hitResult.createAnchor())
                ObjectType.CYLINDER -> createCylinder(hitResult.createAnchor())
                else -> {}
            }
        }
    }

    private fun createCylinder(anchor: Anchor) {
        MaterialFactory
            .makeOpaqueWithColor(this, Color(Color.rgb(255, 99, 71)))
            .thenAccept { material: Material? ->
                val modelRenderable =
                    ShapeFactory.makeCylinder(0.1f, 0.2f, Vector3(0f, 0f, 0f), material)
                placeModel(modelRenderable, anchor)
            }
    }

    private fun createSphere(anchor: Anchor) {
        MaterialFactory
            .makeOpaqueWithColor(this, Color(Color.rgb(175, 238, 238)))
            .thenAccept { material: Material? ->
                val modelRenderable = ShapeFactory.makeSphere(0.1f, Vector3(0f, 0f, 0f), material)
                placeModel(modelRenderable, anchor)
            }
    }

    private fun createCube(anchor: Anchor) {
        MaterialFactory
            .makeOpaqueWithColor(this, Color(Color.rgb(250, 128, 114)))
            .thenAccept { material: Material? ->
                val modelRenderable =
                    ShapeFactory.makeCube(Vector3(0.1f, 0.1f, 0.1f), Vector3(0f, 0f, 0f), material)
                placeModel(modelRenderable, anchor)
            }
    }

    private fun placeModel(modelRenderable: ModelRenderable, anchor: Anchor) {
        val node = AnchorNode(anchor)
        val transformableNode = TransformableNode(fragment!!.transformationSystem)
        transformableNode.setParent(node)
        transformableNode.renderable = modelRenderable
        fragment!!.arSceneView.scene.addChild(node)
        transformableNode.select()
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, ShapeArActivity::class.java))
    }
}