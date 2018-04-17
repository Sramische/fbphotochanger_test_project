package com.inspirationdriven.fbphotochanger.ui


import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.model.Thumbnail
import kotlinx.android.synthetic.main.fragment_album_list.view.*

abstract class ThumbnailListFragment<T : Thumbnail> : Fragment() {
    @get:LayoutRes
    abstract val itemLayout: Int

    protected abstract fun setupList(recyclerView: RecyclerView)
    protected abstract fun onClick(obj: T)

    protected lateinit var thumbnailAdapter: ThumbnailRecyclerViewAdapter<T>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.apply {
            this.title = arguments?.getString(ARG_TITLE) ?: ""
            this.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_album_list, container, false)
        thumbnailAdapter = ThumbnailRecyclerViewAdapter(itemLayout, this::onClick)

        with(view.list as RecyclerView) {
            itemAnimator = DefaultItemAnimator()
            setupList(this)
            adapter = thumbnailAdapter
        }
        return view
    }

    companion object {
        const val ARG_TITLE = "title"
    }
}
