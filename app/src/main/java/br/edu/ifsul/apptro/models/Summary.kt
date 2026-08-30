package br.edu.ifsul.apptro.models

data class Summary(
    val date: String,
    val email: Any,
    val github: String,
    val name: String,
    val roles: List<String>
)