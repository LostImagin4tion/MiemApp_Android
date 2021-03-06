package ru.hse.miem.miemapp.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_project_in_search.view.*
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.domain.entities.ProjectInSearch
import ru.hse.miem.miemapp.presentation.TextViewUtils.makeNameValueString
import java.util.*

class ProjectsAdapter(
    private val navigateToProject: (Long) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    private var projects: List<ProjectInSearch> = emptyList()
    private var displayedProjects = projects

    val hasData get() = projects.isNotEmpty()

    fun performSearch(condition: String, filters: SearchFilters) {
        fun String.matchesCondition() = toLowerCase(Locale.getDefault()).matches(Regex(".*$condition.*"))

        displayedProjects = projects.filter {
                it.name.matchesCondition() ||
                it.head.matchesCondition() ||
                it.number.toString().matchesCondition() ||
                condition.isEmpty() || condition.isBlank()
            }
            .filter { !filters.isAvailableVacancies || it.vacancies > 0 }
            .filter { filters.projectType == 0 || it.type == filters.projectTypeName } // filter if not default
            .filter { filters.projectState == 0 || it.state == filters.projectStateName }

        notifyDataSetChanged()
    }

    fun update(projects: List<ProjectInSearch>) {
        this.projects = projects
        if (displayedProjects.isEmpty()) {
            displayedProjects = this.projects
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_in_search, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(displayedProjects[position], navigateToProject)
    }

    override fun getItemCount() = displayedProjects.size

    class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(project: ProjectInSearch, navigateToProject: (Long) -> Unit) = itemView.apply {
            projectNumber.text = project.number.toString()
            projectName.text = project.name
            projectState.setBackgroundResource(if (project.isActive) R.drawable.badge_active_bg else R.drawable.badge_inactive_bg)
            projectState.text = project.state
            project.vacancies.takeIf { it > 0 }?.let {
                projectVacancies.visibility = View.VISIBLE
                projectVacancies.text = context.getString(R.string.project_vacancies).format(project.vacancies)
            }
            projectType.text = makeNameValueString(R.string.project_type, project.type)
            projectHead.text = makeNameValueString(R.string.project_head, project.head)

            setOnClickListener { navigateToProject(project.id) }
        }
    }
}