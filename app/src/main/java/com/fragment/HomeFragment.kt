package com.fragment

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activity.PlayerActivity
import com.activity.R
import com.adapter.MusicAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.modal.music
import java.io.File
import android.net.Uri
import android.view.*
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.activity.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.modal.MusicPlaylist
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private var searchView:SearchView?=null
    private lateinit var queryTextListener:SearchView.OnQueryTextListener
    lateinit var musicAdapter: MusicAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        //requireContext().theme.applyStyle(MainActivity.cuurrentTheme[MainActivity.themeIndex],true)
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val btn=view.findViewById<ExtendedFloatingActionButton>(R.id.shuffle)
        val recyclerHome=view.findViewById<RecyclerView>(R.id.musicRV)
        val layoutManager=LinearLayoutManager(activity)
        val total=view.findViewById<TextView>(R.id.txt)
        search=false
        val sortEditor=activity?.getSharedPreferences("SORTING",AppCompatActivity.MODE_PRIVATE)
        sortOrder=sortEditor!!.getInt("sortOrder",0)
        MusicListMA=getAudio()
        musicAdapter =
            MusicAdapter(activity as Context, MusicListMA)
        recyclerHome.adapter = musicAdapter
        recyclerHome.layoutManager = layoutManager
        total.text="Total Songs : " + musicAdapter.itemCount
        FavouriteFragment.favList= ArrayList()
            val editor=activity?.getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE)
            val jsonString= editor?.getString("FavouriteSongs",null)
            val typeToken=object: TypeToken<ArrayList<music>>(){}.type
        if(jsonString!=null){
            val data :ArrayList<music> = GsonBuilder().create().fromJson(jsonString,typeToken)
            FavouriteFragment.favList.addAll(data)
        }
        PlaylistFragment.musicPlaylist= MusicPlaylist()
        val jsonStringPlaylist= editor?.getString("MusicPlaylist",null)
        if(jsonStringPlaylist!=null){
            val dataPlaylist :MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,
                MusicPlaylist::class.java)
            PlaylistFragment.musicPlaylist=dataPlaylist
        }
        btn.setOnClickListener {
               val intent =Intent(activity as Context,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
               startActivity(intent)
           }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sorting,menu)
        val searchItem=menu.findItem(R.id.searchView)
        val searchManager=activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if(searchItem!=null){
            searchView=searchItem.actionView as SearchView
        }
        if(searchView!=null){
            searchView!!.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryTextListener=object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean=true
                override fun onQueryTextChange(p0: String?): Boolean {
                    musicListSearch=ArrayList()
                    if(p0!=null) {
                        val userInput = p0.lowercase()
                        for (song in MusicListMA) {
                            if (song.name.lowercase().contains(userInput))
                                musicListSearch.add(song)
                            search = true
                            musicAdapter.updateMusicList(musicListSearch)
                        }
                    }
                    return true
                }

            }
            searchView?.setOnQueryTextListener(queryTextListener)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item.itemId==R.id.searchView)
           return false
        searchView?.setOnQueryTextListener(queryTextListener)
        if(item.itemId == R.id.sorting){
            val menuList= arrayOf("By Date","By Name")
            var currentSort=sortOrder
            val builder = MaterialAlertDialogBuilder(activity as Context)
            builder.setTitle("Sorting")
                .setPositiveButton("OK") { _, _ ->
                    val editor=activity?.getSharedPreferences("SORTING", AppCompatActivity.MODE_PRIVATE)?.edit()
                    editor?.putInt("sortOrder",currentSort)
                    editor?.apply()
                }
                .setSingleChoiceItems(menuList,currentSort){_,which->
                    currentSort=which
                }
            val custom=builder.create()
            custom.show()
            custom.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
        return super.onOptionsItemSelected(item)

    }
    @SuppressLint("Range")
     fun getAudio():ArrayList<music>{
        val list= ArrayList<music>()
        val selection=MediaStore.Audio.Media.IS_MUSIC +"!=0"
        val projection= arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor=activity?.applicationContext?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,
            selection,null,
            sortingList[sortOrder],null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do {
                    val nameC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val durationC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumIdC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri= Uri.parse("content://media/external/audio/albumart")
                    val imgC=Uri.withAppendedPath(uri,albumIdC).toString()
                    val Music=music(idC,nameC,albumC,artistC,durationC,pathC,imgC)
                    val file=File(Music.path)
                    if(file.exists())
                        list.add(Music)
                }while (cursor.moveToNext())
            }
            cursor.close()
        }
        return list

    }

    override fun onResume() {
        super.onResume()
        val editor=activity?.getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE)?.edit()
        val jsonString= GsonBuilder().create()
            .toJson(FavouriteFragment.favList)
        editor?.putString("FavouriteSongs",jsonString)
        val jsonStringPlaylist= GsonBuilder().create()
            .toJson(PlaylistFragment.musicPlaylist)
        editor?.putString("MusicPlaylist",jsonStringPlaylist)
        editor?.apply()
        val sortEditor=activity?.getSharedPreferences("SORTING",AppCompatActivity.MODE_PRIVATE)
        val sortvalue=sortEditor!!.getInt("sortOrder",0)
        if (sortOrder!=sortvalue){
            sortOrder=sortvalue
            MusicListMA=getAudio()
            musicAdapter.updateMusicList(MusicListMA)
        }

    }
    companion object{
        lateinit var MusicListMA:ArrayList<music>
        lateinit var musicListSearch:ArrayList<music>
        var search:Boolean=false
        var sortOrder:Int=0
        val sortingList= arrayOf(MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.TITLE)
    }
}