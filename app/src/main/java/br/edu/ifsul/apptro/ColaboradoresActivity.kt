package br.edu.ifsul.apptro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.models.Desenvolvedor
import br.edu.ifsul.apptro.models.TipoDesenvolvedor
import br.edu.ifsul.apptro.adapters.DesenvolvedorAdapter
import br.edu.ifsul.apptro.adapters.DevActionsListener

/**
 * Activity responsável por exibir a lista de colaboradores (Técnicos, Professores externos, etc).
 * Implementa DevActionsListener para tratar cliques nos ícones de contato.
 */
class ColaboradoresActivity : AppCompatActivity(), DevActionsListener {

    private lateinit var recyclerViewColaboradores: RecyclerView
    private lateinit var colaboradorAdapter: DesenvolvedorAdapter
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_colaboradores)

        // Configura o botão para fechar a tela (voltar)
        btnVoltar = findViewById(R.id.btn_voltar_colaboradores)
        btnVoltar.setOnClickListener {
            finish()
        }

        // Configura a lista (RecyclerView)
        recyclerViewColaboradores = findViewById(R.id.recycler_view_colaboradores)
        recyclerViewColaboradores.layoutManager = LinearLayoutManager(this)

        // Carrega a lista estática de dados
        val listaColaboradores = carregarColaboradores()

        // Inicializa o adapter passando esta Activity como listener dos cliques
        colaboradorAdapter = DesenvolvedorAdapter(listaColaboradores, this)
        recyclerViewColaboradores.adapter = colaboradorAdapter
    }

    // Retorna a lista manual de colaboradores
    private fun carregarColaboradores(): List<Desenvolvedor> {
        return listOf(
            Desenvolvedor(
                id = "c8",
                nome = "Murilo Chagas Martin",
                fotoUrl = "ic_c8",
                funcao = "Técnico em Eletrônica",
                // Definido um valor padrão aqui (não é usado na aba Colaboradores, mas é essencial em DevsTab)
                // Isso resolve o erro na ColaboradoresActivity sem quebrar o DevsTab.
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "martinmurilo29@gmail.com",
                githubUrl = "https://github.com/murcilonx",
                linkedinUrl = "https://www.linkedin.com/in/murilo-c-a58128342/",
                instagramUrl = "https://www.instagram.com/murcilonx"
            ),
            Desenvolvedor(
                id = "c10",
                nome = "Ezequiel Soares da Silva",
                fotoUrl = "ic_c10",
                funcao = "Técnico em Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = null,
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = "https://www.instagram.com/ezequiel__sds/"
            ),
            Desenvolvedor(
                id = "c1",
                nome = "Mateus Mendes Gonçalves",
                fotoUrl = "ic_c1",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "mateusgoncalves@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = "https://www.linkedin.com/in/mateusmgoncalves/",
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c2",
                nome = "Igor da Rocha Barros",
                fotoUrl = "ic_c2",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "igorbarros@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = "https://www.instagram.com/professor_xiru/"
            ),
            Desenvolvedor(
                id = "c5",
                nome = "Isis Duarte Bender",
                fotoUrl = "ic_c5",
                funcao = "Professora do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "isisbender@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c6",
                nome = "Sandro Vilela da Silva",
                fotoUrl = "ic_c6",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "sandrosilva@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c3",
                nome = "Gustavo Buchweitz Giusti",
                fotoUrl = "ic_c3",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "gustavogiusti@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c4",
                nome = "Guilherme Schwanke Cardoso",
                fotoUrl = "ic_c4",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "guilhermecardoso@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c7",
                nome = "Rafael Galli",
                fotoUrl = "ic_c7",
                funcao = "Professor do Curso de Eletrônica",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = "rafaelgalli@ifsul.edu.br",
                githubUrl = null,
                linkedinUrl = "https://www.linkedin.com/in/rafael-galli-964471318",
                instagramUrl = null
            ),
            Desenvolvedor(
                id = "c9",
                nome = "Eric Cezar Domingues",
                fotoUrl = "ic_c9",
                funcao = "Cursando Design Gráfico",
                tipo = TipoDesenvolvedor.PROFESSOR,
                email = null,
                githubUrl = null,
                linkedinUrl = null,
                instagramUrl = "https://www.instagram.com/saikwo_i/"
            )
        )
    }

    // Tenta abrir o cliente de email do usuário (caso o envio direto falhe)
    private fun fallbackEmailChooser(email: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Contato via App Eletrônica")
        }

        try {
            Toast.makeText(this, "Escolha seu aplicativo de email", Toast.LENGTH_SHORT).show()
            startActivity(Intent.createChooser(intent, "Enviar email usando:"))
        } catch (e: Exception) {
            Log.e("ColaboradoresActivity", "Erro ao abrir chooser de email", e)
            Toast.makeText(this, "Nenhum aplicativo de email encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Implementação dos métodos de clique da interface DevActionsListener ---

    override fun onEmailClick(email: String) {
        // Tenta abrir direto para envio
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$email?subject=".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("ColaboradoresActivity", "Erro envio direto: $e")
            fallbackEmailChooser(email)
        }
    }

    override fun onGithubClick(githubUrl: String) {
        // Abre o link no navegador
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