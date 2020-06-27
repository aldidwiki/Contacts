package com.aldidwikip.contacts.presenter

import android.content.Context
import android.util.Log.d
import android.widget.Toast
import com.aldidwikip.contacts.model.CRUDContactModel
import com.aldidwikip.contacts.service.ApiService
import com.aldidwikip.contacts.view.EditView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditPresenter(val context: Context, val view: EditView) {
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

            override fun onResponse(
                    call: Call<CRUDContactModel>,
                    response: Response<CRUDContactModel>
            ) {
                d(TAG, "onResponse Upload: true")
                view.hideLoadingAnimation()
                view.isUploaded()
            }
        })
    }

    fun updateContacts(id: String, name: String, number: String, address: String,
                       avatar: String, mediaPath: String?, imageRemoved: Boolean) {

        view.showLoadingAnimation()
        var strAvatar: String? = null

        mediaPath?.let {
            uploadPicture(mediaPath)
            strAvatar = file.name
        } ?: run {
            strAvatar = if (imageRemoved) {
                null
            } else {
                avatar.substring(avatar.lastIndexOf("/") + 1)
            }
        }
        val contactCall = apiService.updateContacts(id, name, number, address, strAvatar)
        contactCall.enqueue(object : Callback<CRUDContactModel> {
            override fun onFailure(call: Call<CRUDContactModel>, t: Throwable) {
                d(TAG, "onFailure Edit: ${t.message}")
                Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show()
                mediaPath ?: view.hideLoadingAnimation()
            }

            override fun onResponse(
                    call: Call<CRUDContactModel>,
                    response: Response<CRUDContactModel>
            ) {
                d(TAG, "onResponse Edit: ${response.body()?.status}")
                Toast.makeText(
                        context, response.body()?.message, Toast.LENGTH_SHORT
                ).show()
                mediaPath ?: run {
                    view.hideLoadingAnimation()
                    view.isUpdated()
                }
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

            override fun onResponse(
                    call: Call<CRUDContactModel>, response: Response<CRUDContactModel>
            ) {
                d(TAG, "onResponse Delete: ${response.body()?.status}")
                Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                view.isUpdated()
            }
        })
    }

    companion object {
        private const val TAG = "EditPresenter"
    }
}