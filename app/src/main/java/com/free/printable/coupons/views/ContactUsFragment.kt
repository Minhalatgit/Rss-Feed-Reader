package com.free.printable.coupons.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.free.printable.coupons.databinding.FragmentContactUsBinding

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentContactUsBinding.inflate(inflater, container, false)



        return binding.root
    }
}