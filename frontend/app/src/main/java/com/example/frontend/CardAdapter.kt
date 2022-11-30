package com.example.frontend

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.databinding.TrashCardViewBinding
import com.example.frontend.place.Place
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CardAdapter(private val place: ArrayList<Place>): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private lateinit var listener: ItemListener

    // Provides reference to our type of views, aka our components inside the bingo card view
    class ViewHolder(binding: TrashCardViewBinding): RecyclerView.ViewHolder(binding.root) {
        val tv_Number1: TextView = binding.cardName
    }

    // Creates the new recycleview item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrashCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replaces the contents of the recycleview item with what we want it to hold
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = place[position]
        holder.tv_Number1.text = place.name.toString()
    }
    // Necessary to override
    override fun getItemCount(): Int {
        return place.size
    }

    // Creates an interface that listened when an item is clicked
    interface ItemListener {
//        fun onItemClicked(bingoNumber: BingoNumber, position: Int)
    }

    // setter in order to gain access to the listener
    fun setListener(listener: ItemListener) {
        this.listener = listener;
    }
}