package com.inspirationdriven.fbphotochanger.ui

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inspirationdriven.fbphotochanger.BR
import com.inspirationdriven.fbphotochanger.model.Thumbnail

class ThumbnailRecyclerViewAdapter<T: Thumbnail>(@LayoutRes val layout: Int, clickCallback: (T) -> Unit = {})
    : RecyclerView.Adapter<ThumbnailRecyclerViewAdapter<T>.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    var thumbnailsList = listOf<T>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as T
            clickCallback.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = thumbnailsList[position]

        holder.bind(item)

        with(holder.itemView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = thumbnailsList.size

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: Any) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }
    }
}
