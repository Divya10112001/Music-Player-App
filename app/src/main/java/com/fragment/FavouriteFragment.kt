package com.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activity.MainActivity
import com.activity.PlayerActivity
import com.activity.R
import com.activity.databinding.FragmentFavouriteBinding
import com.adapter.FavouriteAdapter
import com.adapter.MusicAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.modal.checkData
import com.modal.music

class FavouriteFragment : Fragment() {
    lateinit var favAdapter: FavouriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //requireContext().theme.applyStyle(MainActivity.cuurrentTheme[MainActivity.themeIndex],true)
        val view= inflater.inflate(R.layout.fragment_favourite, container, false)
        val recycler=view.findViewById<RecyclerView>(R.id.favouriteRV)
        val shuffleBtn=view.findViewById<ExtendedFloatingActionButton>(R.id.favshufflebtn)
        val layoutManager=GridLayoutManager(activity,2)
        favList= checkData(favList)
        favAdapter =
            FavouriteAdapter(activity as Context, favList)
        recycler.adapter = favAdapter
        recycler.layoutManager = layoutManager
        if(favList.size<1) shuffleBtn.visibility=View.INVISIBLE
        shuffleBtn.setOnClickListener {
            val intent = Intent(activity as Context, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","FavouriteFragment")
            startActivity(intent)
        }
        return view
    }
    companion object{
        var favList:ArrayList<music> = ArrayList()
    }
}