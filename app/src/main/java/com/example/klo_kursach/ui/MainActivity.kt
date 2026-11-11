package com.example.klo_kursach.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.UserSession
import com.example.klo_kursach.databinding.ActivityMainBinding
import com.example.klo_kursach.ui.auth.AuthActivity
import com.example.klo_kursach.ui.common.applySystemBarsPadding
import com.example.klo_kursach.util.ThemeManager

/**
 * Главная активность (single-activity): панель инструментов, NavHostFragment
 * с графом nav_main и нижняя навигация по четырём разделам.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    /** Разделы нижней навигации (на них нет кнопки «Назад»). */
    private val topLevelDestinations = setOf(
        R.id.notesFragment,
        R.id.subjectsFragment,
        R.id.searchFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!UserSession.isLoggedIn(this)) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()
        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id in topLevelDestinations
            invalidateOptionsMenu()
        }

        addMenuProvider(mainMenuProvider)
    }

    private val mainMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }

        override fun onPrepareMenu(menu: Menu) {
            // Общие пункты показываем только на основных разделах
            val isTopLevel = navController.currentDestination?.id in topLevelDestinations
            menu.findItem(R.id.action_toggle_theme)?.isVisible = isTopLevel
            menu.findItem(R.id.settingsFragment)?.isVisible = isTopLevel
            menu.findItem(R.id.aboutFragment)?.isVisible = isTopLevel
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
            R.id.action_toggle_theme -> {
                ThemeManager.toggle(this@MainActivity)
                true
            }
            R.id.settingsFragment -> {
                navController.navigate(R.id.action_global_settings)
                true
            }
            R.id.aboutFragment -> {
                navController.navigate(R.id.action_global_about)
                true
            }
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}
