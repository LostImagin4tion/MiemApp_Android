package ru.hse.miem.miemapp.presentation.schedule

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.layout_bottom_calendar.*
import kotlinx.android.synthetic.main.layout_bottom_schedule_settings.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.hse.miem.miemapp.MiemApplication
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.domain.entities.IScheduleItem
import ru.hse.miem.miemapp.presentation.OnBackPressListener
import ru.hse.miem.miemapp.presentation.base.BaseFragment
import ru.hse.miem.miemapp.presentation.profile.ProfileFragmentArgs
import java.text.SimpleDateFormat
import javax.inject.Inject

class ScheduleFragment: BaseFragment(R.layout.fragment_schedule), ScheduleView, OnBackPressListener {

    private val args: ProfileFragmentArgs by navArgs()

    private val calendar = CalendarHelper()

    private var defaultDate = calendar.getCurrentDate()
    private var startDate = defaultDate
    private var finishDate = calendar.getLastDate(defaultDate)

    private var defaultDateRu = calendar.getRuFormattedDate(defaultDate)
    private var startDateRu = defaultDateRu
    private var finishDateRu = calendar.getRuFormattedDate(finishDate)

    @Inject
    @InjectPresenter
    lateinit var schedulePresenter: SchedulePresenter

    @ProvidePresenter
    fun provideSchedulePresenter() = schedulePresenter

    private lateinit var calendarBehaviour: BottomSheetBehavior<View>
    private lateinit var scheduleSettingsBehavior: BottomSheetBehavior<View>

    private val scheduleAdapter = ScheduleAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as MiemApplication).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scheduleList.adapter = scheduleAdapter

        bottomScheduleLoader.visibility = View.GONE

        if(scheduleAdapter.hasData) {
            scheduleLoader.visibility = View.GONE
            scheduleList.visibility = View.VISIBLE
        }

        dateSelector.text = "$defaultDateRu - $finishDateRu"

        calendarBehaviour = BottomSheetBehavior.from(scheduleFilterLayout)
        calendarBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

        scheduleSettingsBehavior = BottomSheetBehavior.from(scheduleSettingsLayout)
        scheduleSettingsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        scheduleSettingsBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        })

        dateSelector.setOnClickListener {
            calendarBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }

        scheduleSettingsButton.setOnClickListener {
            scheduleSettingsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        scheduleSettingsCheckbox.setOnCheckedChangeListener { compoundButton, b ->
            scheduleSettingsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        scheduleCalendar.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val selectedDate = "$dayOfMonth/${month+1}/$year"

            scheduleCalendar.date = SimpleDateFormat("dd/MM/yyyy").parse(selectedDate).time

            startDate = calendar.getApiFormattedDate(year, month + 1, dayOfMonth)
            finishDate = calendar.getLastDate(startDate)

            startDateRu = calendar.getRuFormattedDate(startDate)
            finishDateRu = calendar.getRuFormattedDate(finishDate)

            dateSelector.text = "$startDateRu - $finishDateRu"

            schedulePresenter.onCreate(
                userId = args.userId.toString(),
                startDate = startDate,
                finishDate = finishDate,
                isTeacher = args.isTeacher
            )

            scheduleList.smoothSnapToPosition(0)

            calendarBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        
        scheduleList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!scheduleList.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    bottomScheduleLoader.visibility = View.VISIBLE

                    schedulePresenter.onScrolledDown(
                        userId = args.userId.toString(),
                        startDate = finishDate,
                        finishDate = calendar.getLastDate(finishDate),
                        isTeacher = args.isTeacher
                    )

                    finishDate = calendar.getLastDate(finishDate)
                    finishDateRu = calendar.getRuFormattedDate(finishDate)
                    dateSelector.text = "$startDateRu - $finishDateRu"
                }
            }
        })

        schedulePresenter.onCreate(
            userId = args.userId.toString(),
            startDate = startDate,
            finishDate = finishDate,
            isTeacher = args.isTeacher
        )
    }

    override fun onBackPressed(): Boolean { //FIXME investigate how to make this code nice

        if(scheduleSettingsBehavior.state == BottomSheetBehavior.STATE_HIDDEN &&
            calendarBehaviour.state != BottomSheetBehavior.STATE_COLLAPSED) {

            calendarBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        else if(calendarBehaviour.state == BottomSheetBehavior.STATE_HIDDEN &&
            scheduleSettingsBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {

            scheduleSettingsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        else if(scheduleSettingsBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            scheduleSettingsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        else if (calendarBehaviour.state != BottomSheetBehavior.STATE_COLLAPSED) {
            calendarBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            return true
        }
        return false
    }

    override fun updateSchedule(newItems: List<IScheduleItem>) {
        (scheduleList.adapter as ScheduleAdapter?)?.updateWhenScrolledDown(newItems)
        bottomScheduleLoader.visibility = View.GONE
    }

    override fun setupSchedule(items: List<IScheduleItem>) {
        scheduleAdapter.update(items)

        scheduleLoader.visibility = View.GONE
        scheduleList.visibility = View.VISIBLE
    }
}

fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}