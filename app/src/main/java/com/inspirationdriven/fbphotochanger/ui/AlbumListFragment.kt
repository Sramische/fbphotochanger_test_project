package com.inspirationdriven.fbphotochanger.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.databinding.FragmentAlbumListBinding
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.viewmodel.AlbumsViewModel

class AlbumListFragment : ThumbnailListFragment<Album>() {
    override val itemLayout = R.layout.list_entity_album
    lateinit var vm: AlbumsViewModel

    override fun onClick(obj: Album) {
        (activity!! as MainActivity).showAlbumImages(obj)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(activity!!)[AlbumsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        DataBindingUtil.getBinding<FragmentAlbumListBinding>(view)?.viewModel = vm
        return view
    }

    override fun setupList(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(vm) {
            albums.observe(this@AlbumListFragment, Observer { thumbnailAdapter.thumbnailsList = it ?: listOf() })
            fetchAlbums()
        }
    }
}