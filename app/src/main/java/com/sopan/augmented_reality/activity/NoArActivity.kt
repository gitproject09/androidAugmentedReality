package com.sopan.augmented_reality.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.databinding.ActivityNoArBinding
import com.sopan.augmented_reality.helpers.createNode
import com.sopan.augmented_reality.helpers.loadRenderable
import com.sopan.augmented_reality.helpers.screenShot


class NoArActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoArBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_ar)
        binding = ActivityNoArBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadRenderable(this, onSuccess = {
            addNodeToScene(it)
        })

        setupControls()
    }

    override fun onResume() {
        super.onResume()
        binding.sceneView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.sceneView.pause()
    }

    private fun setupControls() {
        binding.btnClose.setIconTintResource(R.color.primaryTextColor)
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            binding.sceneView.screenShot()
        }
    }

    private fun addNodeToScene(model: ModelRenderable?) {
        val ts = TransformationSystem(resources.displayMetrics, FootprintSelectionVisualizer())
        model?.let {
            ts.createNode(binding.sceneView.scene, model).apply {
                localPosition = Vector3(0f, 0f, -2f)
                localRotation =
                    Quaternion.lookRotation(Vector3(0.5f, 0f, -0.5f), Vector3(0.5f, 0f, -0.5f))
                localScale = Vector3(0.8f, 0.8f, 0.8f)
                name = "Fox"
            }
        }

        binding.sceneView.scene.addOnPeekTouchListener { hitTestResult, motionEvent ->
            try {
                ts.onTouch(hitTestResult, motionEvent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    companion object {
        fun start(context: Context) =
            context.startActivity(Intent(context, NoArActivity::class.java))
    }
}
