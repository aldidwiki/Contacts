package com.aldidwikip.contacts.presenter

import android.content.Context
import android.util.Log.d
import android.widget.Toast
import com.aldidwikip.contacts.model.CRUDContactModel
import com.aldidwikip.contacts.service.ApiService
import com.aldidwikip.contacts.view.InsertView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class InsertPresenter(val context: Context, val view: InsertView) {
    private val apiService = ApiService.create()
    private lateinit var file: File

    private fun uploadPicture(mediaPath: String?) {
        view.showLoadingAnimation()
        file = File(mediaPath!!)

        val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        val pictureToUpload = MultipartBody.Part.createFormData("avatar", file.name, requestBody)

        val uploadCall = apiService.uploadFile(pictureToUpload)
        uploadCall.enqueue(object : Callback<CRUDContactModel> {
            override fun onFailure(call: Call<CRUDContactModel>, t: Throwable) {
                d(TAG, "onFailure Upload: ${t.message}")
                Toast.makeText(context, "Failed to Upload", Toast.LENGTH_SHORT).show()
                view.hideLoadingAnimation()
            }

            override fun onResponse(call: Call<CRUDContactModel>, response: Response<CRUDContactModel>) {
                d(TAG, "onResponse Upload: true")
                view.hideLoadingAnimation()
                view.isUploaded()
            }
        })
    }

    fun insertContact(name: String, number: String, address: String, mediaPath: String?) {
        view.showLoadingAnimation()
        var avatar: String? = null

        mediaPath?.let {
            uploadPicture(mediaPath)
            avatar = file.name
        } ?: run {
            avatar = null
        }

        val contactCall = apiService.insertContacts(name, number, address, avatar)
        contactCall.enqueue(object : Callback<CRUDContactModel> {
            override fun onFailure(call: Call<CRUDContactModel>, t: Throwable) {
                d(TAG, "onFailure Insert: ${t.message}")
                Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show()
                mediaPath ?: view.hideLoadingAnimation()
            }

            override fun onResponse(call: Call<CRUDContactModel>, response: Response<CRUDContactModel>) {
                d(TAG, "onResponse Insert: ${response.body()?.status}")
                Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                mediaPath ?: run {
                    view.hideLoadingAnimation()
                    view.isInserted()
                }
            }
        })
    }

    companion object {
        private const val TAG = "InsertPresenter"
    }
}