package br.edu.ifsul.apptro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.adapters.SubjectsButtonAdapter
import br.edu.ifsul.apptro.models.Subjects
import br.edu.ifsul.apptro.utils.DisciplinaJsonReader
import com.google.android.material.button.MaterialButtonToggleGroup

class SubjectsTab : Fragment() {

    // Views do layout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var progressLoading: ProgressBar
    private lateinit var toggleGroupSort: MaterialButtonToggleGroup

    // Componentes para gerenciar disciplinas
    private lateinit var disciplinaReader: DisciplinaJsonReader
    private lateinit var adapter: SubjectsButtonAdapter
    private var disciplinas: List<Subjects> = emptyList()

    // Enum para controlar o modo de ordenação
    private enum class SortMode {
        BY_SEMESTER,
        BY_DISCIPLINE
    }

    // Variável para guardar o estado atual da ordenação
    private var currentSortMode = SortMode.BY_SEMESTER // Padrão

    // Constantes para SharedPreferences
    companion object {
        private const val PREFS_NAME = "SubjectsTabPreferences"
        private const val KEY_SORT_MODE = "sort_mode"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tab_subjects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar componentes
        initializeViews(view)
        setupRecyclerView()
        setupDisciplinaReader()

        // Ordem de inicialização para carregar preferências
        loadSortPreference()
        applySortPreferenceToUI()
        setupSortToggle()

        // Carrega as disciplinas
        loadDisciplinas()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.rv_disciplinas)
        emptyStateLayout = view.findViewById(R.id.layout_empty_state)
        progressLoading = view.findViewById(R.id.progress_loading)
        toggleGroupSort = view.findViewById(R.id.toggle_group_sort)
    }

    private fun setupRecyclerView() {
        // Configurar adapter com callback para cliques nos botões
        adapter = SubjectsButtonAdapter(emptyList()) { disciplina ->
            onDisciplinaButtonClick(disciplina)
        }

        // Configurar RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupDisciplinaReader() {
        disciplinaReader = DisciplinaJsonReader()
    }

    // Carrega a preferência do SharedPreferences
    private fun loadSortPreference() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedModeName = prefs.getString(KEY_SORT_MODE, SortMode.BY_SEMESTER.name)

        currentSortMode = try {
            SortMode.valueOf(savedModeName ?: SortMode.BY_SEMESTER.name)
        } catch (_: IllegalArgumentException) { // Warning resolvido: parâmetro não usado
            SortMode.BY_SEMESTER
        }

        Log.d("SUBJECTS_TAB", "Preferência de ordenação carregada: $currentSortMode")
    }

    // Atualiza a UI (botões)
    private fun applySortPreferenceToUI() {
        val checkedId = when (currentSortMode) {
            SortMode.BY_DISCIPLINE -> R.id.btn_sort_disciplina
            SortMode.BY_SEMESTER -> R.id.btn_sort_semestre
        }
        toggleGroupSort.check(checkedId)
    }

    // Salva e aplica a preferência
    private fun setupSortToggle() {
        toggleGroupSort.addOnButtonCheckedListener { _, checkedId, isChecked -> // Warning resolvido: 'group' não usado
            if (!isChecked) return@addOnButtonCheckedListener

            currentSortMode = when (checkedId) {
                R.id.btn_sort_disciplina -> SortMode.BY_DISCIPLINE
                R.id.btn_sort_semestre -> SortMode.BY_SEMESTER
                else -> SortMode.BY_SEMESTER
            }

            Log.d("SUBJECTS_TAB", "Modo de ordenação alterado para: $currentSortMode")

            saveSortPreference()
            applySortAndUpdateList()
        }
    }

    // Salva a preferência no SharedPreferences
    private fun saveSortPreference() {
        Log.d("SUBJECTS_TAB", "Salvando preferência: ${currentSortMode.name}")
        // Warning resolvido: uso da extensão KTX 'edit'
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_SORT_MODE, currentSortMode.name)
        }
    }

    private fun loadDisciplinas() {
        Log.d("SUBJECTS_TAB", "Iniciando carregamento de disciplinas...")

        showLoading(true)

        try {
            val jsonFiles = listOf(
                "analise-de-circuitos-i.json",
                "analise-de-circuitos-ii.json",
                "analise-de-circuitos-iii.json",
                "analise-de-circuitos-iv.json",
                "eletricidade-i.json",
                "eletricidade-ii.json",
                "eletricidade-iii.json",
                "eletronica-digital-i.json",
                "eletronica-digital-ii.json",
                "eletronica-digital-iv.json",
                "eletronica-digital-v.json",
                "eletronica-geral-i.json",
                "eletronica-geral-ii.json",
                "eletronica-geral-iii.json",
                "eletronica-geral-v.json",
                "instrumentacao-industrial.json",
                "sistemas-de-controle.json",
                "sistemas-microprocessados-iii.json",
                "sistemas-microprocessados-iv.json",
                "sistemas-de-video.json",
                "eletronica-geral-iv.json",
                "eletronica-de-potencia-i.json",
                "eletronica-de-potencia-ii.json"
            )

            Log.d("SUBJECTS_TAB", "Tentando carregar ${jsonFiles.size} arquivo(s)...")

            disciplinas = disciplinaReader.loadAllDisciplinas(requireContext(), jsonFiles)

            Log.d("SUBJECTS_TAB", "Carregadas ${disciplinas.size} disciplina(s) com sucesso!")

            showLoading(false)

            if (disciplinas.isNotEmpty()) {
                applySortAndUpdateList()
            } else {
                showEmptyState()
                Log.w("SUBJECTS_TAB", "Nenhuma disciplina foi carregada")
            }

        } catch (e: Exception) {
            Log.e("SUBJECTS_TAB", "Erro ao carregar disciplinas: ${e.message}", e)
            showLoading(false)
            showError("Erro ao carregar disciplinas: ${e.message}")
        }
    }

    private fun applySortAndUpdateList() {
        if (disciplinas.isEmpty()) {
            Log.d("SUBJECTS_TAB", "Nenhuma disciplina para ordenar.")
            // Warning resolvido: uso de isGone
            if (progressLoading.isGone) {
                showEmptyState()
            }
            return
        }

        val sortedList = when (currentSortMode) {
            SortMode.BY_SEMESTER -> {
                Log.d("SUBJECTS_TAB", "Ordenando por SEMESTRE...")
                disciplinas.sortedWith(compareBy<Subjects> { it.semmester }.thenBy { it.name })
            }
            SortMode.BY_DISCIPLINE -> {
                Log.d("SUBJECTS_TAB", "Ordenando por DISCIPLINA (nome)...")
                disciplinas.sortedBy { it.name }
            }
        }

        Log.d("SUBJECTS_TAB", "Atualizando adapter com ${sortedList.size} disciplinas ordenadas.")

        adapter.updateDisciplinas(sortedList)
        showDisciplinas()

        sortedList.forEachIndexed { index, disciplina ->
            Log.d(
                "SUBJECTS_TAB",
                "${index + 1}. [${disciplina.semmester}° sem] ${disciplina.name}"
            )
        }
    }

    private fun onDisciplinaButtonClick(disciplina: Subjects) {
        Log.d("DISCIPLINA_CLICK", "Clicou na disciplina: ${disciplina.name}")

        try {
            val fileName = "${disciplina.slug}.json"

            val intent = Intent(requireContext(), FormulasActivity::class.java).apply {
                putExtra("disciplina_arquivo_json", fileName)
                putExtra("disciplina_nome", disciplina.name)
            }
            startActivity(intent)

        } catch (e: Exception) {
            Log.e("DISCIPLINA_CLICK", "Erro ao abrir FormulasActivity: ${e.message}", e)
            Toast.makeText(
                requireContext(),
                "Erro ao abrir fórmulas: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Warning resolvido: uso de isVisible/isGone
    private fun showLoading(show: Boolean) {
        progressLoading.isVisible = show
        recyclerView.isVisible = !show
        toggleGroupSort.isVisible = !show
        emptyStateLayout.isGone = true
    }

    private fun showDisciplinas() {
        recyclerView.isVisible = true
        toggleGroupSort.isVisible = true
        emptyStateLayout.isGone = true
        progressLoading.isVisible = false
    }

    private fun showEmptyState() {
        recyclerView.isVisible = false
        toggleGroupSort.isVisible = true
        emptyStateLayout.isVisible = true
        progressLoading.isVisible = false
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        showEmptyState()
    }

    // Funções não utilizadas (refreshDisciplinas e getDisciplinaBySlug) foram removidas.
}