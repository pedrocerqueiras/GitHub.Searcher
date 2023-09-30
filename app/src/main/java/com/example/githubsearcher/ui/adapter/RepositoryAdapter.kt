package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(
    private val repositories: List<Repository>,
    private val repositoryItemClickListener: (Repository) -> Unit,
    var btnShareItemClickListener: (Repository) -> Unit,
) : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega a quantidade de repositórios da lista
    override fun getItemCount(): Int = repositories.size

    // Pega o conteúdo da view e troca pela informação de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 8 - Realizar o bind do viewHolder
        //  Bind
        holder.nameTextView.text = repositories[position].name

        //  click no item
        holder.itemView.setOnClickListener {
            val repository = repositories[position]
            repositoryItemClickListener(repository)
        }

        //  click no btn Share
        holder.btnShare.setOnClickListener {
            val repository = repositories[position]
            btnShareItemClickListener(repository)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Elementos do layout repository_item.xml
        val nameTextView: TextView = view.findViewById(R.id.tv_nomerepositorio)
        val btnShare: ImageView = view.findViewById(R.id.iv_share)


    }
}