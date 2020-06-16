package com.abhijai.powerfulandroidapp.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState : DataState<*>?)

    fun expandAppBar()
}