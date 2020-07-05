package com.aldidwikip.contacts.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.fragment.CustomBottomDialogFragment
import com.aldidwikip.contacts.presenter.InsertPresenter
import com.aldidwikip.contacts.view.InsertView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_insert.*

class InsertActivity : AppCompatActivity(), InsertView,
        CustomBottomDialogFragment.ItemClickListener {

    private val presenter = InsertPresenter(this, this)
    private var mediaPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        profileImage.setOnClickListener {
            val bottomDialogFragment = CustomBottomDialogFragment.newInstance()
            val ft = supportFragmentManager.beginTransaction()
            bottomDialogFragment.show(ft, "BOTTOM DIALOG")
        }

        btnInsert.setOnClickListener {
            insertContacts()
        }
    }

    override fun onDestroy() {
        btnInsert.dispose()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertContacts() {
        fun TextInputEditText.setErrorMessage() {
            this.error = "Column Can't be Empty!"
        }

        val name = edtName.text.toString()
        val number = edtNumber.text.toString()
        val address = edtAddress.text.toString()

        when {
            TextUtils.isEmpty(name) -> edtName.setErrorMessage()
            TextUtils.isEmpty(number) -> edtNumber.setErrorMessage()
            TextUtils.isEmpty(address) -> edtAddress.setErrorMessage()
            else -> presenter.insertContact(name, number, address, mediaPath)
        }
    }

    override fun isInserted() {
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
        btnInsert.startAnimation()
    }

    override fun hideLoadingAnimation() {
        btnInsert.revertAnimation()
    }

    override fun onItemClicked(item: Int) {
        if (item == R.id.removePhoto) {
            Glide.with(this).clear(profileImage)
            mediaPath = null
        }
    }
}
