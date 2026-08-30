package br.edu.ifsul.apptro.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.R
import br.edu.ifsul.apptro.models.SearchableItem

class SearchAdapter(
    private var itemList: List<SearchableItem>,
    private val onItemClicked: (SearchableItem) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    // Mantém as referências das views do layout
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val description: TextView = view.findViewById(R.id.item_description)
    }

    // Cria a visualização do item a partir do XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    // Vincula os dados (título/descrição) aos componentes visuais
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.description.text = item.description

        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    override fun getItemCount() = itemList.size

    // Atualiza a lista de resultados da busca
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<SearchableItem>) {
        itemList = newList
        notifyDataSetChanged()
    }
}