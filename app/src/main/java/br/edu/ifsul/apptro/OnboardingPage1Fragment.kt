package br.edu.ifsul.apptro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class OnboardingPage1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboarding_page_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("OnboardingFragment", "onViewCreated FOI CHAMADO!")

        val imageView1 = view.findViewById<ImageView>(R.id.if_normal_logo)
        val imageView2 = view.findViewById<ImageView>(R.id.if_tro_logo)
        val textViewEletronica = view.findViewById<TextView>(R.id.textView)

        Glide.with(this)
            .asGif()
            .load(R.drawable.ifsul_gif)
            .into(imageView1)

        Glide.with(this)
            .asGif()
            .load(R.drawable.tro_gif)
            .into(imageView2)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("OnboardingFragment", "Handler ativado. Iniciando animação.")

            val animationDuration = 1000L

            imageView1.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .start()

            imageView2.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .start()

            textViewEletronica.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .start()

        }, 2000)
    }
}