package br.edu.ifsul.apptro.utils

import android.content.Context
import android.util.Log
import br.edu.ifsul.apptro.models.FormulaX // Adicione este import
import br.edu.ifsul.apptro.models.Subjects
import com.google.gson.Gson
import java.io.IOException

/**
 * Classe utilitária responsável por ler e converter arquivos JSON de disciplinas
 * localizados na pasta 'assets'.
 */
class DisciplinaJsonReader {

    private val gson = Gson()

    /**
     * Lê um único arquivo JSON da pasta assets.
     * AGORA CORRIGIDO para esperar um ÚNICO OBJETO Subjects.
     *
     * @param context O contexto para acessar os assets.
     * @param fileName O nome do arquivo a ser lido (ex: "analise-de-circuitos-i.json").
     * @return Um objeto [Subjects] se a leitura for bem-sucedida, ou null em caso de erro.
     */
    fun loadDisciplina(context: Context, fileName: String): Subjects? {
        return try {
            Log.d("DisciplinaJsonReader", "Lendo o arquivo como objeto: $fileName")
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

            // --- A CORREÇÃO CRÍTICA ESTÁ AQUI ---
            // Agora, dizemos ao Gson para converter a string JSON diretamente
            // para a classe Subjects, pois esperamos um objeto.
            gson.fromJson(jsonString, Subjects::class.java)

        } catch (e: IOException) {
            Log.e("JsonReader", "Erro de I/O ao ler o arquivo: $fileName", e)
            null
        } catch (e: Exception) {
            Log.e("JsonReader", "Erro de sintaxe ou outro problema no arquivo: $fileName", e)
            null
        }
    }

    /**
     * Carrega todas as disciplinas de uma lista de nomes de arquivos.
     * (Esta função não precisa de mudanças, pois já usa o método corrigido acima).
     */
    fun loadAllDisciplinas(context: Context, fileNames: List<String>): List<Subjects> {
        val allDisciplinas = mutableListOf<Subjects>()
        fileNames.forEach { fileName ->
            loadDisciplina(context, fileName)?.let { disciplina ->
                allDisciplinas.add(disciplina)
            }
        }
        return allDisciplinas
    }

    /**
     * NOVO: Função que a FormulasActivity precisa.
     * Carrega uma disciplina a partir do seu slug e retorna apenas a sua lista de fórmulas.
     *
     * @param context O contexto da aplicação.
     * @param disciplinaSlug O slug da disciplina (ex: "analise-de-circuitos-i").
     * @return Uma [List] de [FormulaX]. Retorna uma lista vazia se a disciplina ou as fórmulas não forem encontradas.
     */
    fun getFormulas(context: Context, disciplinaSlug: String): List<FormulaX> {
        // Constrói o nome do arquivo a partir do slug
        val fileName = "$disciplinaSlug.json"

        // Usa a função já existente para carregar a disciplina inteira
        val disciplina = loadDisciplina(context, fileName)

        // Retorna a lista de fórmulas da disciplina. Se 'disciplina' ou 'disciplina.formulas' for nulo,
        // o operador elvis (?:) garante que uma lista vazia seja retornada.
        return disciplina?.formulas ?: emptyList()
    }
}