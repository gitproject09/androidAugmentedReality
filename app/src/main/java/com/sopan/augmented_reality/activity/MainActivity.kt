package com.sopan.augmented_reality.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.sopan.augmented_reality.R
import com.sopan.augmented_reality.adapter.MainAdapter
import com.sopan.augmented_reality.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        maybeEnableButtons()
        setCreditsLinks()
    }

    private fun maybeEnableButtons() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        if (availability.isTransient)
            Handler().postDelayed({ maybeEnableButtons() }, 200)

        binding.mainItems.adapter = MainAdapter(availability.isSupported)
    }

    private fun setCreditsLinks() {
        val text = SpannableString(getString(R.string.credits_text))
        text.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.flaticon.com/")
                        )
                    )
                }
            },
            text.indexOf("FlatIcon", ignoreCase = true),
            text.indexOf("FlatIcon", ignoreCase = true) + "FlatIcon".length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        text.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://poly.google.com")))
                }
            },
            text.indexOf("Poly by Google", ignoreCase = true),
            text.indexOf("Poly by Google", ignoreCase = true) + "Poly by Google".length,
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        text.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/gitproject09/androidAugmentedReality")))
                }
            },
            text.indexOf("Github", ignoreCase = true), text.indexOf("Github", ignoreCase = true) + "Github".length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        )
        binding.creditsText.movementMethod = LinkMovementMethod.getInstance();
        binding.creditsText.text = text

        binding.btnPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy))))
        }
    }
}
