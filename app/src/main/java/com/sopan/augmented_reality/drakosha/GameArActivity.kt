package com.sopan.augmented_reality.drakosha

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.sopan.augmented_reality.R
import java.util.*

class GameArActivity : AppCompatActivity() {
    private var scene: Scene? = null
    private var camera: Camera? = null
    private var bulletRenderable: ModelRenderable? = null
    private var timerStarted = false
    private var balloonsLeft = 20
    private var point: Point? = null
    private var balloonsLeftText: TextView? = null
    private var soundPool: SoundPool? = null
    private var sound = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display = windowManager.defaultDisplay
        point = Point()
        display.getRealSize(point)
        setContentView(R.layout.activity_game_ar)
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragmentModelAr) as GameArFragment?
        scene = fragment!!.arSceneView.scene
        camera = scene?.getCamera()
        setBallonsToTheScene()
        builtBullet()
        balloonsLeftText = findViewById(R.id.balloonsCount)
        loadSoundPool()
        val kill = findViewById<Button>(R.id.kill)
        kill.setOnClickListener { view: View? ->
            if (!timerStarted) {
                startTimer()
                timerStarted = true
            }
            shoot()
        }
    }

    private fun loadSoundPool() {
        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build()
        sound = soundPool!!.load(this, R.raw.blop_sound, 1)
    }

    private fun shoot() {
        val ray = camera!!.screenPointToRay(point!!.x / 2f, point!!.y / 2f)
        val node = Node()
        node.renderable = bulletRenderable
        scene!!.addChild(node)
        Thread {
            for (i in 0..99) {
                runOnUiThread {
                    val v = ray.getPoint(i * 0.2f)
                    node.worldPosition = v
                    val nodeContact = scene!!.overlapTest(node)
                    if (nodeContact != null) {
                        balloonsLeft--
                        balloonsLeftText!!.text = "Left : $balloonsLeft"
                        scene!!.removeChild(nodeContact)
                        scene!!.removeChild(node)
                        soundPool!!.play(sound, 1f, 1f, 1, 0, 1f)
                    }
                }
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            runOnUiThread { scene!!.removeChild(node) }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        val timer = findViewById<TextView>(R.id.timer)
        Thread {
            var seconds = 0
            while (balloonsLeft > 0) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                seconds++
                val mins = seconds / 60
                val secs = seconds % 60
                runOnUiThread { timer.text = "$mins : $secs" }
            }
        }.start()
    }

    private fun builtBullet() {
        Texture.builder()
            .setSource(this, R.drawable.texture)
            .build()
            .thenAccept { texture: Texture? ->
                MaterialFactory.makeOpaqueWithTexture(this, texture)
                    .thenAccept { material: Material? ->
                        bulletRenderable =
                            ShapeFactory.makeSphere(0.02f, Vector3(0f, 0f, 0f), material)
                    }
            }
    }

    private fun setBallonsToTheScene() {
        ModelRenderable.builder()
            .setSource(this, Uri.parse("model.sfb"))
            .build()
            .thenAccept { modelRenderable: ModelRenderable? ->
                for (i in 0..9) {
                    val node = Node()
                    node.renderable = modelRenderable
                    val random = Random()
                    val x = random.nextInt(8) - 4f
                    val y = random.nextInt(2).toFloat()
                    val z = random.nextInt(4).toFloat()
                    val position = Vector3(x, y, -z - 5f)
                    val worldPosition = scene!!.camera.worldPosition
                    node.worldPosition = position
                    node.localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 230f)
                    scene!!.addChild(node)
                }
            }
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, GameArActivity::class.java))
    }
}