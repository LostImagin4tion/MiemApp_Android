package ru.hse.miem.miemapp.presentation.profile

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.hse.miem.miemapp.domain.entities.Profile
import ru.hse.miem.miemapp.domain.entities.ProjectBasic
import ru.hse.miem.miemapp.presentation.base.BaseView

interface ProfileView : BaseView {
    @AddToEndSingle fun setupProfile(profile: Profile)
    @AddToEndSingle fun setupProjects(projects: List<ProjectBasic>)
}