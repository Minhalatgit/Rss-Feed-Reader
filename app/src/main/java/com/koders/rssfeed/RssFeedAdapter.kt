package com.koders.rssfeed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RssFeedAdapter(private val rssFeedList: ArrayList<RssFeed>) :
    RecyclerView.Adapter<RssFeedAdapter.RssFeedHolder>() {

    inner class RssFeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RssFeedHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rss_feed_item, parent, false)
        )

    override fun getItemCount() = rssFeedList.size

    override fun onBindViewHolder(holder: RssFeedHolder, position: Int) {
        val rssFeed = rssFeedList[position]

        holder.title.text = rssFeed.title
        holder.thumbnail.setImageResource(rssFeed.thumbnail)

        holder.itemView.setOnClickListener {
            Log.d("RssFeedAdapter", "Item $position clicked")
        }
    }
}