package ru.hse.miem.miemapp.presentation.search

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_bottom_filters.*
import ru.hse.miem.miemapp.MiemApplication
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.domain.entities.ProjectInSearch
import ru.hse.miem.miemapp.presentation.OnBackPressListener
import ru.hse.miem.miemapp.presentation.base.BaseFragment
import ru.hse.miem.miemapp.presentation.search.db.SearchDbManager
import java.util.*
import javax.inject.Inject

class SearchFragment : BaseFragment(R.layout.fragment_search), SearchView, OnBackPressListener {

    @Inject
    @InjectPresenter
    lateinit var searchPresenter: SearchPresenter

    @ProvidePresenter
    fun provideSearchPresenter() = searchPresenter

    private lateinit var dbManager: SearchDbManager

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val projectsAdapter = ProjectsAdapter {
        val action = SearchFragmentDirections.actionFragmentSearchToFragmentProject(it)
        findNavController().navigate(action)
    }

    private val filters = SearchFilters()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MiemApplication).appComponent.inject(this)
        dbManager = SearchDbManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        projectsList.adapter = projectsAdapter
        if (projectsAdapter.hasData) {
            searchLoader.visibility = View.GONE
            projectsList.visibility = View.VISIBLE
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    filterResults()
                    true
                }
                else -> false
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(filtersLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    restoreFilters()
                }
                else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    searchLayoutContent.foreground = ColorDrawable(
                        resources.getColor(R.color.transparent)
                    )
                }
            }
        })

        filterButton.setOnClickListener {
            searchLayoutContent.foreground = ColorDrawable(
                resources.getColor(R.color.semi_transparent)
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        hideFiltersButton.setOnClickListener {
            searchLayoutContent.foreground = ColorDrawable(
                resources.getColor(R.color.transparent)
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        showResultsButton.setOnClickListener {
            filterResults()
            searchLayoutContent.foreground = ColorDrawable(
                resources.getColor(R.color.transparent)
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        tinderButton.setOnClickListener {
            searchLayoutContent.foreground = ColorDrawable(
                resources.getColor(R.color.transparent)
            )
            findNavController().navigate(R.id.fragmentTinder)
        }

        searchPresenter.loadFromDb(dbManager)
    }

    override fun loadProjects(projects: List<ProjectInSearch>) {
        if (projects.isNotEmpty()) {
            setupProjects(projects)
        }
        searchPresenter.onCreate()
    }

    private fun saveToDb(projects: List<ProjectInSearch>) {
        dbManager.openDb()
        dbManager.deleteDb()
        dbManager.openDb()

        for (i in projects.indices) {
            dbManager.insertDb(
                id = projects[i].id,
                number = projects[i].number,
                name = projects[i].name,
                type = projects[i].type,
                state = projects[i].state,
                isActive = projects[i].isActive,
                vacancies = projects[i].vacancies,
                head = projects[i].head
            )
        }
        dbManager.closeDb()
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            searchLayoutContent.foreground = ColorDrawable(
                resources.getColor(R.color.transparent)
            )
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return false
    }

    private fun filterResults() {
        filters.projectType = projectTypeSelector.selectedItemPosition
        filters.projectTypeName = (projectTypeSelector.selectedItem ?: "") as String

        filters.projectState = projectStateSelector.selectedItemPosition
        filters.projectStateName = (projectStateSelector.selectedItem ?: "") as String

        filters.isAvailableVacancies = withVacanciesCheckbox.isChecked

        (projectsList.adapter as ProjectsAdapter?)?.performSearch(
            searchInput.text
                .toString()
                .lowercase(Locale.getDefault())
                .trim(),
            filters
        )
    }

    private fun restoreFilters() {
        projectTypeSelector.setSelection(filters.projectType)
        projectStateSelector.setSelection(filters.projectState)
        withVacanciesCheckbox.isChecked = filters.isAvailableVacancies
    }

    override fun setupProjects(projects: List<ProjectInSearch>) {
        projectsAdapter.update(projects)

        val defaultFilterValue = getString(R.string.search_filters_selector_default)
        projectTypeSelector.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_simple,
            listOf(defaultFilterValue) + projects.map { it.type }.toSet().toList()
        )
        projectStateSelector.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_simple,
            listOf(defaultFilterValue) + projects.map { it.state }.toSet().toList()
        )

        restoreFilters()
        searchLoader.visibility = View.GONE
        projectsList.visibility = View.VISIBLE

        searchInput.isEnabled = true
        filterButton.isEnabled = true

        saveToDb(projects)
    }
}