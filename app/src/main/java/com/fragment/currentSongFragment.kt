package com.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.activity.MainActivity
import com.activity.PlayerActivity
import com.activity.R
import com.activity.databinding.FragmentCurrentSongBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class currentSongFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_current_song, container, false)
        binding= FragmentCurrentSongBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playpauseBtnNP.setOnClickListener {
            if(PlayerActivity.isPlay)     pause()
            else                          play()
        }
        binding.nextBtnNP.setOnClickListener {
            PlayerActivity().setposition(true)
            PlayerActivity().createPlayer()
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].imguri).apply(
                RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(binding.imgNP)
        binding.nameNP.text=PlayerActivity.musicListPA[PlayerActivity.songPosition].name
            play()
        }
        binding.root.setOnClickListener{
            val intent= Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(PlayerActivity.player!= null){
            binding.root.visibility=View.VISIBLE
            binding.nameNP.isSelected=true
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].imguri).apply(
                RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(binding.imgNP)
            binding.nameNP.text=PlayerActivity.musicListPA[PlayerActivity.songPosition].name
            if(PlayerActivity.isPlay){
                binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_pause_24)
            }
            else{
                binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }
    private fun play(){
       PlayerActivity.player!!.start()
        binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_pause_24)
        PlayerActivity.binding.nextbtn.setIconResource(R.drawable.ic_baseline_pause_24)
        PlayerActivity.isPlay=true
    }
    private fun pause(){
        PlayerActivity.player!!.pause()
        binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        PlayerActivity.binding.nextbtn.setIconResource((R.drawable.ic_baseline_play_arrow_24))
        PlayerActivity.isPlay=false
    }
    companion object{
        lateinit var binding:FragmentCurrentSongBinding
    }
}