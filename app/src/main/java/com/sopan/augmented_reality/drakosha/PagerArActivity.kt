package com.sopan.augmented_reality.drakosha

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.sopan.augmented_reality.R

class PagerArActivity : AppCompatActivity() {
    private var fragment: ArFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_ar)
        fragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment?
        assert(fragment != null)
        fragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            ViewRenderable
                .builder()
                .setView(this, R.layout.pager)
                .build()
                .thenAccept { viewRenderable: ViewRenderable ->
                    addToScene(
                        viewRenderable,
                        hitResult.createAnchor()
                    )
                }
        }
    }

    private fun addToScene(viewRenderable: ViewRenderable, anchor: Anchor) {
        val node = AnchorNode(anchor)
        node.renderable = viewRenderable
        fragment!!.arSceneView.scene.addChild(node)
        val view = viewRenderable.view
        val pager = view.findViewById<ViewPager>(R.id.viewPager)
        val images: MutableList<Int> = ArrayList()
        images.add(R.drawable.iu)
        images.add(R.drawable.im2)
        images.add(R.drawable.im3)
        images.add(R.drawable.im4)
        images.add(R.drawable.im5)
        val adapter = ArAdapter(images)
        pager.adapter = adapter
    }

    private inner class ArAdapter(var images: List<Int>) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(R.layout.image_item, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            imageView.setImageResource(images[position])
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, PagerArActivity::class.java))
    }
}