package com.abhijai.powerfulandroidapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.abhijai.powerfulandroidapp.BaseActivity
import com.abhijai.powerfulandroidapp.R
import com.abhijai.powerfulandroidapp.ui.auth.AuthActivity
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.ChangePasswordFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.UpdateAccountFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.blog.UpdateBlogFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.blog.ViewBlogFragment
import com.abhijai.powerfulandroidapp.util.BottomNavController
import com.abhijai.powerfulandroidapp.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("ResourceType")
class MainActivity : BaseActivity(), BottomNavController.NavGraphProvider, BottomNavController.OnNavigationGraphChanged, BottomNavController.OnNavigationReSelectedListener
{

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bnc2 by lazy(LazyThreadSafetyMode.NONE){

        BottomNavController(this,R.id.main_nav_host_fragment,R.id.nav_blog,this,this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bnc2,this)
        // if it is the first time that activity is being launched
        if (savedInstanceState==null){
            bnc2.onNavigationItemSelected()
        }
        subscribeObservers()
    }

    private fun setUpActionBar(){
        setSupportActionBar(tool_bar)
    }

    private fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer {
            Log.d("MainActivity", "subscribeObservers AuthToken ->  $it")
            if (it==null || it.account_pk==-1 || it.token==null){
                navigateToAuthScreen()
            }
        })
    }

    private fun navigateToAuthScreen(){
        val intent = Intent(this,AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun getNavGraphId(itemId: Int) = when(itemId){
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    override fun onGraphChange() {
        expandAppBar()
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) = when(fragment) {

        is ViewBlogFragment->{
            navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        }
        is UpdateBlogFragment->{
            navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        }
        is UpdateAccountFragment->{
            navController.navigate(R.id.action_updateAccountFragment_to_home)
        }
        is ChangePasswordFragment->{
            navController.navigate(R.id.action_changePasswordFragment_to_home)
        }
        else->{
            // Do Nothing
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun onBackPressed() = bnc2.onBackPressed()
}
