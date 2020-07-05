package com.aldidwikip.contacts.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aldidwikip.contacts.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_dialog_fragment.*
import java.io.File

class CustomBottomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var listener: ItemClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        removePhoto.setOnClickListener(this)
        gallery.setOnClickListener(this)
        camera.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(v: View?) {
        val pathName = "/Compressed Image"
        val rootPath = File(v?.context?.getExternalFilesDir(pathName).toString())

        when (v?.id) {
            R.id.removePhoto -> listener?.onItemClicked(R.id.removePhoto)
            R.id.gallery -> {
                ImagePicker.with(this)
                        .galleryOnly()
                        .compress(200)
                        .saveDir(rootPath)
                        .start()
            }
            R.id.camera -> {
                ImagePicker.with(this)
                        .cameraOnly()
                        .compress(200)
                        .saveDir(rootPath)
                        .start()
            }
        }
        dismiss()
    }

    interface ItemClickListener {
        fun onItemClicked(item: Int)
    }

    companion object {
        fun newInstance() = CustomBottomDialogFragment()
    }
}
