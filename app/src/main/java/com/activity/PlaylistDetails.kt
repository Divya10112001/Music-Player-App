package com.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.activity.databinding.ActivityMainBinding
import com.activity.databinding.ActivityPlaylistDetailsBinding
import com.activity.databinding.AddplaylistBinding
import com.adapter.MusicAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fragment.FavouriteFragment
import com.fragment.HomeFragment
import com.fragment.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.modal.MusicPlaylist
import com.modal.checkData
import java.lang.Exception

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding:ActivityPlaylistDetailsBinding
    lateinit var adapter:MusicAdapter
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicApplication)
        binding= ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarPD)
        supportActionBar?.title = "My Playlists"
        supportActionBar?.setHomeButtonEnabled(true)
        currentPlaylist=intent.extras?.get("index") as Int
        try {
            PlaylistFragment.musicPlaylist.ref[currentPlaylist].playlist =
                checkData(PlaylistFragment.musicPlaylist.ref[currentPlaylist].playlist)
        }catch (e:Exception){}
        binding.playlistRVPD.layoutManager=LinearLayoutManager(this)
        adapter= MusicAdapter(this,PlaylistFragment.musicPlaylist.ref[currentPlaylist].playlist,true)
        binding.playlistRVPD.adapter=adapter
        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","PDetails")
            startActivity(intent)
        }
        binding.addPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removePD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove").setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("YES") { dialog, _ ->
                PlaylistFragment.musicPlaylist.ref[currentPlaylist].playlist.clear()
                    adapter.refreshPlaylist()
                dialog.dismiss()
            }.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.toolbarPD.title=PlaylistFragment.musicPlaylist.ref[currentPlaylist].name
        binding.namePD.text="Total ${adapter.itemCount} Songs.\n\n"+
                "Created On:\n${PlaylistFragment.musicPlaylist.ref[currentPlaylist].createdOn}\n\n"
          if(adapter.itemCount>0){
              Glide.with(this).load( PlaylistFragment.musicPlaylist.ref[currentPlaylist].playlist[0].imguri)
                  .apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(binding.imgPD)
              binding.shuffleBtnPD.visibility=View.VISIBLE
          }
        adapter.notifyDataSetChanged()
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE)?.edit()
        val jsonStringPlaylist= GsonBuilder().create()
            .toJson(PlaylistFragment.musicPlaylist)
        editor?.putString("MusicPlaylist",jsonStringPlaylist)
        editor?.apply()
    }
    companion object{
        var currentPlaylist:Int=-1
    }
}