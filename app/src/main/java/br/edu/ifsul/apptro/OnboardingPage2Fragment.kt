package br.edu.ifsul.apptro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment


class OnboardingPage2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.onboarding_page_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("OnboardingFragment2", "onViewCreated FOI CHAMADO!")

        val lampOff = view.findViewById<ImageView>(R.id.imageView2)
        val lampOn = view.findViewById<ImageView>(R.id.imageView3)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("OnboardingFragment2", "Handler ativado. Iniciando animação da lâmpada.")

            lampOff.animate()
                .alpha(0f)
                .setDuration(1500)
                .start()

            lampOn.animate()
                .alpha(1f)
                .setDuration(1500)
                .start()

        }, 2000)
    }
}