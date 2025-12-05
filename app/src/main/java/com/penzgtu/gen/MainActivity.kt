package com.penzgtu.gen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.penzgtu.gen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding
        get() = _binding!!
    private var _binding: ActivityMainBinding? = null
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.apply {
            statusBarColor = ContextCompat.getColor(context, R.color.main)
            navigationBarColor = ContextCompat.getColor(context, R.color.main)
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.details -> Toast.makeText(
                    applicationContext,
                    "\"Не для коммерческого использования\"\nРазработал: Гордеев А.В.\nВерсия приложения: 1.0",
                    Toast.LENGTH_LONG
                ).show()

                else -> Unit
            }
            return@setOnMenuItemClickListener true
        }

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.PassGen, R.id.PassSelect, R.id.PassCheck
        ).build()
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            navController.navigate(
                item.itemId, null, NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(navController.graph.startDestinationId, false, true)
                    .build()
            )
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { _, windowInsets ->
            val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            binding.root.updatePadding(bottom = insets.bottom, top = insets.top)

            windowInsets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
