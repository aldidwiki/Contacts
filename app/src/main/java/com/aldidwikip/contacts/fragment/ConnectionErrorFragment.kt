package com.aldidwikip.contacts.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.aldidwikip.contacts.R
import com.aldidwikip.contacts.presenter.MainPresenter
import com.labo.kaji.fragmentanimations.PushPullAnimation
import kotlinx.android.synthetic.main.connection_error.*

class ConnectionErrorFragment(private val mPresenter: MainPresenter) : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.connection_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRetry.setOnClickListener {
            btnRetry.startAnimation()
            mPresenter.loadContacts()
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return PushPullAnimation.create(PushPullAnimation.DOWN, enter, 500L)
    }

    override fun onDestroy() {
        btnRetry?.dispose()
        super.onDestroy()
    }
}