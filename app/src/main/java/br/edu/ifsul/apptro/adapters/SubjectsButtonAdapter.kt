package br.edu.ifsul.apptro.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsul.apptro.R
import br.edu.ifsul.apptro.models.Subjects
import java.util.Locale

class SubjectsButtonAdapter(
    private var disciplinas: List<Subjects>,
    private val onDisciplinaClick: (Subjects) -> Unit
) : RecyclerView.Adapter<SubjectsButtonAdapter.DisciplinaViewHolder>() {

    class DisciplinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tv_disciplina_nome)
        val tvSemestre: TextView = itemView.findViewById(R.id.tv_disciplina_semestre)
        val tvDescricao: TextView = itemView.findViewById(R.id.tv_disciplina_descricao)
        val tvCurso: TextView = itemView.findViewById(R.id.tv_disciplina_curso)
        val tvFormulas: TextView = itemView.findViewById(R.id.tv_disciplina_formulas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return DisciplinaViewHolder(view)
    }

    @SuppressLint("SetTextI18n") // Permite textos diretos sem necessidade de strings.xml
    override fun onBindViewHolder(holder: DisciplinaViewHolder, position: Int) {
        val disciplina = disciplinas[position]

        // Define os textos básicos
        holder.tvNome.text = disciplina.name
        holder.tvSemestre.text = "${disciplina.semmester}° semestre"
        holder.tvDescricao.text = disciplina.description

        // Formata o nome do curso (Primeira letra maiúscula)
        holder.tvCurso.text = disciplina.course.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Lógica de plural para fórmulas
        val formulasCount = disciplina.formulas?.size ?: 0
        holder.tvFormulas.text = if (formulasCount == 1) {
            "1 fórmula"
        } else {
            "$formulasCount fórmulas"
        }

        // Configura o clique
        holder.itemView.setOnClickListener {
            onDisciplinaClick(disciplina)
        }
    }

    override fun getItemCount(): Int = disciplinas.size

    // Atualiza a lista completa e notifica o RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    fun updateDisciplinas(newDisciplinas: List<Subjects>) {
        disciplinas = newDisciplinas
        notifyDataSetChanged()
    }
}