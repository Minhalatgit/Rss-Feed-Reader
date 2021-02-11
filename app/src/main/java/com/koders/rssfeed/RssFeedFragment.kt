package com.koders.rssfeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koders.rssfeed.databinding.FragmentRssFeedBinding
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RssFeedFragment"

class RssFeedFragment : Fragment() {

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var rssFeedAdapter: RssFeedAdapter
    private var rssFeedList = ArrayList<Article>()

    private lateinit var parser: Parser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRssFeedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        feedRecyclerView = binding.feedRecyclerView
        rssFeedAdapter = RssFeedAdapter(rssFeedList)
        feedRecyclerView.layoutManager = LinearLayoutManager(context)
        feedRecyclerView.adapter = rssFeedAdapter

        parser = Parser.Builder()
            .context(requireActivity())
            // If you want to provide a custom charset (the default is utf-8):
            // .charset(Charset.forName("ISO-8859-7"))
            .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
            .build()

        getFeed()

        return binding.root
    }

    private fun getFeed() {
        GlobalScope.launch {
            val channel =
                parser.getChannel("https://tools.shophermedia.net/gc-rss.asp?cp=2&afid=357373")
            Log.d(TAG, "onCreate: ${channel.articles}")
            rssFeedList.addAll(channel.articles)
            withContext(Dispatchers.Main) {
                rssFeedAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
        if (null == getShareIntent().resolveActivity(requireActivity().packageManager)) {
            menu.findItem(R.id.shareApp)?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reload -> getFeed()
            R.id.shareApp -> startActivity(getShareIntent())
            R.id.notification -> Toast.makeText(activity, "Notification", Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getShareIntent(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, "Install the RSS feed app and check your feed")
        return shareIntent
    }
}