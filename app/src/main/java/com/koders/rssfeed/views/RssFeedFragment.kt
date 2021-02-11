package com.koders.rssfeed.views

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koders.rssfeed.R
import com.koders.rssfeed.RssFeedAdapter
import com.koders.rssfeed.RssFeedViewModel
import com.koders.rssfeed.databinding.FragmentRssFeedBinding
import com.prof.rssparser.Article

class RssFeedFragment : Fragment() {

    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var rssFeedAdapter: RssFeedAdapter
    private var rssFeedList = ArrayList<Article>()

    private lateinit var viewModel: RssFeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRssFeedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        feedRecyclerView = binding.feedRecyclerView
        feedRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel = ViewModelProvider(this).get(RssFeedViewModel::class.java)
        viewModel.getFeed()

        viewModel.rssFeedList.observe(viewLifecycleOwner, Observer {
            rssFeedAdapter = RssFeedAdapter(it)
            feedRecyclerView.adapter = rssFeedAdapter
        })

        return binding.root
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
            R.id.reload -> viewModel.getFeed()
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