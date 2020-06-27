package com.aldidwikip.contacts.view

import com.aldidwikip.contacts.model.DataContactModel

interface MainView {
    fun onContactsLoaded(dataContactModel: List<DataContactModel>)
    fun showSkeleton()
    fun hideSkeleton()
    fun hideSwipeRefresh()
    fun showConnectionError()
    fun hideConnectionError()
    fun onContactDeleted()
    fun onSearchSuccess()
    fun onSearchFailed()
}