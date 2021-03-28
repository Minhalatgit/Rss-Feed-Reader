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

        holder.date.text = getFormattedDated(rssFeed.pubDate!!)
        Picasso.get().load(rssFeed.image).into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            // open external browser with item link
            addLimit++
            if (addLimit > 4) {
                addLimit = 0
                Log.d("AddCount", "Add limit value set to $addLimit")
            }
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(rssFeed.link)))
        }
    }

    private fun getFormattedDated(date: String): String {

        val finalDateArr = date.split(" ")
        val finalDateString =
            finalDateArr[0] + " " + finalDateArr[1] + " " + finalDateArr[2] + " " + finalDateArr[3] + " @ " + finalDateArr[4].substring(
                0,
                finalDateArr[4].length - 3
            )
        Log.d("RssFeedAdapter", "getFormattedDated: $finalDateString")

//        val formatIn = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
//        val formatOut = SimpleDateFormat("dd-MMM-yyyy")
//        val finalDateString: String
//        var formattedDate: Date? = null
//        try {
//            formattedDate = formatIn.parse(date)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//        finalDateString = if (formattedDate != null) formatOut.format(formattedDate) else date

        return finalDateString
    }
}