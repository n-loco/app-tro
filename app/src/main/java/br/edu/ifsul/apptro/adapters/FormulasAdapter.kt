package br.edu.ifsul.apptro.adapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.R
import br.edu.ifsul.apptro.models.FormulaX
import br.edu.ifsul.apptro.utils.FavoritesManager
import java.io.BufferedReader
import java.io.InputStreamReader

class FormulasAdapter(
    private val context: Context,
    private val formulas: List<FormulaX>,
    formulaFocoIndice: Int,
    private val onFormulaClick: (FormulaX) -> Unit
) : RecyclerView.Adapter<FormulasAdapter.FormulaViewHolder>() {

    private val targetPosition = formulaFocoIndice
    private var animationId = System.currentTimeMillis()
    private var animatedId: Long? = null

    // Cor de fundo padrão para animações de destaque
    private val defaultBackgroundColor: Int by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceInverse, typedValue, true)
        if (typedValue.resourceId != 0) {
            ContextCompat.getColor(context, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }

    // Carrega o template HTML apenas uma vez
    private val htmlKatexTemplate: String by lazy {
        loadKatexTemplate(context)
    }

    private fun loadKatexTemplate(context: Context): String {
        return try {
            val inputStream = context.assets.open("katex_renderer.html")
            BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            "<html><body>Erro ao carregar template KaTeX: ${e.message}</body></html>"
        }
    }

    // Obtém a cor do texto primário para ajustar o LaTeX ao tema (Claro/Escuro)
    private fun getTextColorPrimaryHex(): String {
        return try {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            val colorInt = if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT
            ) {
                typedValue.data
            } else {
                ContextCompat.getColor(context, typedValue.resourceId)
            }
            String.format("#%06X", 0xFFFFFF and colorInt)
        } catch (e: Exception) {
            Log.e("FormulasAdapter", "Erro ao obter textColorPrimary: ${e.message}")
            val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (nightMode) {
                Configuration.UI_MODE_NIGHT_YES -> "#FFFFFF"
                Configuration.UI_MODE_NIGHT_NO -> "#000000"
                else -> "#000000"
            }
        }
    }

    inner class FormulaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val favoriteButton: ImageButton = view.findViewById(R.id.btn_favorite)
        private val variablesListTextView: TextView = view.findViewById(R.id.tv_variables_list)
        private val constantsListTextView: TextView = view.findViewById(R.id.tv_constants_list)
        private val separatorView: View = view.findViewById(R.id.separator_variables_constants)

        val cardView: CardView = view as CardView
        val contentLayout: LinearLayout = view.findViewById(R.id.contentLayout)
        val formulaName: TextView = view.findViewById(R.id.tv_formula_name)
        val formulaDescription: TextView = view.findViewById(R.id.tv_formula_description)
        val formulaWebView: WebView = view.findViewById(R.id.webview_latex_formula)
        private val expandableContentLayout: LinearLayout = view.findViewById(R.id.layout_expandable_content)
        private val expandStatusTextView: TextView = view.findViewById(R.id.tv_expand_status)
        private val variablesHeaderTextView: TextView = view.findViewById(R.id.tv_variables_header)
        private val constantsHeaderTextView: TextView = view.findViewById(R.id.tv_constants_header)

        private var isFormulaRendered = false
        private var isWebViewSetupDone = false
        lateinit var currentFormula: FormulaX

        // Configurações básicas do WebView para suportar JS e KaTeX
        @SuppressLint("SetJavaScriptEnabled")
        fun setupWebViewDefaults(webView: WebView) {
            if (isWebViewSetupDone) return

            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.setBackgroundColor(0x00000000) // Fundo transparente
            webView.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d("KaTeX_WebView_JS", "JS Console: \"${it.message()}\" -- (Source: ${it.sourceId()}, Line: ${it.lineNumber()})")
                    }
                    return true
                }
            }
            isWebViewSetupDone = true
        }

        fun bind(formula: FormulaX) {
            currentFormula = formula
            formulaName.text = formula.name
            formulaDescription.text = formula.description

            if (!isWebViewSetupDone) {
                setupWebViewDefaults(formulaWebView)
            }

            variablesListTextView.text = formatTermsForDisplay(formula.variables)
            constantsListTextView.text = formatTermsForDisplay(formula.constants)

            isFormulaRendered = false
            updateExpandCollapseUI(formula)

            // Expande ou recolhe o card ao clicar
            cardView.setOnClickListener {
                formula.isExpanded = !formula.isExpanded
                notifyItemChanged(adapterPosition)
                onFormulaClick(formula)
            }

            Log.d("FormulasAdapter", "Bind - Fórmula: '${formula.name}', ID Único: '${formula.getUniqueId()}', isFavorite: ${formula.isFavorite}")

            updateFavoriteIcon(formula)

            // Gerencia clique no botão de favorito
            favoriteButton.setOnClickListener {
                FavoritesManager.toggleFormulaFavorite(context, formula)
                formula.isFavorite = !formula.isFavorite
                updateFavoriteIcon(formula)
            }
        }

        private fun updateFavoriteIcon(formula: FormulaX) {
            if (formula.isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_star_filled)
                favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.golden))
            } else {
                favoriteButton.setImageResource(R.drawable.ic_star_border)
                favoriteButton.clearColorFilter()
            }
        }

        // Controla visibilidade dos elementos expandidos
        fun updateExpandCollapseUI(formula: FormulaX) {
            if (formula.isExpanded) {
                expandableContentLayout.visibility = View.VISIBLE
                expandStatusTextView.text = context.getString(R.string.collapse)
                expandStatusTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)

                // Renderiza LaTeX se houver
                if (currentFormula.latex.isNotEmpty()) {
                    formulaWebView.visibility = View.VISIBLE
                    if (!isFormulaRendered) {
                        renderFormulaInWebView(currentFormula)
                    }
                } else {
                    formulaWebView.visibility = View.GONE
                }

                // Controla visibilidade de variáveis e constantes
                val variablesPresent = variablesListTextView.text.isNotBlank()
                variablesHeaderTextView.visibility = if (variablesPresent) View.VISIBLE else View.GONE
                variablesListTextView.visibility = if (variablesPresent) View.VISIBLE else View.GONE

                val constantsPresent = constantsListTextView.text.isNotBlank()
                constantsHeaderTextView.visibility = if (constantsPresent) View.VISIBLE else View.GONE
                constantsListTextView.visibility = if (constantsPresent) View.VISIBLE else View.GONE

                separatorView.visibility = if (variablesPresent && constantsPresent) View.VISIBLE else View.GONE
            } else {
                expandableContentLayout.visibility = View.GONE
                expandStatusTextView.text = context.getString(R.string.expand)
                expandStatusTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
        }

        // Formata lista de termos (variáveis/constantes) em negrito
        private fun formatTermsForDisplay(termsMap: Map<String, String>?): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            termsMap?.forEach { (symbol, description) ->
                if (builder.isNotEmpty()) {
                    builder.append("\n")
                }
                val start = builder.length
                builder.append("$symbol: ")
                builder.setSpan(StyleSpan(Typeface.BOLD), start, builder.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append(description)
            }
            return builder
        }

        // Injeta o código JS para renderizar a fórmula no WebView
        private fun renderFormulaInWebView(formula: FormulaX) {
            formulaWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (formula.latex.isNotEmpty()) {
                        val latexToRender = formula.latex.joinToString(" \\\\\\\\ ")
                        val escapedLatex = latexToRender.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n")
                        val formulaTextColor = this@FormulasAdapter.getTextColorPrimaryHex()
                        val jsCommand = "javascript:clearFormula(); setPageTextColor('${formulaTextColor}'); displayFormula('${escapedLatex}');"
                        Log.d("KaTeX_JS_Command", "Executando JS: $jsCommand")
                        view?.evaluateJavascript(jsCommand, null)
                        isFormulaRendered = true
                    } else {
                        view?.evaluateJavascript("javascript:clearFormula();", null)
                        isFormulaRendered = false
                    }
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    val errorDescription = "${error?.errorCode} ${error?.description}"
                    val urlString = request?.url?.toString() ?: "Unknown URL"
                    Log.e("KaTeX_WebView_Error", "Erro ao carregar WebView ($urlString): $errorDescription")
                    isFormulaRendered = false
                }
            }
            formulaWebView.loadDataWithBaseURL("file:///android_asset/", htmlKatexTemplate, "text/html", "UTF-8", null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormulaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_formula, parent, false)
        return FormulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormulaViewHolder, position: Int) {
        val formula = formulas[position]
        formula.isFavorite = FavoritesManager.isFormulaFavorite(context, formula)

        holder.bind(formula)

        holder.contentLayout.setBackgroundColor(defaultBackgroundColor)
        holder.contentLayout.tag = null

        if (position == targetPosition && animatedId == null) {
            animatedId = animationId
            holder.contentLayout.tag = animationId
            animateHighlight(holder.contentLayout, animationId)
        }
    }

    // Anima a cor de fundo para destacar a fórmula selecionada
    private fun animateHighlight(contentLayout: LinearLayout, expectedId: Long) {
        try {
            if (contentLayout.tag != expectedId) {
                return
            }
            val highlightColor = ContextCompat.getColor(context, R.color.highlight_color) or 0xFF000000.toInt()
            contentLayout.setBackgroundColor(highlightColor)
            contentLayout.postDelayed({
                if (contentLayout.tag == expectedId) {
                    val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), highlightColor, defaultBackgroundColor)
                    colorAnimator.duration = 1500
                    colorAnimator.addUpdateListener { animator ->
                        if (contentLayout.tag == expectedId) {
                            contentLayout.setBackgroundColor(animator.animatedValue as Int)
                        }
                    }
                    colorAnimator.start()
                }
            }, 500)
        } catch (e: Exception) {
            Log.e("FormulasAdapter", "Erro ao animar destaque: ${e.message}", e)
        }
    }

    override fun getItemCount() = formulas.size
}