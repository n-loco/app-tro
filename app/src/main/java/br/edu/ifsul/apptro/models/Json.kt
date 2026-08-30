package br.edu.ifsul.apptro.models

data class Json(
    val date: String,
    val email: Any,
    val github: String,
    val name: String,
    val roles: List<String>
)