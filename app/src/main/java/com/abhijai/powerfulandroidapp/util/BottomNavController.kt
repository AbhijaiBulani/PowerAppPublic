package com.abhijai.powerfulandroidapp.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.abhijai.powerfulandroidapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Q1-> What is the need of this class?
 * A1 -> When u click any item on BottomNavigation, we r going to programmatically adding nav_host_fragment
 *       to the activity.
 *       Basically this class is going to maintain 4 BackStacks.
 *       1 BackStack for internal navigation inside Home
 *       1 BackStack for internal navigation inside Blog
 *       1 BackStack for internal navigation inside Account
 *       1 BackStack for BottomNavigation(Home, Blog, Account)
 */
class BottomNavController(
    val context: Context,
    @IdRes val containerId : Int,// main_nav_host_fragment inside activity_main
    @IdRes val appStartDestinationId : Int,// this is the startDestination for the navigation(blogFragment)
    val graphChangeListener:OnNavigationGraphChanged?,
    val navGraphProvider:NavGraphProvider
)
{
    lateinit var activity : Activity
    lateinit var fragmentManager : FragmentManager
    lateinit var navItemChangeListener:OnNavigationItemChanged

    private val navigationBackStack = BackStack.of(appStartDestinationId)

    init {
        if (context is Activity){
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    //for setting the checked icon in Bottom Nav
    // this interface is going to be used internally i.e -> No activity is going to implement this
    interface OnNavigationItemChanged{
        fun onItemChanged(itemId:Int)
    }

    // Get id of each nav graph
    // for e.g -> R.navigation.nav_blog
    interface NavGraphProvider{
        // below annotation tells that we are going to return -> navigation resource reference.
        // for e.g -> R.navigation.flow
        @NavigationRes
        fun getNavGraphId(itemId:Int):Int
    }

    //this interface is going to communicate with the activity
    // Executes when navigation graph changes
    // ex : Select a new item on the Bottom nav
    // from Home to Account
    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }

    interface OnNavigationReSelectedListener{
        //NavController manages app navigation within a {@link NavHost}.
        fun onReselectNavItem(navController:NavController, fragment:Fragment)
    }

    /**
     * bottomNavController.setOnItemNavigationChanged {itemId->
        menu.findItem(itemId).isChecked = true
        }
     */
    fun setOnItemNavigationChanged(listener : (itemId:Int)->Unit)
    {
        this.navItemChangeListener = object : OnNavigationItemChanged{
            // we are programmatically invoking OnNavigationItemChanged interface b'coz it is not implemented by the activity
            override fun onItemChanged(itemId: Int) {
              listener.invoke(itemId)
            }

        }
    }

    /**
     * This BackStack data structure is for managing the backStack for each of navHostFragment
     * which get added when we click bottom navigation.
     * In our case there will be 3 navHostFragment in MainModule.
     *
     * This is only for NavigationHostFragments.
     */
    private class BackStack : ArrayList<Int>()
    {
        companion object
        {
            fun of(vararg elements : Int):BackStack
            {
                // elements are the id's of nav_host_fragment
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }
        fun removeLast() = removeAt(size-1)
        fun moveLast(item:Int){
            /**
             * Removes the first occurrence of the specified element from this list,
             * if it is present.  If the list does not contain the element, it is
             * unchanged.
             */
            remove(item)
            add(item)
        }
    }

    /**
     * This function is one of the most important function of this class.
     * B'coz -> This fun will determines what going to happen when we click on navigation item(Home, Blog, Account)
     *
     * We want to add NavHostFragment programmatically when navigation item is going to be clicked.
     * And we also need to manage the individual BackStack, which maintains the fragments inside NavHost.
     *
     * NavHostFragment -> NavHostFragment provides an area within your layout for self-contained navigation to occur.*/
    // if parameter is not passed into this fun then it will take the last element form the list
    fun onNavigationItemSelected(itemId : Int = navigationBackStack.last()):Boolean
    {
        // Replace fragment when we click on other navigation item
        // below code will either find the fragment which we have visited or create a new NavHostFragment.
        // For eg -> If we are at home and click at account then we are going to replace BlogFragment with AccountFragment
        Log.d("BottomNavController", "onNavigationItemSelected (line 138): $itemId")

        if (fragmentManager.findFragmentByTag(itemId.toString())==null){
            Log.d("BottomNavController", "onNavigationItemSelected NO Fragment found")
        }
        else{
            Log.d("BottomNavController", "onNavigationItemSelected Fragment found")
        }

        /*
        below code will always return NavHostFragment
        if it not exist for the given itemId then it will create new NavHostFragment,
        and if it exist then it will return already exist NavHostFragment.
        */

        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?:NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())// replace oldFragment in containerId with new fragment
            .addToBackStack(null)//Add this transaction to the back stack and remember this transaction after it is committed.
            .commit()

        // Add to back stack
        navigationBackStack.moveLast(itemId)

        // Update checked icon, icon color grey to blue
        navItemChangeListener.onItemChanged(itemId)

        // communicate with Activity, tell activity that something change.
        // because of this in activity we can cancel the n/w job if user wants
        graphChangeListener?.onGraphChange()
        return true
    }



    //This method is going to be used in Activity
    fun onBackPressed(){
        val currentNavHostFragment = fragmentManager.findFragmentById(containerId)!!
        val navHostFragmentPersonalFragmentManager = currentNavHostFragment.childFragmentManager
        when{
            //This condition is for BackStack(Android's) inside NavHostFragment.
            // this will be true if there are fragments inside NavHostFragment BackStack
            navHostFragmentPersonalFragmentManager.popBackStackImmediate()->{

            }

            //this condition will be true if there is no backStack(Android's) for Current NavHostFragment and now
            // we are talking about the BackStack(our) of BottomNavigation i.e BackStack b/w NavHostFragments(not inside them but for them).
            navigationBackStack.size > 1 ->{
                navigationBackStack.removeLast()
                // update the container with new fragment
                onNavigationItemSelected()
            }

           /* *
             * if the stack has only one and it is not the navigation home we should
             * ensure that the application only leaves from start destination.
             *
             * This condition is basically a pre-cautions*/

            navigationBackStack.last() != appStartDestinationId ->{
                navigationBackStack.removeLast()
                navigationBackStack.add(0,appStartDestinationId)
                onNavigationItemSelected()
            }

            else -> activity.finish()

        }
    }


}

//BottomNavigationView -> Represents a standard bottom navigation bar for application.
fun BottomNavigationView.setUpNavigation(bottomNavController: BottomNavController, onReselectListener : BottomNavController.OnNavigationReSelectedListener)
{
    /**
     * Set a listener that will be notified when a bottom navigation item is selected. This listener
     * will also be notified when the currently selected item is reselected unless an {@link
     * OnNavigationItemReselectedListener} has also been set.
     *
     * So this function will not be called when item is going to be reselected b'coz we are also setting OnNavigationItemReselectedListener below.
     */
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }
    /**
     * Set a listener that will be notified when the currently selected bottom navigation item is
     * reselected.
     * Here this listener will help us to go back to the starting fragment, for eg if u navigate -> BlogFragment to ViewBlogFragment to UpdateBlogFragment
     *  And at UpdateBlogFragment if u reselect the icon u will be navigated back to the BlogFragment(starting fragment)
     *
     *  It accesses the BackStack(our) for Fragments that are inside of each individual NavigationHost.
     *
     *  This is basically Secondary BackStack.
     *
     *  This backStack is for the fragments that are in each of the NavigationHost.
     */
    setOnNavigationItemReselectedListener {
        // here we are finding the fragment which is residing inside the FrameLayout inside activity_main(containerId)
        // I think this fragment is NavigationHostFragment(not sure yet)
        val navHostFragment = bottomNavController.fragmentManager.findFragmentById(bottomNavController.containerId)!!
        //fragment.childFragmentManager -> Return a private FragmentManager for placing and managing Fragments inside of this Fragment.
        val childFragmentManager = navHostFragment.childFragmentManager
        //Get a list of all fragments that are currently added to the FragmentManager.
        val fragmentsInsideOfNavigationHost = childFragmentManager.fragments
        // here we are trying to find the first fragment that is inside of whatever NavigationHost currently in view.
        val fragmentAtZeroPosition = fragmentsInsideOfNavigationHost[0]
        fragmentAtZeroPosition?.let {fragmentAtZero ->
            //NavController manages app navigation within a {@link NavHost}.
            val navController = bottomNavController.activity.findNavController(bottomNavController.containerId)

              /*Ques -> Where is navHost in MainActivity?
              Ans -> I think main_nav_host_fragment is the NavHost which will host our NavGraphs(nav_blog,nav_account,nav_create_blog)*/

            onReselectListener.onReselectNavItem(navController,fragmentAtZero)
        }
    }


    bottomNavController.setOnItemNavigationChanged {itemId->
        menu.findItem(itemId).isChecked = true
    }
}