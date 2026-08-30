package br.edu.ifsul.apptro.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import br.edu.ifsul.apptro.R
import br.edu.ifsul.apptro.models.Desenvolvedor

// Interface para tratar os cliques nos botões de contato
interface DevActionsListener {
    fun onEmailClick(email: String)
    fun onGithubClick(githubUrl: String)
    fun onLinkedinClick(linkedinUrl: String)
    fun onInstagramClick(instagramUrl: String)
}

class DesenvolvedorAdapter(
    private var desenvolvedores: List<Desenvolvedor>,
    private val listener: DevActionsListener? = null
) : RecyclerView.Adapter<DesenvolvedorAdapter.DesenvolvedorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesenvolvedorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_devs, parent, false)
        return DesenvolvedorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesenvolvedorViewHolder, position: Int) {
        val desenvolvedor = desenvolvedores[position]
        Log.d("DevAdapter", "Exibindo: ${desenvolvedor.nome} (${desenvolvedor.tipo})")
        holder.bind(desenvolvedor, listener)
    }

    override fun getItemCount(): Int = desenvolvedores.size

    class DesenvolvedorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgFoto: ImageView = itemView.findViewById(R.id.img_desenvolvedor_foto)
        private val tvNome: TextView = itemView.findViewById(R.id.tv_desenvolvedor_nome)
        private val tvFuncao: TextView = itemView.findViewById(R.id.tv_desenvolvedor_funcao)
        private val btnEmail: ImageButton = itemView.findViewById(R.id.btn_desenvolvedor_email)
        private val btnGithub: ImageButton = itemView.findViewById(R.id.btn_desenvolvedor_github)
        private val btnLinkedin: ImageButton = itemView.findViewById(R.id.btn_desenvolvedor_linkedin)
        private val btnInstagram: ImageButton = itemView.findViewById(R.id.btn_desenvolvedor_instagram)

        @SuppressLint("DiscouragedApi") // Permite buscar recursos pelo nome (string)
        fun bind(desenvolvedor: Desenvolvedor, listener: DevActionsListener?) {
            tvNome.text = desenvolvedor.nome
            tvFuncao.text = desenvolvedor.funcao

            // Configuração da Imagem (Glide ou Resource Local)
            val fotoUrl = desenvolvedor.fotoUrl
            val placeholderDrawable = R.drawable.ic_devdefault

            Glide.with(itemView.context).clear(imgFoto)

            if (fotoUrl != null && fotoUrl.isNotBlank()) {
                val requestOptions = RequestOptions()
                    .placeholder(placeholderDrawable)
                    .error(placeholderDrawable)
                    .dontAnimate()

                if (fotoUrl.startsWith("http")) {
                    // Carrega via URL
                    Glide.with(itemView.context)
                        .load(fotoUrl)
                        .apply(requestOptions)
                        .into(imgFoto)
                } else {
                    // Carrega drawable local pelo nome
                    val imageResId = itemView.context.resources.getIdentifier(
                        fotoUrl,
                        "drawable",
                        itemView.context.packageName
                    )

                    if (imageResId != 0) {
                        imgFoto.setImageResource(imageResId)
                    } else {
                        Log.w("DevAdapter", "Imagem não encontrada: $fotoUrl")
                        imgFoto.setImageResource(placeholderDrawable)
                    }
                }
            } else {
                imgFoto.setImageResource(placeholderDrawable)
            }

            // Configuração dos Botões de Ação
            setupButton(btnEmail, desenvolvedor.email) {
                listener?.onEmailClick(it)
            }

            setupButton(btnGithub, desenvolvedor.githubUrl) {
                listener?.onGithubClick(it)
            }

            setupButton(btnLinkedin, desenvolvedor.linkedinUrl) {
                listener?.onLinkedinClick(it)
            }

            setupButton(btnInstagram, desenvolvedor.instagramUrl) {
                listener?.onInstagramClick(it)
            }
        }

        // Função auxiliar para exibir ou esconder botões
        private fun setupButton(button: View, data: String?, onClick: (String) -> Unit) {
            if (!data.isNullOrBlank()) {
                button.visibility = View.VISIBLE
                button.setOnClickListener { onClick(data) }
            } else {
                button.visibility = View.GONE
            }
        }
    }
}