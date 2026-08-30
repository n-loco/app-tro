package br.edu.ifsul.apptro.models

data class Desenvolvedor(
    val id: String, // Um ID único
    val nome: String,
    val fotoUrl: String?, // URL da imagem ou um identificador de drawable local
    val funcao: String,
    val tipo: TipoDesenvolvedor, // Para filtrar entre Aluno e Professor
    val email: String?,
    val githubUrl: String?,
    val linkedinUrl: String?,
    val instagramUrl: String?,
    // Adicione mais campos se necessário (Site, etc.)
)

enum class TipoDesenvolvedor {
    ALUNO, PROFESSOR
}