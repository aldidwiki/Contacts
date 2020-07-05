package com.aldidwikip.contacts.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.listener.ContactListener
import com.aldidwikip.contacts.model.DataContactModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.custom_dialog_fragment.*

class CustomDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var listener: ContactListener
    private lateinit var dataContact: DataContactModel
    private lateinit var contactName: String

    fun intentToEdit(listener: ContactListener, dataContact: DataContactModel) {
        this.listener = listener
        this.dataContact = dataContact
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_dialog_fragment, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_bg)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactName = arguments?.getString("contactName").toString()
        val contactAvatar = arguments?.getString("contactAvatar")

        tvNameDialog.text = contactName

        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
        Glide.with(this)
                .applyDefaultRequestOptions(requestOptions)
                .load(contactAvatar)
                .centerCrop()
                .into(profileImage)

        iconEdit.setOnClickListener(this)
        iconDelete.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iconEdit -> listener.onContactClicked(dataContact, false)
            R.id.iconDelete -> {
                MaterialDialog(activity!!).show {
                    cancelable(false)
                    title(text = "Delete Confirmation")
                    message(text = "Are you sure want to delete $contactName as a contact?")
                    positiveButton(text = "Yes") {
                        listener.onContactClicked(dataContact, true)
                    }
                    negativeButton(text = "No")
                }
            }
        }
        dismiss()
    }

    companion object {
        fun newInstance(dataContact: DataContactModel): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            val args = Bundle().apply {
                putString("contactID", dataContact.id)
                putString("contactName", dataContact.nama)
                putString("contactNumber", dataContact.nomor)
                putString("contactAddress", dataContact.alamat)
                putString("contactAvatar", dataContact.avatar)
            }

            fragment.arguments = args
            return fragment
        }
    }

}