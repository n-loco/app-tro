package br.edu.ifsul.apptro.models

data class SearchableItem(
    val title: String,
    val description: String,
    val searchText: String,
    val sourceFile: String
)