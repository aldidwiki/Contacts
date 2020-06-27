package com.aldidwikip.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.listener.ContactListener
import com.aldidwikip.contacts.model.DataContactModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_list.view.*

class ContactAdapter(
        private val mDataContact: List<DataContactModel>,
        private val listener: ContactListener
) :
        RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.contact_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        with(holder) {
            bind(mDataContact[position])

            mContactParent.setOnClickListener {
                listener.onContactClicked(mDataContact[adapterPosition], false)
            }

            mProfilePic.setOnClickListener {
                listener.onAvatarClicked(mDataContact[adapterPosition])
            }
        }
    }

    override fun getItemCount() = mDataContact.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mName = itemView.tvName
        private val mNumber = itemView.tvNumber
        private val mAddress = itemView.tvAddress
        val mProfilePic: CircleImageView = itemView.profileImage
        val mContactParent: RelativeLayout = itemView.contactParent

        fun bind(dataContact: DataContactModel) {
            mName.text = dataContact.nama
            mNumber.text = dataContact.nomor
            mAddress.text = dataContact.alamat

            val requestOption = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)

            Glide.with(itemView)
                    .applyDefaultRequestOptions(requestOption)
                    .load(dataContact.avatar)
                    .into(mProfilePic)
        }
    }
}

