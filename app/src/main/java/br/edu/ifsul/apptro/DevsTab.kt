package br.edu.ifsul.apptro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.models.Desenvolvedor
import br.edu.ifsul.apptro.models.TipoDesenvolvedor
import br.edu.ifsul.apptro.adapters.DesenvolvedorAdapter
import br.edu.ifsul.apptro.adapters.DevActionsListener

class DevsTab : Fragment(), DevActionsListener {

    private lateinit var recyclerViewDesenvolvedores: RecyclerView
    private lateinit var devAdapter: DesenvolvedorAdapter

    private var listaCompletaDevs: List<Desenvolvedor> = listOf()
    private var listaExibidaDevs: MutableList<Desenvolvedor> = mutableListOf()

    // Views de controle
    private lateinit var btnFiltroDropdown: Button
    private lateinit var btnColaboradores: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tab_devs, container, false)

        // Inicializar RecyclerView e layout manager
        recyclerViewDesenvolvedores = view.findViewById(R.id.recycler_view_desenvolvedores)
        recyclerViewDesenvolvedores.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar Adapter
        devAdapter = DesenvolvedorAdapter(listaExibidaDevs, this)
        recyclerViewDesenvolvedores.adapter = devAdapter

        // Configurar botões de ação
        btnFiltroDropdown = view.findViewById(R.id.btn_filtro_dropdown)
        btnColaboradores = view.findViewById(R.id.btn_colaboradores)

        // Ações de clique
        btnFiltroDropdown.setOnClickListener {
            mostrarMenuFiltro(it)
        }

        btnColaboradores.setOnClickListener {
            abrirColaboradores()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Carrega dados iniciais e exibe a lista completa
        carregarDesenvolvedoresOriginais()
        aplicarFiltro(null)
    }

    // Exibe o menu popup para filtrar a lista por tipo
    @SuppressLint("SetTextI18n") // Permite setar texto direto sem resources
    private fun mostrarMenuFiltro(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_filtro_devs, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_filtro_todos -> {
                    Log.d("DevsTab", "Filtro TODOS selecionado")
                    aplicarFiltro(null)
                    btnFiltroDropdown.text = "Todos"
                    true
                }
                R.id.menu_filtro_alunos -> {
                    Log.d("DevsTab", "Filtro ALUNOS selecionado")
                    aplicarFiltro(TipoDesenvolvedor.ALUNO)
                    btnFiltroDropdown.text = "Alunos"
                    true
                }
                R.id.menu_filtro_professores -> {
                    Log.d("DevsTab", "Filtro PROFESSORES selecionado")
                    aplicarFiltro(TipoDesenvolvedor.PROFESSOR)
                    btnFiltroDropdown.text = "Professores"
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    // Navega para a tela de colaboradores externos
    private fun abrirColaboradores() {
        Log.d("DevsTab", "Abrindo tela de Colaboradores")
        val intent = Intent(requireContext(), ColaboradoresActivity::class.java)
        startActivity(intent)
    }

    // Carrega a lista estática de desenvolvedores do projeto
    private fun carregarDesenvolvedoresOriginais() {
        val devsOriginais = listOf(
            Desenvolvedor("1", "Alexandre Nunes da Silva Filho", "ic_dev1", "Desenvolvedor Full-Stack", TipoDesenvolvedor.ALUNO, "xandyhsilvah@gmail.com", "https://github.com/ale1zin", "https://www.linkedin.com/in/ale1zin/", "https://www.instagram.com/ale1zin/"),
            Desenvolvedor("2", "Carlos Alexandre Dutra Volz", "ic_dev2", "Desenvolvedor Full-Stack", TipoDesenvolvedor.ALUNO, null, "https://github.com/Carlosvolz", null, "https://www.instagram.com/carlos__volz/"),
            Desenvolvedor("3", "Eduardo Peixoto Alves Decker", "ic_dev3", "Desenvolvedor Full-Stack", TipoDesenvolvedor.ALUNO, null, "https://github.com/eduardodecker2006", null, "https://www.instagram.com/eduardopeixotoalves/"),
            Desenvolvedor("4", "Yuri Andrade dos Anjos", "ic_dev4", "Desenvolvedor Full-Stack", TipoDesenvolvedor.ALUNO, "yurixbroficial@gmail.com", "https://github.com/YuriXbr", "https://www.linkedin.com/in/yuri-andrade-dos-anjos-08a98027a/", null),
            Desenvolvedor("5", "Fabricio Neitzke Ferreira", "ic_devt1", "Professor Orientador", TipoDesenvolvedor.PROFESSOR, "fabricioferreira@ifsul.edu.br", null, null, null),
            Desenvolvedor("6", "Rodrigo Nuevo Lellis", "ic_devt2", "Professor Orientador", TipoDesenvolvedor.PROFESSOR, "rodrigolellis@ifsul.edu.br", null, null, "https://www.instagram.com/rodrigonuevolellis/")
        )
        listaCompletaDevs = devsOriginais
        Log.d("DevsTab", "Devs carregados: ${listaCompletaDevs.size}")
    }

    // Filtra a lista e atualiza o RecyclerView
    @SuppressLint("NotifyDataSetChanged") // Atualização total da lista é intencional aqui
    private fun aplicarFiltro(tipo: TipoDesenvolvedor?) {
        val listaFiltrada: List<Desenvolvedor> = if (tipo == null) {
            listaCompletaDevs
        } else {
            listaCompletaDevs.filter { it.tipo == tipo }
        }

        listaExibidaDevs.clear()
        listaExibidaDevs.addAll(listaFiltrada)

        devAdapter.notifyDataSetChanged()

        Log.d("DevsTab", "Filtro aplicado. Exibindo ${listaExibidaDevs.size} itens.")
    }

    // Tenta abrir app de email caso o intent direto falhe
    private fun fallbackEmailChooser(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Assunto do email")
        }

        try {
            Toast.makeText(requireContext(), "Escolha seu aplicativo de email", Toast.LENGTH_SHORT).show()
            val chooser = Intent.createChooser(intent, "Enviar email usando:")
            startActivity(chooser)
        } catch (e: Exception) {
            Log.e("DevsTab", "Erro ao abrir chooser de email", e)
            Toast.makeText(requireContext(), "Nenhum aplicativo de email encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Implementação dos clicks do Adapter (DevActionsListener) ---

    override fun onEmailClick(email: String) {
        // Warning resolvido: Uso da extensão KTX toUri()
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$email?subject=".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            startActivity(intent)
        } catch (_: Exception) {
            fallbackEmailChooser(email)
        }
    }

    override fun onGithubClick(githubUrl: String) {
        // Warning resolvido: Uso da extensão KTX toUri()
        val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri())
        startActivity(intent)
    }

    override fun onLinkedinClick(linkedinUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, linkedinUrl.toUri())
        startActivity(intent)
    }

    override fun onInstagramClick(instagramUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, instagramUrl.toUri())
        startActivity(intent)
    }
}