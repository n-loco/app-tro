package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

private const val BUTTON_INTERVAL = 5000L
private const val DEVICE_NAME = "HC-05"

private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

private enum class FenadoceTabState {
    NoConnection,
    Connecting,
    Idle,
    Pulsing,
}

class FenadoceTab(private val activity: Activity) : Fragment() {
    private var state = FenadoceTabState.NoConnection

    private val bluetoothManager = activity.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val mainLoopHandler = Handler(Looper.getMainLooper())

    private lateinit var fenadoceUnsupported: FrameLayout
    private lateinit var okLay: LinearLayout

    private lateinit var connectButton: Button
    private lateinit var pulseButton: Button

    private var engineSocket: BluetoothSocket? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fenadoce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fenadoceUnsupported = view.findViewById<FrameLayout>(R.id.fenadoce_unsupported)
        okLay = view.findViewById<LinearLayout>(R.id.fenadoce_tab_ok)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            fenadoceUnsupported.isVisible = true
            okLay.isVisible = false
            return
        }

        connectButton = view.findViewById<Button>(R.id.fenadoce_connect)
        connectButton.setOnClickListener {
            onConnectClick()
        }

        pulseButton = view.findViewById<Button>(R.id.fenadoce_pulse)
        pulseButton.setOnClickListener {
            onPulseClick()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onConnectClick() {
        val hasBluetoothPermission = ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED

        if (hasBluetoothPermission) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 2
            )
            return
        }

        updateState(FenadoceTabState.Connecting)

        val device = bluetoothAdapter.bondedDevices.find { device ->  device.name.equals(DEVICE_NAME) }
        if (device == null) {
            Toast.makeText(requireContext(), "${DEVICE_NAME} não encontrado", Toast.LENGTH_SHORT).show()
            updateState(FenadoceTabState.NoConnection)
            return
        }

        suspend fun connect(): BluetoothSocket =
            withContext(Dispatchers.IO) {
                val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                socket.connect()
                socket
            }

        lifecycleScope.launch {
            try {
                val socket = connect()
                updateState(FenadoceTabState.Idle)
                engineSocket = socket
            } catch (_: IOException) {
                Toast.makeText(requireContext(), "Não foi possível conectar.", Toast.LENGTH_SHORT).show()
                updateState(FenadoceTabState.NoConnection)
                engineSocket = null
            }
        }
    }

    private fun onPulseClick() {
        if (engineSocket == null) {
            updateState(FenadoceTabState.NoConnection)
            return
        }

        updateState(FenadoceTabState.Pulsing)

        try {
            engineSocket!!.outputStream.write(1)
        } catch (_: IOException) {
            Toast.makeText(requireContext(), "Conexão perdida.", Toast.LENGTH_SHORT).show()
            updateState(FenadoceTabState.NoConnection)
            engineSocket = null
            return
        }

        val pulseEnabler = {
            if (state == FenadoceTabState.Pulsing) {
                updateState(FenadoceTabState.Idle)
            }
        }
        mainLoopHandler.postDelayed(pulseEnabler, BUTTON_INTERVAL)
    }

    private fun updateState(newState: FenadoceTabState) {
        state = newState
        when (newState) {
            FenadoceTabState.NoConnection -> {
                connectButton.isEnabled = true
                connectButton.text = "Conectar"

                pulseButton.isEnabled = false
                pulseButton.text = "Pulso"
            }
            FenadoceTabState.Connecting -> {
                connectButton.isEnabled = false
                connectButton.text = "Conectando..."

                pulseButton.isEnabled = false
                pulseButton.text = "Pulso"
            }
            FenadoceTabState.Idle -> {
                connectButton.isEnabled = false
                connectButton.text = "Conectar"

                pulseButton.isEnabled = true
                pulseButton.text = "Pulso"
            }
            FenadoceTabState.Pulsing -> {
                connectButton.isEnabled = false
                connectButton.text = "Conectar"

                pulseButton.isEnabled = false
                pulseButton.text = "Funcionando..."
            }
        }
    }
}
