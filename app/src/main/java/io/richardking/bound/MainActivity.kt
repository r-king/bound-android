package io.richardking.bound

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                Navigation.findNavController(this, R.id.my_nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.my_nav_host_fragment)
        setupActionBarWithNavController(navController)
        navigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() =
            findNavController(R.id.my_nav_host_fragment).navigateUp()


    override fun supportFragmentInjector() = dispatchingAndroidInjector

    fun setNavigationVisibility(visible: Boolean) {
        if (navigation.isShown && !visible) {
            navigation.visibility = View.GONE
        } else if (!navigation.isShown && visible) {
            navigation.visibility = View.VISIBLE
        }
    }
}