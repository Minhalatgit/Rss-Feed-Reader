package com.koders.rssfeed.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.koders.rssfeed.FaqAdapter
import com.koders.rssfeed.QuestionAnswer
import com.koders.rssfeed.databinding.FragmentHelpBinding
import org.json.JSONArray
import org.json.JSONObject

class HelpFragment : Fragment() {

    lateinit var faqListRv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHelpBinding.inflate(inflater, container, false)

        faqListRv = binding.faqList

        faqListRv.layoutManager = LinearLayoutManager(activity)

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
                faqList.reverse()

                faqListRv.adapter = FaqAdapter(faqList)
                Log.d("HelpFragment", faqList.toString())
            }
        })

        return binding.root
    }
}