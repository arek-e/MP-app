package com.example.frontend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frontend.databinding.ActivityMainBinding
import com.example.frontend.place.Place
import com.example.frontend.place.PlacesReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TrashCardAdapter.ItemListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var mapFragment = MapsFragment()
        val fragManager = supportFragmentManager
        fragManager.commit{
            add(binding.mapsFragFrame.id, mapFragment)
        }

        val trashCardAdapter = TrashCardAdapter(places as ArrayList<Place>)
        binding.trashRecyclerView.adapter = trashCardAdapter
        binding.trashRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        trashCardAdapter.setListener(this@MainActivity)

    }

    // Method is called when the listener is triggered from the recycle view item
    override fun onItemClicked(place: Place, position: Int) {
        // Run code when optional item inside the card is clicked
    }
}