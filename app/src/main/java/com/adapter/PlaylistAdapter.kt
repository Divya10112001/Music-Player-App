package com.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.activity.PlayerActivity
import com.activity.PlaylistDetails
import com.activity.R
import com.activity.databinding.PlaylistSingleViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fragment.PlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.modal.Playlist
import com.modal.music

class PlaylistAdapter(private val context: Context, private var musicList: ArrayList<Playlist>):
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    class ViewHolder(binding: PlaylistSingleViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val img=binding.playlistimg
        val name=binding.playlistname
        val root=binding.root
        val delete=binding.playlistdelete

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlaylistSingleViewBinding.inflate( LayoutInflater.from(context), parent, false))
    }
    override fun getItemCount(): Int {
        return musicList.size
    }
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text= musicList[position].name
        holder.name.isSelected=true
        Playlist().playlist=ArrayList()
        holder.delete.setOnClickListener {
            val builder= MaterialAlertDialogBuilder(context)
            builder.setTitle(musicList[position].name).setMessage("Do you want to delete playlist?").setPositiveButton("Yes"){dialog,_->
                PlaylistFragment.musicPlaylist.ref.removeAt(position)
                refresh()
                dialog.dismiss()
            }.setNegativeButton("No"){dialog,_->
            }
            val customDialog=builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.red)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.red)}
          holder.root.setOnClickListener {
           val intent=Intent(context,PlaylistDetails::class.java)
              intent.putExtra("index",position)
              ContextCompat.startActivity(context,intent,null)
          }
        if(PlaylistFragment.musicPlaylist.ref[position].playlist.size>0){
            Glide.with(context).load( PlaylistFragment.musicPlaylist.ref[position].playlist[0].imguri)
                .apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(holder.img)

        }
    }
        //Glide.with(context).load(musicList[position].imguri).apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(holder.img)
        @SuppressLint("NotifyDataSetChanged")
        fun refresh(){
        musicList= ArrayList()
        musicList.addAll(PlaylistFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}