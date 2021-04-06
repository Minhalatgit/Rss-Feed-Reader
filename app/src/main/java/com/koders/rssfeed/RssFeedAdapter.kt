package com.koders.rssfeed

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prof.rssparser.Article
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RssFeedAdapter(
    private val context: Context,
    private val rssFeedList: List<com.koders.rssfeed.network.Article>,
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<RssFeedAdapter.RssFeedHolder>() {

    inner class RssFeedHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val date: TextView = itemView.findViewById(R.id.date)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val progress: ProgressBar = itemView.findViewById(R.id.progressBar)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RssFeedHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rss_feed_item, parent, false)
        )

    override fun getItemCount() = rssFeedList.size

    override fun onBindViewHolder(holder: RssFeedHolder, position: Int) {
        val rssFeed = rssFeedList[position]

        holder.date.text = getFormattedDated(rssFeed.pubDate ?: "")
        holder.progress.visibility = View.VISIBLE
        if (rssFeed.image != "") {
            Picasso.get().load(rssFeed.image).into(holder.thumbnail, object : Callback {
                override fun onSuccess() {
                    holder.progress.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    holder.progress.visibility = View.GONE
                }

            })
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    private fun getFormattedDated(date: String): String {

        if (!date.equals("", true)) {
            val formatIn = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
            val formatOut = SimpleDateFormat("EEE, dd MMM yyyy @ HH:mm a")
            val finalDateString: String
            var formattedDate: Date? = null
            try {
                formattedDate = formatIn.parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            finalDateString = if (formattedDate != null) formatOut.format(formattedDate) else date

            Log.d("RssFeedAdapter", "getFormattedDated: $finalDateString")
            return finalDateString
        } else {
            return ""
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}