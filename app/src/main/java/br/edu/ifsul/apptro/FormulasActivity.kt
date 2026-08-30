package br.edu.ifsul.apptro

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.adapters.FormulasAdapter
import br.edu.ifsul.apptro.models.FormulaX
import br.edu.ifsul.apptro.utils.DisciplinaJsonReader
import br.edu.ifsul.apptro.utils.RecentFormulasManager

class FormulasActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTituloFormulas: TextView
    private lateinit var tvSubtituloFormulas: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var formulasAdapter: FormulasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulas)

        // Inicializar componentes da tela
        btnBack = findViewById(R.id.btn_back)
        tvTituloFormulas = findViewById(R.id.tv_titulo_formulas)
        tvSubtituloFormulas = findViewById(R.id.tv_subtitulo_formulas)
        recyclerView = findViewById(R.id.rv_formulas)

        // Recupera dados enviados pela tela anterior (Home ou Disciplinas)
        val nomeDisciplina = intent.getStringExtra("disciplina_nome") ?: "Fórmulas"
        val nomeArquivoJson = intent.getStringExtra("disciplina_arquivo_json")
        val formulaFocoNome = intent.getStringExtra("formula_nome_foco")

        // Define o título visual
        tvTituloFormulas.text = nomeDisciplina

        // Configura ação de voltar usando o Dispatcher (padrão moderno)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicia o processo de leitura do arquivo JSON
        if (nomeArquivoJson != null) {
            carregarFormulasDoArquivo(nomeArquivoJson, nomeDisciplina, formulaFocoNome)
        } else {
            Toast.makeText(this, "Erro: Arquivo da disciplina não encontrado.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun carregarFormulasDoArquivo(
        fileName: String,
        nomeDisciplina: String,
        formulaFocoNome: String?
    ) {
        // Instancia o leitor e busca o objeto Subject (Disciplina)
        val disciplinaJsonReader = DisciplinaJsonReader()
        val disciplina = disciplinaJsonReader.loadDisciplina(this, fileName)
        val formulas = disciplina?.formulas ?: emptyList()

        // Preenche metadados necessários para o funcionamento dos favoritos/recentes
        formulas.forEachIndexed { index, formula ->
            formula.disciplinaOrigem = nomeDisciplina
            formula.arquivoJsonOrigem = fileName
            formula.indiceNoArray = index
        }

        // Atualiza o subtítulo com a contagem
        val numFormulas = formulas.size
        tvSubtituloFormulas.text =
            if (numFormulas == 1) "1 fórmula disponível" else "$numFormulas fórmulas disponíveis"

        // Verifica se precisamos focar em uma fórmula específica (vinda da busca)
        val indexParaFocar: Int = if (formulaFocoNome != null) {
            formulas.indexOfFirst { it.name.equals(formulaFocoNome, ignoreCase = true) }
        } else {
            -1
        }

        Log.d("FormulasActivity", "Buscando foco para: '$formulaFocoNome', Index: $indexParaFocar")

        if (indexParaFocar != -1) {
            val focusedFormula = formulas[indexParaFocar]
            focusedFormula.isExpanded = true // Abre o card automaticamente

            // Registra nos recentes já que o usuário entrou direto nela
            registerRecentFormula(focusedFormula)
        }

        // Configura o adapter e define o clique para salvar no histórico
        formulasAdapter = FormulasAdapter(this, formulas, indexParaFocar) { clickedFormula ->
            Log.d("FormulasActivity", "Fórmula clicada: ${clickedFormula.name}")
            registerRecentFormula(clickedFormula)
        }
        recyclerView.adapter = formulasAdapter

        // Lógica de rolagem automática para centralizar o item focado
        if (indexParaFocar != -1) {
            recyclerView.post {
                // Primeiro scroll simples
                (recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPosition(indexParaFocar)

                // Segundo ajuste fino para centralizar o item na tela
                recyclerView.post {
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return@post
                    val viewDoItem = layoutManager.findViewByPosition(indexParaFocar)

                    if (viewDoItem != null) {
                        val alturaTela = recyclerView.height
                        val alturaItem = viewDoItem.height
                        val posicaoAtualDoItem = viewDoItem.top
                        val offsetDesejado = (alturaTela / 2) - (alturaItem / 2)
                        val distanciaParaRolar = posicaoAtualDoItem - offsetDesejado

                        recyclerView.smoothScrollBy(0, distanciaParaRolar, null, 500)
                    }
                }
            }
        } else if (formulaFocoNome != null) {
            Log.w("FormulasActivity", "Fórmula solicitada não encontrada na lista.")
        }
    }

    // Salva a fórmula na lista de "Recentes" do SharedPreferences
    private fun registerRecentFormula(formula: FormulaX) {
        val formulaId = formula.getUniqueId()
        RecentFormulasManager.addFormula(this, formulaId)
    }
}