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
import ru.hse.miem.miemapp.domain.entities.Vacancies
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

        if (items.size == 0) {
            items.add(
                ItemModel(
                    R.drawable.sample2,
                    "",
                    "Добро пожаловать в проектный тиндер\n\nСвайп вправо, если понравилась вакансия",
                    "Свайп влево, если нет",
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

    override fun setupVacancies(projects: List<Vacancies>) {
        for (item in projects){
            items.add(items.size - 1,
                ItemModel(
                    R.drawable.sample1,
                    "#" + item.project_id,
                    item.project_name_rus,
                    item.vacancy_role,
                    item.vacancy_disciplines.toString(),
                    ""
                )
            )
        }
        filling()
    }

    fun filling(){
        manager = CardStackLayoutManager(this.requireContext(), listener)
        manager.setStackFrom(StackFrom.None)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())
        adapter = CardStackAdapter(items)
        val cardStackView: CardStackView = card_stack_view
        cardStackView.layoutManager = manager;
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator ()
    }

    override fun showError() {

    }
}