package br.edu.ifsul.apptro.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.FormulasActivity
import br.edu.ifsul.apptro.R
import br.edu.ifsul.apptro.models.FormulaX

/**
 * Adapter para o carrossel horizontal de favoritos e recentes na tela inicial.
 */
class FavoritesCarouselAdapter(
    private val context: Context,
    private val favoriteFormulas: List<FormulaX>
) : RecyclerView.Adapter<FavoritesCarouselAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_favorite_formula_name)
        private val subjectNameTextView: TextView = itemView.findViewById(R.id.tv_favorite_subject_name)

        fun bind(formula: FormulaX) {
            nameTextView.text = formula.name
            subjectNameTextView.text = formula.disciplinaOrigem

            Log.d("FavCarouselAdapter", "Binding: '${formula.name}' em '${formula.disciplinaOrigem}'")

            itemView.setOnClickListener {
                // Garante que temos os dados necessários para navegação
                if (formula.disciplinaOrigem.isNullOrBlank() || formula.arquivoJsonOrigem.isNullOrBlank()) {
                    Log.e("FavoritesClick", "Dados incompletos para navegar para '${formula.name}'.")
                    Toast.makeText(context, "Erro ao abrir favorito.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Log.d("FavoritesClick", "Navegando para '${formula.name}' em '${formula.disciplinaOrigem}'.")

                val intent = Intent(context, FormulasActivity::class.java).apply {
                    putExtra("disciplina_arquivo_json", formula.arquivoJsonOrigem)
                    putExtra("disciplina_nome", formula.disciplinaOrigem)
                    putExtra("formula_nome_foco", formula.name)
                    putExtra("formula_indice_foco", formula.indiceNoArray)
                }
                context.startActivity(intent)
            }
        }
    }

    // Cria o layout do item (card)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_card, parent, false)
        return FavoriteViewHolder(view)
    }

    // Vincula os dados ao layout
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteFormulas[position])
    }

    override fun getItemCount() = favoriteFormulas.size
}