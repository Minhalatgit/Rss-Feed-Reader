package com.free.printable.coupons

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FaqAdapter(
    private val list: ArrayList<QuestionAnswer>
) :
    RecyclerView.Adapter<FaqAdapter.FaqHolder>() {

    inner class FaqHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.question)
        val answer: TextView = itemView.findViewById(R.id.answer)
        val arrow: ImageView = itemView.findViewById(R.id.arrowIcon)
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
                            holder.arrow.setImageResource(R.drawable.arrow_down_icon)
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
                            holder.arrow.setImageResource(R.drawable.arrow_up_icon)
                            holder.answer.visibility = View.VISIBLE
                        }
                    })
            }
        }
    }
}