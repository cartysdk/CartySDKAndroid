package io.carty.bidad.demo.ui

import android.content.Intent
import android.os.Bundle
import io.carty.bidad.demo.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashBtn.setOnClickListener {
            startActivity(AppOpenActivity::class.java)
        }

        binding.rewardBtn.setOnClickListener {
            startActivity(RewardActivity::class.java)
        }

        binding.interstitialBtn.setOnClickListener {
            startActivity(InterstitialActivity::class.java)
        }

        binding.bannerBtn.setOnClickListener {
            startActivity(BannerActivity::class.java)
        }

        binding.nativeBtn.setOnClickListener {
            startActivity(NativeActivity::class.java)
        }
    }

    fun startActivity(clazz: Class<*>) {
        startActivity(Intent(this@MainActivity, clazz))
    }
}