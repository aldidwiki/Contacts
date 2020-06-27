package com.aldidwikip.contacts.model

data class ContactModel(
        val status: Boolean,
        val message: String,
        val result: List<DataContactModel>
)

data class CRUDContactModel(
        val status: Boolean,
        val message: String,
        val result: DataContactModel
)

data class DataContactModel(
        val id: String,
        val nama: String,
        val nomor: String,
        val alamat: String,
        val avatar: String
)

