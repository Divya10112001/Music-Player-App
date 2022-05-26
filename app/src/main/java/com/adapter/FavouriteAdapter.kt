package com.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.activity.PlayerActivity
import com.activity.R
import com.activity.databinding.FavouriteSingleViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.modal.music
class FavouriteAdapter(private val context: Context, private var musicList: ArrayList<music>):
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {
    class ViewHolder(binding: FavouriteSingleViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val img=binding.favimg
        val name=binding.favname
        val root= binding.root
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FavouriteSingleViewBinding.inflate( LayoutInflater.from(context), parent, false))
    }
    override fun getItemCount(): Int {
        return musicList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text=musicList[position].name
        Glide.with(context).load(musicList[position].imguri).apply(
            RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(holder.img)
        holder.root.setOnClickListener{
            val intent= Intent(context, PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","Favourites")
            ContextCompat.startActivity(context,intent,null)
        }
    }
}