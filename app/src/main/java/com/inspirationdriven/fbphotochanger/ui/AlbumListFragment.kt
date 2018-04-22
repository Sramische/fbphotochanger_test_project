package com.inspirationdriven.fbphotochanger.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.viewmodel.AlbumsViewModel

class AlbumListFragment : ThumbnailListFragment<Album>() {
    override val itemLayout = R.layout.list_entity_album

    override fun onClick(obj: Album) {
        (activity!! as MainActivity).showAlbumImages(obj)
    }

    override fun setupList(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(ViewModelProviders.of(activity!!)[AlbumsViewModel::class.java]) {
            albums.observe(this@AlbumListFragment, Observer { thumbnailAdapter.thumbnailsList = it ?: listOf() })
            loading.observe(this@AlbumListFragment, Observer { showLoading(it ?: false) })
            fetchAlbums()
        }
    }
}