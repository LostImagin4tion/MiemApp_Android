package ru.hse.miem.miemapp.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import ru.hse.miem.miemapp.MiemApplication
import ru.hse.miem.miemapp.R
import ru.hse.miem.miemapp.presentation.OnBackPressListener
import ru.hse.miem.miemapp.presentation.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var originalIntent: Intent
    val intentUri get() = originalIntent.data

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MiemApplication).appComponent.inject(this)
        setTheme(R.style.Theme_MIEMApp)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // because default behaviour not suits us, we want to login first in all cases
        originalIntent = intent.clone() as Intent
        intent.data = null

        if (savedInstanceState == null) { // not screen rotation or something like this
            bottomNavigation.setupWithNavController(
                navGraphIds = listOf(
                    R.navigation.nav_profile, // keep first!!
                    R.navigation.nav_schedule,
                    R.navigation.nav_search,
                    R.navigation.nav_apps,
                ),
                fragmentManager = supportFragmentManager,
                containerId = R.id.navHost,
                intent = intent,
                fragmentsBarGone = listOf(R.id.fragmentLogin)
            )

            bottomNavigation.post {
                bottomNavigation.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.fragments.find { it is NavHostFragment } as NavHostFragment?
        navHostFragment?.childFragmentManager?.fragments?.first()?.let {
            if (it is OnBackPressListener) {
                if (!it.onBackPressed()) super.onBackPressed()
            } else {
                super.onBackPressed()
            }
        } ?: run {
            super.onBackPressed()
        }

    }

}