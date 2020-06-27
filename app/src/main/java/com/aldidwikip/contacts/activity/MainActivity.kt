package com.aldidwikip.contacts.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.adapter.ContactAdapter
import com.aldidwikip.contacts.listener.ContactListener
import com.aldidwikip.contacts.model.DataContactModel
import com.aldidwikip.contacts.presenter.MainPresenter
import com.aldidwikip.contacts.fragment.ConnectionErrorFragment
import com.aldidwikip.contacts.fragment.CustomDialogFragment
import com.aldidwikip.contacts.view.MainView
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ContactListener, MainView {
    private val presenter = MainPresenter(this, this)
    private var hasBackPressed: Boolean? = false
    private lateinit var skeleton: Skeleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initSwipeRefresh()
        initRecyclerView()
        iniSkeleton()

        fabInsert.setOnClickListener {
            val intentToInsert = Intent(this, InsertActivity::class.java)
            startActivityForResult(intentToInsert, INTENT_TO_INSERT)
        }

        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search_view.clearFocus()
                presenter.searchContact(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.searchContact(newText!!)
                return true
            }
        })

        search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewClosed() {
                presenter.loadContacts()
                onSearchSuccess()
                fabInsert.visibility = View.VISIBLE
                swipeRefresh.isEnabled = true
            }

            override fun onSearchViewShown() {
                onSearchFailed()
            }

        })
    }

    override fun onBackPressed() {
        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val menuItem = menu?.findItem(R.id.action_search)
        search_view.setMenuItem(menuItem)

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_TO_INSERT || requestCode == INTENT_TO_EDIT) {
                hasBackPressed = data?.getBooleanExtra("hasBackPressed", false)
            }
        }
    }

    private fun initSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            presenter.loadContacts()
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val myID = android.os.Process.myPid()
        android.os.Process.killProcess(myID)
    }

    override fun onResume() {
        super.onResume()
        if (!hasBackPressed!!) {
            if (search_view.isSearchOpen) {
                search_view.closeSearch()
            }
            presenter.loadContacts()
        }
    }

    private fun iniSkeleton() {
        skeleton =
            recyclerView.applySkeleton(R.layout.contact_list, 10, shimmerDurationInMillis = 1000L)
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && fabInsert.visibility == View.VISIBLE) {
                        fabInsert.hide()
                    } else if (dy < 0 && fabInsert.visibility != View.VISIBLE) {
                        fabInsert.show()
                    }
                }
            })
        }
    }

    override fun onContactClicked(dataContact: DataContactModel, isDelete: Boolean) {
        if (isDelete) {
            presenter.deleteContact(dataContact.id)
        } else {
            val intentToEdit = Intent(this, EditActivity::class.java)
                .apply {
                    putExtra("ID", dataContact.id)
                    putExtra("Name", dataContact.nama)
                    putExtra("Number", dataContact.nomor)
                    putExtra("Address", dataContact.alamat)
                    putExtra("ProfilePic", dataContact.avatar)
                }
            startActivityForResult(intentToEdit, INTENT_TO_EDIT)
        }
    }

    override fun onAvatarClicked(dataContact: DataContactModel) {
        val dialogFragment = CustomDialogFragment.newInstance(dataContact)
        val ft = supportFragmentManager.beginTransaction()
        dialogFragment.intentToEdit(this, dataContact)
        dialogFragment.show(ft, "CUSTOM DIALOG")
    }

    override fun onContactsLoaded(dataContactModel: List<DataContactModel>) {
        recyclerView.adapter = ContactAdapter(dataContactModel, this)
    }

    override fun showSkeleton() {
        skeleton.showSkeleton()
    }

    override fun hideSkeleton() {
        skeleton.showOriginal()
    }

    override fun hideSwipeRefresh() {
        swipeRefresh.isRefreshing = false
    }

    override fun showConnectionError() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,
                ConnectionErrorFragment(presenter), "CONNECTION ERROR")
            .commit()
        fabInsert.visibility = View.INVISIBLE
        appBarLayout.visibility = View.GONE
        swipeRefresh.isEnabled = false
    }

    override fun hideConnectionError() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = supportFragmentManager.findFragmentByTag("CONNECTION ERROR")
        fragment?.let {
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commit()
        }
        fabInsert.visibility = View.VISIBLE
        appBarLayout.visibility = View.VISIBLE
        swipeRefresh.isEnabled = true
    }

    override fun onContactDeleted() {
        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        }
        presenter.loadContacts()
    }

    override fun onSearchSuccess() {
        recyclerView.visibility = View.VISIBLE
        tvNothingToShow.visibility = View.GONE

        fabInsert.visibility = View.INVISIBLE
        swipeRefresh.isEnabled = false
    }

    override fun onSearchFailed() {
        recyclerView.visibility = View.INVISIBLE
        tvNothingToShow.visibility = View.VISIBLE

        fabInsert.visibility = View.INVISIBLE
        swipeRefresh.isEnabled = false
    }

    companion object {
        private const val INTENT_TO_INSERT = 10
        private const val INTENT_TO_EDIT = 20
    }
}