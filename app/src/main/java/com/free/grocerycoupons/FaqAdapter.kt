package com.free.grocerycoupons

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FaqAdapter(
    private val list: ArrayList<QuestionAnswer>
) :
    RecyclerView.Adapter<FaqAdapter.FaqHolder>() {

    inner class FaqHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.question)
        val answer: TextView = itemView.findViewById(R.id.answer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FaqHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.faq_item, parent, false)
    )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: FaqHolder, position: Int) {
        val faqItem = list[position]
        holder.question.text = faqItem.question
        holder.answer.text = faqItem.answer

        holder.itemView.setOnClickListener {
            if (holder.answer.visibility == View.VISIBLE) {
                holder.answer.animate()
                    .translationY(0.0f)
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            holder.answer.visibility = View.GONE
                        }
                    })
            } else {
                holder.answer.animate()
                    .translationY(1.0f)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            holder.answer.visibility = View.VISIBLE
                        }
                    })
            }
        }
    }
}