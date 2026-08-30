package br.edu.ifsul.apptro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import br.edu.ifsul.apptro.adapters.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)

        val pagerAdapter = OnboardingAdapter(this)

        viewPager.adapter = pagerAdapter
    }
}