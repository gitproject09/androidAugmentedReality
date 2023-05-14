package com.sopan.augmented_reality.vision

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.activity.CloudAnchorActivity
import java.io.IOException

class VisionActivity : AppCompatActivity() {
    var cameraView: SurfaceView? = null
    var textView: TextView? = null
    var cameraSource: CameraSource? = null
    var swapButton: ImageButton? = null
    var expressionEditText: EditText? = null
    var layout: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_vision)
        cameraView = findViewById(R.id.surfaceView)
        textView = findViewById(R.id.textView)
        swapButton = findViewById(R.id.swapButton)
        expressionEditText = findViewById(R.id.expressionEditText)
        layout = findViewById(R.id.relativeLayout)
        swapButton?.setOnClickListener(View.OnClickListener { view: View? ->
            if (cameraView?.getVisibility() == View.VISIBLE) {
                cameraSource?.stop()
                cameraView?.setVisibility(View.GONE)
                layout?.setVisibility(View.VISIBLE)
            } else {
                layout?.setVisibility(View.GONE)
                cameraView?.setVisibility(View.VISIBLE)
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnClickListener
                    }
                    cameraSource?.start(cameraView?.getHolder())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            textView?.setText("")
        })
        expressionEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    textView?.setText(MathEvaluation(s.toString().replace("\n", "")).parse())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        startCameraSource()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionID && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                cameraSource?.start(cameraView?.holder)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCameraSource() {
        val textRecognizer = TextRecognizer.Builder(applicationContext).build()
        if (textRecognizer.isOperational) {
            cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(4.0f)
                .build()
            cameraView?.holder?.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@VisionActivity, arrayOf(Manifest.permission.CAMERA),
                                requestPermissionID
                            )
                        }
                        cameraSource?.start(cameraView?.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraSource?.stop()
                }
            })
            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {}
                override fun receiveDetections(detections: Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {
                        textView?.post {
                            try {
                                textView?.text = MathEvaluation(items.valueAt(0).value).parse()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {
        private const val requestPermissionID = 101
        fun start(context: Context) = context.startActivity(Intent(context, VisionActivity::class.java))
    }
}