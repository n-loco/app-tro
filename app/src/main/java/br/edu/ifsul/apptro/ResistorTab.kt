package br.edu.ifsul.apptro

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class ResistorTab : Fragment() {

    // Views do layout
    private lateinit var band1Image: ImageView
    private lateinit var band2Image: ImageView
    private lateinit var band3Image: ImageView
    private lateinit var band4Image: ImageView
    private lateinit var resistanceValue: TextView
    private lateinit var validationIcon: ImageView

    // Estado atual das faixas (valores -1 indicam não selecionado)
    private var firstBand = -1
    private var secondBand = -1
    private var multiplier = -1
    private var tolerance = -1

    // Mapeamento de cores para valores RGB
    private val colorMap = mapOf(
        0 to R.color.black,
        1 to R.color.brown,
        2 to R.color.red,
        3 to R.color.orange,
        4 to R.color.yellow,
        5 to R.color.green,
        6 to R.color.blue,
        7 to R.color.violet,
        8 to R.color.gray,
        9 to R.color.white,
        10 to R.color.golden,
        11 to R.color.silver
    )

    // Valores padrão da série E12
    private val e12Values = listOf(
        1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2
    )

    // Multiplicadores para cada posição
    private val multipliers = mapOf(
        0 to 1.0,           // x1
        1 to 10.0,          // x10
        2 to 100.0,         // x100
        3 to 1000.0,        // x10³
        4 to 10000.0,       // x10⁴
        5 to 100000.0,      // x10⁵
        6 to 1000000.0,     // x10⁶
        7 to 10000000.0,    // x10⁷
        8 to 100000000.0,   // x10⁸
        9 to 1000000000.0,  // x10⁹
        10 to 0.1,          // x0.1
        11 to 0.01          // x0.01
    )

    // Tolerâncias
    private val tolerances = mapOf(
        1 to 1.0,      // ±1%
        2 to 2.0,      // ±2%
        5 to 0.5,      // ±0.5%
        6 to 0.25,     // ±0.25%
        7 to 0.1,      // ±0.1%
        8 to 0.05,     // ±0.05%
        10 to 5.0,     // ±5%
        11 to 10.0     // ±10%
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_resistor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar views
        band1Image = view.findViewById(R.id.band1_image)
        band2Image = view.findViewById(R.id.band2_image)
        band3Image = view.findViewById(R.id.band3_image)
        band4Image = view.findViewById(R.id.band4_image)
        resistanceValue = view.findViewById(R.id.resistance_value)
        validationIcon = view.findViewById(R.id.validation_icon)

        setupButtons(view)

        // Definir valores padrão para resistor de 1kΩ (marrom, preto, vermelho, dourado)
        setDefaultResistor()
    }

    private fun setDefaultResistor() {
        firstBand = 1    // Marrom
        secondBand = 0   // Preto
        multiplier = 2   // Vermelho (x100)
        tolerance = 10   // Dourado (±5%)

        updateResistorDisplay()
        calculateAndDisplayValue()
    }

    @SuppressLint("DiscouragedApi") // Uso de getIdentifier é necessário devido à estrutura dinâmica
    private fun setupButtons(view: View) {
        val packageName = requireContext().packageName

        // Configurar botões da primeira coluna (primeiro dígito)
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("btn_c1_$i", "id", packageName)
            val button = view.findViewById<MaterialButton>(buttonId)
            button?.setOnClickListener { selectFirstBand(i) }
        }

        // Configurar botões da segunda coluna (segundo dígito)
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("btn_c2_$i", "id", packageName)
            val button = view.findViewById<MaterialButton>(buttonId)
            button?.setOnClickListener { selectSecondBand(i) }
        }

        // Configurar botões da terceira coluna (multiplicador)
        for (i in 0..11) {
            val buttonId = resources.getIdentifier("btn_c3_$i", "id", packageName)
            val button = view.findViewById<MaterialButton>(buttonId)
            button?.setOnClickListener { selectMultiplier(i) }
        }

        // Configurar botões da quarta coluna (tolerância)
        val toleranceButtons = listOf(1, 2, 5, 6, 7, 8, 10, 11)
        for (i in toleranceButtons) {
            val buttonId = resources.getIdentifier("btn_c4_$i", "id", packageName)
            val button = view.findViewById<MaterialButton>(buttonId)
            button?.setOnClickListener { selectTolerance(i) }
        }
    }

    private fun selectFirstBand(value: Int) {
        firstBand = value
        updateResistorDisplay()
        calculateAndDisplayValue()
    }

    private fun selectSecondBand(value: Int) {
        secondBand = value
        updateResistorDisplay()
        calculateAndDisplayValue()
    }

    private fun selectMultiplier(value: Int) {
        multiplier = value
        updateResistorDisplay()
        calculateAndDisplayValue()
    }

    private fun selectTolerance(value: Int) {
        tolerance = value
        updateResistorDisplay()
        calculateAndDisplayValue()
    }

    private fun updateResistorDisplay() {
        // Helper interno para aplicar cor
        fun applyColor(view: ImageView, value: Int) {
            val colorRes = getColorForValue(value)
            val color = ContextCompat.getColor(requireContext(), colorRes)
            view.setColorFilter(color)
        }

        if (firstBand != -1) applyColor(band1Image, firstBand)
        if (secondBand != -1) applyColor(band2Image, secondBand)
        if (multiplier != -1) applyColor(band3Image, multiplier)
        if (tolerance != -1) applyColor(band4Image, tolerance)
    }

    @SuppressLint("SetTextI18n") // Texto formatado dinamicamente para exibição de valores
    private fun calculateAndDisplayValue() {
        if (firstBand == -1 || secondBand == -1 || multiplier == -1) {
            resistanceValue.text = "--- Ω"
            validationIcon.visibility = View.GONE
            return
        }

        val baseValue = (firstBand * 10 + secondBand).toDouble()
        val multiplierValue = multipliers[multiplier] ?: 1.0
        val finalValue = baseValue * multiplierValue

        // Formatação do valor com tolerância
        val formattedValue = formatResistanceValue(finalValue)
        val toleranceText = if (tolerance != -1) {
            " (±${tolerances[tolerance]}%)"
        } else {
            ""
        }

        // Uso de String.format para evitar concatenação direta em setText
        resistanceValue.text = String.format(Locale.getDefault(), "%s%s", formattedValue, toleranceText)

        // Validação E12
        val isValidE12 = validateE12(finalValue)

        validationIcon.visibility = View.VISIBLE

        // Atualizar ícone de validação
        val iconRes = if (isValidE12) R.drawable.ic_check else R.drawable.ic_error
        val colorRes = if (isValidE12) android.R.color.holo_green_dark else android.R.color.holo_red_dark
        val desc = if (isValidE12) "Valor válido na série E12" else "Valor não pertence à série E12"

        validationIcon.setImageResource(iconRes)
        validationIcon.setColorFilter(ContextCompat.getColor(requireContext(), colorRes))
        validationIcon.contentDescription = desc

        // Animação suave na troca dos ícones
        validationIcon.alpha = 0f
        validationIcon.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun formatResistanceValue(value: Double): String {
        val locale = Locale.getDefault()

        if (value == 0.0) {
            return String.format(locale, "%.2f Ω", 0.0)
        }

        return when {
            value >= 1_000_000_000 -> String.format(locale, "%.2f GΩ", value / 1_000_000_000)
            value >= 1_000_000 -> String.format(locale, "%.2f MΩ", value / 1_000_000)
            value >= 1_000 -> String.format(locale, "%.2f kΩ", value / 1_000)
            value >= 1 -> String.format(locale, "%.2f Ω", value)
            value >= 0.001 -> String.format(locale, "%.2f mΩ", value * 1_000)
            else -> String.format(locale, "%.2e Ω", value)
        }
    }

    private fun validateE12(resistance: Double): Boolean {
        if (resistance <= 0) return false

        // Encontrar a potência de 10 mais próxima usando Kotlin Math
        val exponent = floor(log10(resistance))
        val decade = 10.0.pow(exponent)

        // Normalizar o valor para o intervalo [1, 10)
        val normalizedValue = resistance / decade

        // Tolerância padrão para comparação
        val toleranceVal = 0.005

        // Verificar se o valor normalizado está próximo de algum valor E12
        return e12Values.any { e12Value ->
            val relativeError = abs(normalizedValue - e12Value) / e12Value
            relativeError <= toleranceVal
        }
    }

    private fun getColorForValue(value: Int): Int {
        return colorMap[value] ?: R.color.black
    }
}