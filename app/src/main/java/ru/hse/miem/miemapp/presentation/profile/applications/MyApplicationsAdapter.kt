package ru.hse.miem.miemapp.presentation.profile.applications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_application.view.*
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.domain.entities.MyProjectsAndApplications
import ru.hse.miem.miemapp.presentation.TextViewUtils.makeNameValueString

class MyApplicationsAdapter(
    private val projects: List<MyProjectsAndApplications.MyApplication>,
    private val navigateToProject: (Long) -> Unit,
    private val withdrawApplication: (Long) -> Unit,
    private val approveApplication: (Long) -> Unit
) : RecyclerView.Adapter<MyApplicationsAdapter.MyApplicationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyApplicationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return MyApplicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyApplicationViewHolder, position: Int) {
        holder.bind(projects[position], navigateToProject, withdrawApplication, approveApplication)
    }

    override fun getItemCount() = projects.size

    class MyApplicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            application: MyProjectsAndApplications.MyApplication,
            navigateToProject: (Long) -> Unit,
            withdrawApplication: (Long) -> Unit,
            approveApplication: (Long) -> Unit
        ) = itemView.apply {
            projectNumber.text = application.projectNumber.toString()

            // because name for my project contains its number
            projectName.text = application.projectName.replace(application.projectNumber.toString(), "").trim()

            when(application.status) {
                MyProjectsAndApplications.MyApplication.Status.WAITING -> applicationState.apply {
                    setText(R.string.application_state_waiting)
                    setBackgroundResource(R.drawable.badge_inactive_bg)
                }
                MyProjectsAndApplications.MyApplication.Status.DECLINED -> applicationState.apply {
                    setText(R.string.application_state_declined)
                    setBackgroundResource(R.drawable.badge_closed_bg)
                }
                MyProjectsAndApplications.MyApplication.Status.APPROVED -> {
                    applicationState.setText(R.string.application_state_approved)
                    applicationState.setBackgroundResource(R.drawable.badge_active_bg)
                    vacancyApprove.visibility = View.VISIBLE
                }
            }

            projectType.text = makeNameValueString(R.string.project_type, application.projectType)
            projectHead.text = makeNameValueString(R.string.project_head, application.head)
            projectRole.text = makeNameValueString(R.string.project_role, application.role)

            application.studentComment?.takeIf { it.isNotBlank() }?.let {
                applicationStudentComment.visibility = View.VISIBLE
                applicationStudentComment.text = makeNameValueString(R.string.application_my_comment, it, sep = "\n")
            }
            application.headComment?.takeIf { it.isNotBlank() }?.let {
                applicationHeadComment.visibility = View.VISIBLE
                applicationHeadComment.text = makeNameValueString(R.string.application_head_comment, it, sep = "\n")
            }

            setOnClickListener { navigateToProject(application.projectId) }
            vacancyWithdraw.setOnClickListener {
                showLoader()
                withdrawApplication(application.id)
            }
            vacancyApprove.setOnClickListener {
                showLoader()
                approveApplication(application.id)
            }
        }

        private fun View.showLoader() {
            applicationButtons.visibility = View.GONE
            applicationConfirmLoader.visibility = View.VISIBLE
        }
    }
}