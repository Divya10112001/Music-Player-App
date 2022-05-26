package com.modal

import android.media.MediaMetadataRetriever
import com.activity.PlayerActivity
import com.fragment.FavouriteFragment
import java.io.File
import java.util.concurrent.TimeUnit

data class music (
    val id:String,
    val name:String,
    val album:String,
    val artist:String,
    val duration:Long=0,
    val path:String,
    val imguri:String
        )

fun formatDuration(d:Long):String{
    val min=TimeUnit.MINUTES.convert(d,TimeUnit.MILLISECONDS)
    val sec=(TimeUnit.SECONDS.convert(d,TimeUnit.MILLISECONDS)-min*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",min,sec)
}
fun checkFavourite(id:String):Int{
    PlayerActivity.isFav=false
    FavouriteFragment.favList.forEachIndexed{index, music ->
    if (id==music.id){
        PlayerActivity.isFav=true
        return index
         }
    }
  return -1
}
fun getImageArt(path:String):ByteArray?{
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
fun checkData(playlist:ArrayList<music>):ArrayList<music>{
    playlist.forEachIndexed { index, music ->
        val file=File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist

}
class Playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<music>
    lateinit var createdOn:String
}
class MusicPlaylist {
    var ref:ArrayList<Playlist> = ArrayList()
}