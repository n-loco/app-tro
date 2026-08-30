package br.edu.ifsul.apptro.models

import com.google.gson.annotations.SerializedName

typealias Variables = Map<String, String>
typealias Constants = Map<String, String>

data class FormulaX(

    @SerializedName("constants") val constants: Constants?,
    @SerializedName("description") val description: String,
    @SerializedName("latex") val latex: List<String>,
    @SerializedName("name") val name: String,
    @SerializedName("variables") val variables: Variables?,

    // Parâmetro de expandido ou não da fórmula
    @Transient var isExpanded: Boolean = false,

    /** Implementação do favorito. */
    // Indica se a fórmula é um favorito ou não
    @Transient var isFavorite: Boolean = false,
    // Necessário para o clique no carrossel saber qual disciplina abrir.
    @Transient var disciplinaOrigem: String? = null,
    // Necessário para o clique no carrossel saber qual arquivo carregar.
    @Transient var arquivoJsonOrigem: String? = null,
    // Índice da fórmula no array (necessário para diferenciar fórmulas duplicadas no mesmo arquivo)
    @Transient var indiceNoArray: Int = -1
) {
    /**
     * Gera um ID único para a fórmula baseado no arquivo JSON, índice e nome.
     * Formato: "arquivo-json::indice::nome-da-formula"
     *
     * Exemplo: "analise-de-circuitos-i.json::0::Lei de Ohm"
     *
     * O índice garante que mesmo fórmulas com nomes idênticos no mesmo arquivo
     * tenham IDs únicos diferentes.
     */
    fun getUniqueId(): String {
        return if (arquivoJsonOrigem != null && indiceNoArray >= 0) {
            "$arquivoJsonOrigem::$indiceNoArray::$name"
        } else if (arquivoJsonOrigem != null) {
            // Fallback se o índice não foi definido (compatibilidade)
            "$arquivoJsonOrigem::$name"
        } else {
            // Fallback caso arquivoJsonOrigem não esteja definido
            name
        }
    }
}