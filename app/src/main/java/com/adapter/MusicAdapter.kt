package com.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.activity.PlayerActivity
import com.activity.PlayerActivity.Companion.nowPlayingId
import com.activity.PlaylistDetails
import com.activity.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fragment.HomeFragment.Companion.search
import com.fragment.PlaylistFragment
import com.google.android.material.imageview.ShapeableImageView
import com.modal.formatDuration
import com.modal.music

class MusicAdapter(private val context: Context, private var musicList: ArrayList<music>,private val playlistDetails: Boolean=false,private val selection: Boolean=false):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
         val name=view.findViewById<TextView>(R.id.songname)
         val album=view.findViewById<TextView>(R.id.songalbum)
         val duration=view.findViewById<TextView>(R.id.songtime)
         val image=view.findViewById<ShapeableImageView>(R.id.imgview)
         val content=view.findViewById<RelativeLayout>(R.id.singlemusic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.single_list, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
        holder.name.text = musicList[position].name
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context).load(musicList[position].imguri)
            .apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(holder.image)
        when{
            playlistDetails->{
                holder.content.setOnClickListener{
                  sendIntent("PDAdapter", position)
                }
            }
            selection->{
                holder.content.setOnClickListener {
                    if(addSong(musicList[position]))
                        holder.content.setBackgroundColor(ContextCompat.getColor(context,R.color.red))
                    else
                        holder.content.setBackgroundColor(ContextCompat.getColor(context,R.color.white))

                }
            }
            else-> {
                holder.content.setOnClickListener {
                    when {
                        search -> sendIntent("MusicAdapterSearch", position)
                        musicList[position].id == nowPlayingId ->
                            sendIntent("NowPlaying", PlayerActivity.songPosition)
                        else -> sendIntent("MusicAdapter", position)
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int {
       return musicList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList:ArrayList<music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref:String,pos:Int){
        val intent=Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    private fun addSong(song:music):Boolean{
        PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist.forEachIndexed { index, music ->
            if(song.id== music.id){
                PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist.removeAt(index)
                return false
            }
        }
        PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList= ArrayList()
        musicList=PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist
        notifyDataSetChanged()
    }

}



