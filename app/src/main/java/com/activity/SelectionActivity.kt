package com.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.activity.databinding.ActivitySelectionBinding
import com.adapter.MusicAdapter
import com.fragment.HomeFragment
import com.fragment.PlaylistFragment

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicApplication)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(
            this,
           HomeFragment.MusicListMA,false,true )
        binding.selectionRV.adapter = adapter
         binding.searchviewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
             androidx.appcompat.widget.SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(p0: String?): Boolean =true
             override fun onQueryTextChange(p0: String?): Boolean {
                 HomeFragment.musicListSearch = ArrayList()
                 if (p0 != null) {
                     val userInput = p0.lowercase()
                     for (song in HomeFragment.MusicListMA) {
                         if (song.name.lowercase().contains(userInput))
                             HomeFragment.musicListSearch.add(song)
                         HomeFragment.search = true
                         adapter.updateMusicList(HomeFragment.musicListSearch)
                     }
                 }
                 return true
             }
         })
    }

}