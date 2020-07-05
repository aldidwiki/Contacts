package com.aldidwikip.contacts.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.fragment.CustomBottomDialogFragment
import com.aldidwikip.contacts.presenter.EditPresenter
import com.aldidwikip.contacts.view.EditView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity(), EditView, CustomBottomDialogFragment.ItemClickListener {
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var number: String
    private lateinit var address: String
    private lateinit var avatar: String
    private var mediaPath: String? = null
    private var imageRemoved: Boolean = false
    private val presenter = EditPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.apply {
            id = getStringExtra("ID")!!
            name = getStringExtra("Name")!!
            number = getStringExtra("Number")!!
            address = getStringExtra("Address")!!
            avatar = getStringExtra("ProfilePic")!!
        }
        edtName.setText(name)
        edtNumber.setText(number)
        edtAddress.setText(address)
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24)
        Glide.with(this)
                .applyDefaultRequestOptions(requestOptions)
                .load(avatar)
                .into(profileImage)

        profileImage.setOnClickListener {
            val bottomDialog = CustomBottomDialogFragment.newInstance()
            val ft = supportFragmentManager.beginTransaction()
            bottomDialog.show(ft, "BOTTOM DIALOG")
        }

        btnEdit.setOnClickListener {
            updateContacts()
        }
    }

    override fun onDestroy() {
        btnEdit.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mediaPath = ImagePicker.getFilePath(data)
            val requestOption = RequestOptions()
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
            Glide.with(this)
                    .applyDefaultRequestOptions(requestOption)
                    .load(mediaPath)
                    .into(profileImage)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.icon_delete -> {
                MaterialDialog(this).show {
                    cancelable(false)
                    title(text = "Delete Confirmation")
                    message(text = "Are you sure want to delete $name as a contact?")
                    positiveButton(text = "Yes") {
                        presenter.deleteContact(id)
                    }
                    negativeButton(text = "No")
                }
            }
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateContacts() {
        fun TextInputEditText.setErrorMessage() {
            this.error = "Column Can't be Empty!"
        }

        name = edtName.text.toString()
        number = edtNumber.text.toString()
        address = edtAddress.text.toString()

        when {
            TextUtils.isEmpty(name) -> edtName.setErrorMessage()
            TextUtils.isEmpty(number) -> edtNumber.setErrorMessage()
            TextUtils.isEmpty(address) -> edtAddress.setErrorMessage()
            else -> presenter.updateContacts(id, name, number, address, avatar, mediaPath, imageRemoved)
        }
    }

    override fun isUpdated() {
        val returnIntent = Intent().apply {
            putExtra("hasBackPressed", false)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun isUploaded() {
        val returnIntent = Intent().apply {
            putExtra("hasBackPressed", false)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onBackPressed() {
        val returnIntent = Intent().apply {
            putExtra("hasBackPressed", true)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun showLoadingAnimation() {
        btnEdit.startAnimation()
    }

    override fun hideLoadingAnimation() {
        btnEdit.revertAnimation()
    }

    override fun onItemClicked(item: Int) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(profileImage)
            mediaPath = null
            imageRemoved = true
        }
    }
}