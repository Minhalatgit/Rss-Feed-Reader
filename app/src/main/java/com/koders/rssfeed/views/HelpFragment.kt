package com.koders.rssfeed.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import at.blogc.android.views.ExpandableTextView
import com.koders.rssfeed.R
import com.koders.rssfeed.databinding.FragmentHelpBinding

class HelpFragment : Fragment() {

    private lateinit var expandableTextView: ExpandableTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpBinding.inflate(inflater, container, false)

        expandableTextView = binding.expandableTextView

        expandableTextView.setInterpolator(OvershootInterpolator())

        expandableTextView.setOnClickListener {
            expandableTextView.toggle()
        }

        return binding.root
    }
}