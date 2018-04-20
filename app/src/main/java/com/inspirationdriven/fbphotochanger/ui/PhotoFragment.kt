package com.inspirationdriven.fbphotochanger.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.viewmodel.PhotoViewModel
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoFragment : Fragment() {
    lateinit var viewModel: PhotoViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!)[PhotoViewModel::class.java]
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_photo_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.profilePicUrl.observe(this, Observer {
            Picasso.with(context).load(it).transform(CropCircleTransformation()).into(imgview_photo)
        })
        (activity as AppCompatActivity).supportActionBar?.apply {
            this.title = arguments?.getString(ThumbnailListFragment.ARG_TITLE) ?: ""
        }
    }
}