package com.aldidwikip.contacts.service

import com.aldidwikip.contacts.model.CRUDContactModel
import com.aldidwikip.contacts.model.ContactModel
import com.aldidwikip.contacts.utils.Constant
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {
    @GET("contacts")
    fun getContacts(): Call<ContactModel>

    @GET("contacts")
    fun searchContacts(@Query("keywords") keywords: String): Call<ContactModel>

    @FormUrlEncoded
    @POST("contacts")
    fun insertContacts(@Field("nama") nama: String,
                       @Field("nomor") nomor: String,
                       @Field("alamat") alamat: String,
                       @Field("avatar") avatar: String?
    ): Call<CRUDContactModel>

    @FormUrlEncoded
    @PUT("contacts")
    fun updateContacts(@Field("id") id: String,
                       @Field("nama") nama: String,
                       @Field("nomor") nomor: String,
                       @Field("alamat") alamat: String,
                       @Field("avatar") avatar: String?
    ): Call<CRUDContactModel>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "contacts", hasBody = true)
    fun deleteContacts(@Field("id") id: String): Call<CRUDContactModel>

    @Multipart
    @POST("contacts")
    fun uploadFile(@Part file: MultipartBody.Part): Call<CRUDContactModel>

    companion object {
        private const val BASE_URL = "https://api.kuratori.xyz/"
        private val AUTH_TOKEN = Constant.AUTH_TOKEN

        fun create(): ApiService {
            val httpClient = OkHttpClient.Builder().apply {
                connectTimeout(20, TimeUnit.SECONDS)
                readTimeout(20, TimeUnit.SECONDS)
                writeTimeout(20, TimeUnit.SECONDS)
                addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request().newBuilder()
                                .addHeader("TOKEN", AUTH_TOKEN)
                                .build()

                        return chain.proceed(request)
                    }
                })
            }.build()

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}