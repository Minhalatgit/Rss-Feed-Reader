package com.koders.rssfeed

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prof.rssparser.Article
import com.squareup.picasso.Picasso

class RssFeedAdapter(private val context: Context, private val rssFeedList: MutableList<Article>) :
    RecyclerView.Adapter<RssFeedAdapter.RssFeedHolder>() {

    inner class RssFeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RssFeedHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rss_feed_item, parent, false)
        )

    override fun getItemCount() = rssFeedList.size

    override fun onBindViewHolder(holder: RssFeedHolder, position: Int) {
        val rssFeed = rssFeedList[position]

        holder.date.text = rssFeed.pubDate
        Picasso.get().load(rssFeed.image).into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            // open external browser with item link
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(rssFeed.link)))
        }
    }
}