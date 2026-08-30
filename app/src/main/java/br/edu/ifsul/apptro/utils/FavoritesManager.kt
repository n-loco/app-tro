package br.edu.ifsul.apptro.utils

import android.content.Context
import android.content.SharedPreferences
import br.edu.ifsul.apptro.models.FormulaX

object FavoritesManager {

    private const val PREFS_NAME = "app_favorites_prefs"
    private const val KEY_FAVORITE_FORMULAS = "favorite_formulas"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Alterna o estado de favorito de uma fórmula usando seu ID único.
     */
    fun toggleFormulaFavorite(context: Context, formula: FormulaX) {
        val uniqueId = formula.getUniqueId()
        val prefs = getPreferences(context)
        val favorites = getFormulaFavorites(context).toMutableSet()

        if (favorites.contains(uniqueId)) {
            favorites.remove(uniqueId)
        } else {
            favorites.add(uniqueId)
        }

        prefs.edit().putStringSet(KEY_FAVORITE_FORMULAS, favorites).apply()
    }

    /**
     * Verifica se uma fórmula está favoritada usando seu ID único.
     */
    fun isFormulaFavorite(context: Context, formula: FormulaX): Boolean {
        val uniqueId = formula.getUniqueId()
        return getFormulaFavorites(context).contains(uniqueId)
    }

    /**
     * Retorna o conjunto de IDs únicos de fórmulas favoritadas.
     */
    fun getFormulaFavorites(context: Context): Set<String> {
        val prefs = getPreferences(context)
        return prefs.getStringSet(KEY_FAVORITE_FORMULAS, emptySet()) ?: emptySet()
    }
}