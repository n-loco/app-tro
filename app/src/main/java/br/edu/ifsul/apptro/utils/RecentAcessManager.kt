
package br.edu.ifsul.apptro.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RecentFormulasManager {
    private const val PREFS_NAME = "app_recent_formulas_prefs"
    private const val KEY_RECENT_FORMULAS = "recent_formulas"
    private const val MAX_RECENTS = 5

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun addFormula(context: Context, formulaUniqueId: String) {
        val recents = getRecentFormulas(context).toMutableList()
        recents.remove(formulaUniqueId)
        recents.add(0, formulaUniqueId)
        val limitedRecents = recents.take(MAX_RECENTS)
        val jsonString = Gson().toJson(limitedRecents)
        getPreferences(context).edit().putString(KEY_RECENT_FORMULAS, jsonString).apply()
    }

    fun getRecentFormulas(context: Context): List<String> {
        val jsonString = getPreferences(context).getString(KEY_RECENT_FORMULAS, null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, object : TypeToken<List<String>>() {}.type)
        } else {
            emptyList()
        }
    }
}
    