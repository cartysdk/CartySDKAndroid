package io.carty.bidad.demo.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val NATIVE_PLACEMENT_ID = "281217152610"
        const val NATIVE_EXPRESS_PLACEMENT_ID = "281217152611"
        const val INTERSTITIAL_PLACEMENT_ID = "281217152620"
        const val SPLASH_PLACEMENT_ID = "281217152630"
        const val BANNER_PLACEMENT_ID = "281217152640"
        const val REWARD_PLACEMENT_ID = "281217152650"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }


    fun dp2px(width: Int): Int {
        val density = resources.displayMetrics.density
        return (width * density + 0.5f).toInt()
    }

    fun px2dp(px: Int): Int {
        val scale = resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
