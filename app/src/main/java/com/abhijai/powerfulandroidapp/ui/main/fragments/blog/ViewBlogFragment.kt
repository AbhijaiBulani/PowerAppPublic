package com.abhijai.powerfulandroidapp.ui.main.fragments.blog

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.abhijai.powerfulandroidapp.R

class ViewBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }


    private fun navToUpdateBlogFragment(){
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //TODO("Check if user is author of blog post")
        val isUserIsAuthorOfBlog = true
        if (isUserIsAuthorOfBlog){
            inflater.inflate(R.menu.edit_view_menu,menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isUserIsAuthorOfBlog = true
        if (isUserIsAuthorOfBlog){
            when(item.itemId){
                R.id.edit -> {
                    navToUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
