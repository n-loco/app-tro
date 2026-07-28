package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class FenadoceTab : Fragment() {
    private val mainLoopHandler = Handler(Looper.getMainLooper())
    private val pulseEnabler = {
        pulseButton.text = "Pulso"
        pulseButton.isEnabled = true
    }
    private lateinit var pulseButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fenadoce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pulseButton = view.findViewById<Button>(R.id.pulse)

        pulseButton.setOnClickListener {
            onPulseClicked()
        }
    }

    private fun onPulseClicked() {
        pulseButton.text = "Funcionando..."
        pulseButton.isEnabled = false

        mainLoopHandler.postDelayed(pulseEnabler, 5000)
    }
}
