package com.sopan.augmented_reality.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.BaseArFragment
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.databinding.ActivityArBinding
import com.sopan.augmented_reality.helpers.createAnchor
import com.sopan.augmented_reality.helpers.createNode
import com.sopan.augmented_reality.helpers.screenShot

@SuppressLint("Registered")
abstract class BaseArActivity : AppCompatActivity() {

    abstract val arFragment: BaseArFragment
    lateinit var renderable: ModelRenderable
    lateinit var binding: ActivityArBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArBinding.inflate(layoutInflater)
        //we will be able to access included layouts view like this
       // val includedView: View = binding.yourId.idOfIncludedView

        if (!checkIsSupportedDeviceOrFinish())
            return

        setContentView(binding.root)
        setupControls()

        val path = Path().apply {
            addCircle(
                250f,
                resources.displayMetrics.heightPixels.div(2.0f),
                100f,
                Path.Direction.CCW
            )
        }
        ObjectAnimator.ofFloat(binding.handView, View.X, View.Y, path).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            duration = 1600
            start()
        }
    }

    private fun setupControls() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            arFragment.arSceneView.screenShot()
        }
    }

    protected fun replaceFragment(onCommit: (() -> Unit)? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_ar, arFragment)
            .runOnCommit {
                onCommit?.invoke()
            }
            .commit()
    }

    protected fun hasRendered() = ::renderable.isInitialized

    protected fun placeNode(anchor: Anchor, scale: Float = 0.2f) {
        val anchorNode = arFragment.arSceneView.scene.createAnchor(anchor)
        arFragment.transformationSystem.createNode(anchorNode, renderable, scale)
    }

    protected fun showMessage(@StringRes message: Int) {
        binding.txtMessage.visibility = View.VISIBLE
        binding.txtMessage.setText(message)
    }

    protected fun showMessage(message: String) {
        binding.txtMessage.visibility = View.VISIBLE
        binding.txtMessage.text = message
    }

    private fun checkIsSupportedDeviceOrFinish(): Boolean {
        val openGlVersionString =
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(
                this,
                R.string.error_opengl_min_version,
                Toast.LENGTH_LONG
            ).show()
            finish()
            return false
        }
        return true
    }

    companion object {
        private const val MIN_OPENGL_VERSION = 3.0
        private const val TAG = "AR Activity"
    }
}