package com.abhijai.powerfulandroidapp.di.main


import com.abhijai.powerfulandroidapp.ui.main.fragments.account.AccountFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.ChangePasswordFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.account.UpdateAccountFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.blog.BlogFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.blog.UpdateBlogFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.blog.ViewBlogFragment
import com.abhijai.powerfulandroidapp.ui.main.fragments.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}