package ru.hse.miem.miemapp.presentation.profile.git_stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import ru.hse.miem.miemapp.R

class GitStatsAdapter: RecyclerView.Adapter<GitStatsAdapter.GitStatsViewHolder>() {

    private var languagesList: MutableList<String> = mutableListOf()
    private var displayedLanguages = languagesList

    fun update(items: List<String>) {
        this.languagesList = if (items.isEmpty()) {
            mutableListOf()
        } else {
            items as MutableList<String>
        }
        displayedLanguages = this.languagesList

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return GitStatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GitStatsViewHolder, position: Int) {
        holder.bind(displayedLanguages[position])
    }

    override fun getItemCount() = languagesList.size

    inner class GitStatsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(language: String) = itemView.apply {
            languageCard.text = language.split(" ")[0] // cause api returns language and percent of using
        }
    }
}