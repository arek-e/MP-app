package com.example.frontend

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.databinding.TrashCardItemViewBinding
import com.example.frontend.place.Place
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.ViewCompat.setAlpha
import com.example.frontend.place.Wastetypes

class TrashCardAdapter(private val binPlaces: ArrayList<Place>): RecyclerView.Adapter<TrashCardAdapter.ViewHolder>() {
    private lateinit var listener: ItemListener

    // Provides reference to our type of views, aka our components inside the bingo card view
    class ViewHolder(binding: TrashCardItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        val tv_location: TextView = binding.locationTextView
        val tv_distance: TextView = binding.distanceTextView
        val tv_info: TextView = binding.infoTextView
        val tv_status: TextView = binding.statusTextView
        val ifw_organic: ImageFilterView = binding.imageFilterViewOrganic
        val ifw_metal: ImageFilterView = binding.imageFilterViewMetal
        val ifw_liquid: ImageFilterView = binding.imageFilterViewLiquid
        val ifw_glass: ImageFilterView = binding.imageFilterViewGlass
        val ifw_paper: ImageFilterView = binding.imageFilterViewPaper
        val ifw_plastic: ImageFilterView = binding.imageFilterViewPlastic
    }

    // Creates the new recycleview item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrashCardItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Replaces the contents of the recycleview item with what we want it to hold
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trashBinPlace = binPlaces[position]
        holder.tv_location.text = trashBinPlace.address
        if (trashBinPlace.wastetypes.liquid){
            holder.ifw_liquid.setAlpha(80)
        }

        if (trashBinPlace.wastetypes.metal){
            holder.ifw_metal.setAlpha(80)
        }

        if (trashBinPlace.wastetypes.organic){
            holder.ifw_organic.setAlpha(80)
        }

        if (trashBinPlace.wastetypes.glass){
            holder.ifw_glass.setAlpha(80)
        }

        if (trashBinPlace.wastetypes.paper){
            holder.ifw_paper.setAlpha(80)
        }

        if (trashBinPlace.wastetypes.plastic){
            holder.ifw_plastic.setAlpha(80)
        }


    }
    // Necessary to override
    override fun getItemCount(): Int {
        return binPlaces.size
    }

    // Creates an interface that listened when an item is clicked
    interface ItemListener {
        fun onItemClicked(place: Place, position: Int)
    }

    // setter in order to gain access to the listener
    fun setListener(listener: ItemListener) {
        this.listener = listener;
    }
}