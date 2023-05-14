package com.sopan.augmented_reality.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.activity.AugmentedImageActivity
import com.sopan.augmented_reality.activity.CloudAnchorActivity
import com.sopan.augmented_reality.activity.NoArActivity
import com.sopan.augmented_reality.activity.ObjectPlacementActivity
import com.sopan.augmented_reality.drakosha.*
import com.sopan.augmented_reality.vision.VisionActivity

class MainAdapter(private val hasAr: Boolean = false) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.main_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = typesCount

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    private val typesCount: Int
        get() = if (hasAr) 10 else 1

    private val typeNoAr: Int
        get() = if (hasAr) 2 else 0

    companion object {
        private const val TYPE_OBJECT_PLACEMENT = 0
        private const val TYPE_AUGMENTED_IMAGE = 1
        private const val TYPE_CLOUD_ANCHOR = 3
        private const val TYPE_VISION = 4
        private const val TYPE_OJECT_PUT = 5
        private const val TYPE_SHAPE = 6
        private const val TYPE_PAGER = 7
        private const val TYPE_CUSTOM = 8
        private const val TYPE_GAME = 9

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<MaterialTextView>(R.id.title)

        fun bind() = with(itemView) {
            when (bindingAdapterPosition) {
                TYPE_OBJECT_PLACEMENT -> {
                    title.setText(R.string.title_object_placement)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_object_placement,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        ObjectPlacementActivity.start(context)
                    }
                }
                TYPE_AUGMENTED_IMAGE -> {
                    title.setText(R.string.title_augmented_image)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_augmented_image,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        AugmentedImageActivity.start(context)
                    }
                }
                typeNoAr -> {
                    title.setText(R.string.title_no_ar)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_no_ar,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        NoArActivity.start(context)
                    }
                }
                TYPE_CLOUD_ANCHOR -> {
                    title.setText(R.string.title_cloud_anchor)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_cloud_anchor,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        CloudAnchorActivity.start(context)
                    }
                }
                TYPE_VISION -> {
                    title.setText(R.string.title_vision)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_augmented_image,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        VisionActivity.start(context)
                    }
                }
                TYPE_OJECT_PUT -> {
                    title.setText(R.string.title_object_put)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_cloud_anchor,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        ObjectArActivity.start(context)
                    }
                }
                TYPE_SHAPE -> {
                    title.setText(R.string.title_shape)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_cached,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        ShapeArActivity.start(context)
                    }
                }
                TYPE_PAGER -> {
                    title.setText(R.string.title_pager)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_augmented_image,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        PagerArActivity.start(context)
                    }
                }
                TYPE_CUSTOM -> {
                    title.setText(R.string.title_custom)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_no_ar,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        CustomArActivity.start(context)
                    }
                }
                TYPE_GAME -> {
                    title.setText(R.string.title_game_ar)
                    title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_augmented_face,
                        0,
                        0
                    )
                    title.setOnClickListener {
                        GameArActivity.start(context)
                    }
                }
            }
        }
    }
}