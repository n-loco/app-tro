package br.edu.ifsul.apptro.models

data class File(
    val author: String,
    val created_at: Any,
    val deleted_at: Any,
    val description: String,
    val fileId: String,
    val id: Any,
    val name: String,
    val source: String,
    val type: String,
    val updated_at: Any,
    val url: String
)