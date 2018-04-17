package com.inspirationdriven.fbphotochanger.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.model.LargeImage
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import com.inspirationdriven.fbphotochanger.viewmodel.ImageListViewModel
import com.inspirationdriven.fbphotochanger.viewmodel.PhotoViewModel

class AlbumImagesFragment : ThumbnailListFragment<LargeImage>(){
    override val itemLayout = R.layout.grid_entity_album_image

    override fun setupList(recyclerView: RecyclerView) {
       recyclerView.layoutManager = GridLayoutManager(context, 3)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ViewModelProviders.of(activity!!)[ImageListViewModel::class.java]){
            getCollection(arguments!!.getSerializable(ARG_ALBUM) as AlbumMeta).observe(this@AlbumImagesFragment, Observer { thumbnailAdapter.thumbnailsList = it ?: listOf() })
        }
    }

    override fun onClick(obj: LargeImage) {
        with(ViewModelProviders.of(activity!!)[PhotoViewModel::class.java]){
            setProfilePic(obj.bigUrl)
        }
        activity!!.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        const val ARG_ALBUM = "album"

        fun newInstance(a: Album) = AlbumImagesFragment().apply {
            arguments = Bundle().apply {
                this.putSerializable(ARG_ALBUM, a.meta)
                this.putString(ARG_TITLE, a.meta.name)
            }
        }
    }
}