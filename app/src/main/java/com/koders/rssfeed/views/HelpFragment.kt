package com.koders.rssfeed.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.koders.rssfeed.QuestionAnswer
import com.koders.rssfeed.R
import com.koders.rssfeed.databinding.FragmentHelpBinding
import org.json.JSONArray
import org.json.JSONObject

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpBinding.inflate(inflater, container, false)

        val database = FirebaseDatabase.getInstance()
        database.reference.child("help").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("HelpFragment", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val helpObject = JSONObject(snapshot.value as Map<*, *>)
                Log.d("HelpFragment", helpObject.toString())

                val faqList = ArrayList<QuestionAnswer>()

                val x: Iterator<*> = helpObject.keys()
                val jsonArray = JSONArray()

                while (x.hasNext()) {
                    val key = x.next() as String
                    jsonArray.put(helpObject.get(key))
                }

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    faqList.add(
                        QuestionAnswer(
                            item.getString("question"),
                            item.getString("answer")
                        )
                    )
                }
                Log.d("HelpFragment", faqList.toString())
            }
        })

//        binding.expand.text = requireContext().resources.getString(R.string.dummy_text)

        return binding.root
    }
}