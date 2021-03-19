package ru.hse.miem.miemapp.presentation.tinder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_tinder.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.hse.miem.miemapp.MiemApplication
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.domain.entities.ProjectExtended
import ru.hse.miem.miemapp.domain.entities.ProjectInSearch
import ru.hse.miem.miemapp.presentation.base.BaseFragment
import java.util.*
import javax.inject.Inject


class TinderFragment : BaseFragment(R.layout.fragment_tinder), InfoView{
    private var adapter: CardStackAdapter = CardStackAdapter()
    private var items: ArrayList<ItemModel> = arrayListOf()
    private val listener = CardStackCallback()
    private lateinit var manager: CardStackLayoutManager

    @Inject
    @InjectPresenter
    lateinit var presenter: TinderPresenter

    @ProvidePresenter
    fun provideSearchPresenter() = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MiemApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("ItIsHere", "1")

        if (items.isNotEmpty()) {
            Log.d("ItIsHere", items.size.toString())
        }

        if (adapter.hadData){
            tinderLoader.visibility = View.GONE
        }

        viewAll.setOnClickListener {
            if (listener.likeVacancy.size > 3){
                Log.d("LogTinderAct", adapter.screenItems[1].vacancy)
            }
            Log.d("LogTinderAct", listener.likeVacancy.size.toString())
        }

        presenter.onCreate()
    }

    override fun setupProjects(projects: List<ProjectInSearch>) {
        if (projects.isEmpty()){
            Log.d("MyLogs", "Empty")
        }else{
            Log.d("MyLogs", "NotEmpty")
        }

        for (i in projects.indices) {
            if (projects[i].vacancies > 0){
                Log.d("MyLogs", "Yes")
                presenter.infoProject(projects[i].id)
            }else{
                Log.d("MyLogs", "No")
            }
        }
    }

    override fun setupProject(project: ProjectExtended){
        if (items.size == 0) {
            items.add(
                ItemModel(
                    R.drawable.sample2,
                    "",
                    "Добро пожаловать в проектный тиндер",
                    "",
                    "",
                    ""
                )
            )
            items.add(
                ItemModel(
                    R.drawable.sample2,
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        for (i in project.vacancies.indices) {
            items.add(
                items.size - 1,
                ItemModel(
                    R.drawable.sample1,
                    "#" + project.id + " " + project.type,
                    project.name,
                    project.vacancies[i].role,
                    project.vacancies[i].recommended + " " + project.vacancies[i].required,
                    ""
                )
            )
            Log.d(
                "InfoMyLogs",
                i.toString() + " " + project.state + " " + project.vacancies[i].role
            )
        }
        smt()
    }

    fun smt(){
        manager = CardStackLayoutManager(this.requireContext(), listener)
        manager.setStackFrom(StackFrom.None)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())
        adapter = CardStackAdapter(items)
        val cardStackView: CardStackView = card_stack_view
        cardStackView.layoutManager = manager;
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator ()
    }

    override fun showError() {}
}