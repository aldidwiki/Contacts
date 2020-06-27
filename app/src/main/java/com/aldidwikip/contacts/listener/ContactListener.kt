package com.aldidwikip.contacts.listener

import com.aldidwikip.contacts.model.DataContactModel

interface ContactListener {
    fun onContactClicked(dataContact: DataContactModel, isDelete: Boolean)
    fun onAvatarClicked(dataContact: DataContactModel)
}