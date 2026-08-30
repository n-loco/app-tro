package br.edu.ifsul.apptro.models

import com.google.gson.annotations.SerializedName


data class Subjects(
    val alias: List<String>?,
    val articleWriters: List<ArticleWriter>,
    val chapters: List<String>,
    val content: String,
    val course: String,
    val created_at: Any?,
    val deleted_at: Any?,
    val description: String,
    val embededVideos: List<EmbededVideo>,
    val fileVersion: Int,
    val files: List<File>,
    val formulas: List<FormulaX>?,
    val id: Int,
    val images: List<Image>,
    @SerializedName("name_disc")
    val name: String,
    val needs: List<Any?>,
    val obs: String,
    val parents: List<String>,
    val period: String,
    val references: List<Reference>,
    val semmester: Int,
    val slug: String,
    val status: String,
    val supervisors: List<Supervisor>,
    val tags: List<String>?,
    val updated_at: Any?
)

