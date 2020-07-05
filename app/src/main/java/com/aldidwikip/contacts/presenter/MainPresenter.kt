package com.aldidwikip.contacts.presenter

import android.content.Context
import android.util.Log.d
import android.widget.Toast
import com.aldidwikip.contacts.model.CRUDContactModel
import com.aldidwikip.contacts.model.ContactModel
import com.aldidwikip.contacts.model.DataContactModel
import com.aldidwikip.contacts.service.ApiService
import com.aldidwikip.contacts.view.MainView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainPresenter(val context: Context, val view: MainView) {
    private val apiService = ApiService.create()
    private var contactList: List<DataContactModel>? = null

    fun loadContacts() {
        view.showSkeleton()
        val contactCall = apiService.getContacts()
        contactCall.enqueue(object : Callback<ContactModel> {
            override fun onFailure(call: Call<ContactModel>, t: Throwable) {
                view.showConnectionError()
                view.hideSwipeRefresh()
                d(TAG, "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<ContactModel>, response: Response<ContactModel>) {
                view.hideConnectionError()
                view.hideSkeleton()
                view.hideSwipeRefresh()
                d(TAG, "onResponse: ${response.body()?.status}")
                contactList = response.body()?.result
                contactList?.let { view.onContactsLoaded(it) }
            }
        })
    }

    fun deleteContact(id: String) {
        val contactCall = apiService.deleteContacts(id)
        contactCall.enqueue(object : Callback<CRUDContactModel> {
            override fun onFailure(call: Call<CRUDContactModel>, t: Throwable) {
                d(TAG, "onFailure Delete: ${t.message}")
                Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<CRUDContactModel>, response: Response<CRUDContactModel>) {
                d(TAG, "onResponse Delete: ${response.body()?.status}")
                Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                view.onContactDeleted()
            }
        })
    }

    fun searchContact(keywords: String) {
        val searchCall = apiService.searchContacts(keywords)
        searchCall.enqueue(object : Callback<ContactModel> {
            override fun onFailure(call: Call<ContactModel>, t: Throwable) {
                d(TAG, "onFailure Search: ${t.message}")
                view.onSearchFailed()
                view.showConnectionError()
            }

            override fun onResponse(call: Call<ContactModel>, response: Response<ContactModel>) {
                d(TAG, "onResponse Search: ${response.body()?.status}")
                contactList = response.body()?.result
                contactList?.let {
                    view.onSearchSuccess()
                    view.onContactsLoaded(it)
                } ?: run {
                    view.onSearchFailed()
                }
            }
        })
    }

    companion object {
        private const val TAG = "MainPresenter"
    }
}