package com.free.grocerycoupons.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.free.grocerycoupons.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentContactUsBinding.inflate(inflater, container, false)



        return binding.root
    }
}