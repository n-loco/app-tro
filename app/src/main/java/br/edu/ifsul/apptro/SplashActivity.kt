package br.edu.ifsul.apptro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val appTitle: TextView = findViewById(R.id.app_intro)

        // Animação inicial do título
        appTitle.alpha = 0f
        appTitle.translationX = -200f
        appTitle.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(1500)
            .start()

        // Handler para aguardar o tempo da Splash e decidir o destino
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val onboardingCompleted = sharedPref.getBoolean("onboarding_completed", false)

            val intent = if (onboardingCompleted) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, splashDelay)
    }
}