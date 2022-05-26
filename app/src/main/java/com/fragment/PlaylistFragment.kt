package com.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activity.R
import com.activity.databinding.ActivityMainBinding
import com.activity.databinding.AddplaylistBinding
import com.adapter.PlaylistAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.modal.MusicPlaylist
import com.modal.Playlist
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistFragment : Fragment() {
   lateinit var musicAdapter:PlaylistAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //requireContext().theme.applyStyle(MainActivity.cuurrentTheme[MainActivity.themeIndex],true)
        val view= inflater.inflate(R.layout.fragment_playlist, container, false)
        val recyclerPF=view.findViewById<RecyclerView>(R.id.playlistRV)
        val layoutManager=GridLayoutManager(activity,2)
        val addbtn=view.findViewById<ExtendedFloatingActionButton>(R.id.addBtn)
        musicAdapter =
            PlaylistAdapter(activity as Context, musicPlaylist.ref )
        recyclerPF.adapter = musicAdapter
        recyclerPF.layoutManager = layoutManager
        addbtn.setOnClickListener {
            custom_playlist()
        }
        return view
    }
    fun custom_playlist() {
        val customdialog = LayoutInflater.from(activity as Context)
            .inflate(R.layout.addplaylist, ActivityMainBinding.inflate(layoutInflater).root, false)
        val binder=AddplaylistBinding.bind(customdialog)
        val builder = MaterialAlertDialogBuilder(activity as Context)
        builder.setView(customdialog).setTitle("Playlist").setPositiveButton("ADD") { dialog, _ ->
            val playlistname=binder.addplaylistname.text
            if (playlistname!=null){
                if(playlistname.isNotEmpty()){
                    addPlaylist(playlistname.toString())
                }
            }
            dialog.dismiss()
        }.show()
    }
    private fun addPlaylist(name:String){
        var playlistexist=false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistexist=true
                break
            }
        }
        if   (playlistexist) Toast.makeText(activity as Context, "Playlist Exists!!", Toast.LENGTH_SHORT).show()
        else{
            val tempPlaylist=Playlist()
            tempPlaylist.name=name
            tempPlaylist.playlist= ArrayList()
            val calendar=Calendar.getInstance().time
            val sdf= SimpleDateFormat("dd/mm/yy",Locale.ENGLISH)
            tempPlaylist.createdOn=sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            musicAdapter.refresh()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        musicAdapter.notifyDataSetChanged()
    }
    companion object{
         var musicPlaylist:MusicPlaylist= MusicPlaylist()
    }
}