package com.free.grocerycoupons.views

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.free.grocerycoupons.*
import com.free.grocerycoupons.databinding.FragmentRssFeedBinding

class RssFeedFragment : Fragment(), RssFeedAdapter.ItemClickListener {

    private lateinit var binding: FragmentRssFeedBinding
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var rssFeedAdapter: RssFeedAdapter

    private lateinit var rssFeedList: List<com.free.grocerycoupons.network.Article>

    private lateinit var viewModel: RssFeedViewModel

    private var isOutside: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RssFeedViewModel::class.java)
        viewModel.getFeed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRssFeedBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        feedRecyclerView = binding.feedRecyclerView

        rssFeedList = ArrayList()

        FirebaseDatabase.getInstance().reference.child("outside")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("RssFeedFragment", "Firebase database failed ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("RssFeedFragment", "onDataChange: $snapshot")
                    isOutside = snapshot.value as Boolean
                }

            })

        val orientation = activity?.resources?.configuration?.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            Log.d("ResFeedFragment", "onCreateView: Portrait ")
            feedRecyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            // code for landscape mode
            Log.d("ResFeedFragment", "onCreateView: Landscape")
            feedRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        }

        binding.progress.visibility = View.VISIBLE
        viewModel.rssFeedList.observe(viewLifecycleOwner, Observer {
            (rssFeedList as ArrayList<com.free.grocerycoupons.network.Article>).clear()
            rssFeedList = it
            binding.progress.visibility = View.GONE
            rssFeedAdapter = RssFeedAdapter(requireActivity(), rssFeedList, this)
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
            R.id.reload -> {
                addLimit++
                if (addLimit > 4) {
                    showAds()
                }

                (rssFeedList as ArrayList<com.free.grocerycoupons.network.Article>).clear()
                rssFeedAdapter.notifyDataSetChanged()
                binding.progress.visibility = View.VISIBLE
                viewModel.getFeed()
            }
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("ResFeedFragment", "onConfigurationChanged: Portrait")
            feedRecyclerView.layoutManager = LinearLayoutManager(context)
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("ResFeedFragment", "onConfigurationChanged: Landscape")
            feedRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        }
    }

    override fun onItemClick(position: Int) {
        // open external browser with item link
        addLimit++
        if (addLimit > 4) {
            showAds()
        }
        if (rssFeedList.isNotEmpty()) {
            //check for firebase toggle whether to open inside or outside the app
            if (isOutside) {
                //open in web view inside app
                findNavController().navigate(
                    RssFeedFragmentDirections.actionRssFeedFragmentToWebViewFragment(
                        rssFeedList[position].link ?: "www.google.com"
                    )
                )
            } else {
                requireContext().startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(rssFeedList[position].link)
                    )
                )
            }
        }
    }

    private fun showAds() {
        (activity as MainActivity).initInterstitialAds()
        Log.d("AddCount", "Add limit value set to $addLimit")
        addLimit = 0
    }
}