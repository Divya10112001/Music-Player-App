package com.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.activity.R.layout.*
import com.activity.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fragment.FavouriteFragment
import com.fragment.HomeFragment
import com.fragment.HomeFragment.Companion.musicListSearch
import com.fragment.PlaylistFragment
import com.fragment.currentSongFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.modal.*
import java.lang.Exception
import kotlin.system.exitProcess

class PlayerActivity : AppCompatActivity(),MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener {
   companion object{
       lateinit var musicListPA:ArrayList<music>
       lateinit var audioManager: AudioManager
       var songPosition:Int=0
       var player:MediaPlayer?=null
       var isPlay:Boolean=false
       lateinit var runnable: Runnable
       var repeat:Boolean=false
       var min15:Boolean=false
       var min30:Boolean=false
       lateinit var binding: ActivityPlayerBinding
       var nowPlayingId:String=""
       var isFav:Boolean=false
       var fIndex:Int=-1
   }
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicApplication)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (intent.data?.scheme.contentEquals("content")){musicListPA = ArrayList()
            musicListPA.add(getSong(intent.data!!))
            Glide.with(this).load(getImageArt(musicListPA[songPosition].path)).apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(binding.songimg)
            binding.songtitle.text= musicListPA[songPosition].name
            createPlayer()
            seekBarSetup()}
        else
        {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "PDetails" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist)
                musicListPA.shuffle()
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "PDAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistFragment.musicPlaylist.ref[PlaylistDetails.currentPlaylist].playlist)
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "FavouriteFragment" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteFragment.favList)
                musicListPA.shuffle()
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "Favourites" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteFragment.favList)
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "NowPlaying" -> {
                setLayout()
                binding.songstarttime.text = formatDuration(player!!.currentPosition.toLong())
                binding.songendtime.text = formatDuration(player!!.duration.toLong())
                binding.seekbar.progress = player!!.currentPosition
                binding.seekbar.max = player!!.duration
                if (isPlay) {
                    binding.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
                } else {
                    binding.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
            "MusicAdapterSearch" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(musicListSearch)
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "MusicAdapter" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(HomeFragment.MusicListMA)
                setLayout()
                createPlayer()
                seekBarSetup()
            }
            "MainActivity" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(HomeFragment.MusicListMA)
                musicListPA.shuffle()
                setLayout()
                createPlayer()
                seekBarSetup()
            }
        }
    }
        binding.playpausebtn.setOnClickListener{
            if(isPlay)    pauseMusic()
            else          playMusic()
        }
        binding.previousbtn.setOnClickListener {
             previousNextSong(false)
        }
        binding.nextbtn.setOnClickListener {
           previousNextSong(true)
        }
        binding.seekbar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, user: Boolean) {
                    if(user) player!!.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) =Unit

                override fun onStopTrackingTouch(p0: SeekBar?) =Unit
            }
        )
        binding.repeatbtn.setOnClickListener{
            if(!repeat){
                repeat=true
                binding.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
            }
            else{
                repeat=false
                binding.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.white))
            }
        }
        binding.equaliserbtn.setOnClickListener {
            try {
                val intent= Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION,player!!.audioSessionId)
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(intent,1)
            }catch (e:Exception){
                Toast.makeText(this, "Equalizer not supported", Toast.LENGTH_SHORT).show()}
        }
        binding.timerbtn.setOnClickListener {
            val timer= min15|| min30
            if(!timer)   { timeSheetDialog()}
            else{
                val builder=MaterialAlertDialogBuilder(this)
                builder.setTitle("StopTimer").setMessage("Do you want to stop timer?").setPositiveButton("Yes"){_,_->
                    min15=false
                    min30=false
                    binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.white))
                }.setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }
                val customDialog=builder.create()
                 customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.red)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.red)}
        }
        binding.sharebtn.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_SEND
            intent.type="audio/*"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(intent,"Sharing Music File!!"))
        }
        binding.favbtn.setOnClickListener {
            if(isFav){
                isFav=false
                binding.favbtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                FavouriteFragment.favList.removeAt(fIndex)
            }else{
                isFav=true
                binding.favbtn.setImageResource(R.drawable.ic_baseline_favorite_24)
                FavouriteFragment.favList.add(musicListPA[songPosition])

            }
        }
    }
     fun createPlayer(){
        try {
            if (player == null) {
                player = MediaPlayer()
            }
            player!!.reset()
            player!!.setDataSource(musicListPA[songPosition].path)
            player!!.prepare()
            player!!.start()
            isPlay=true
            binding.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
            binding.songstarttime.text= formatDuration(player!!.currentPosition.toLong())
            binding.songendtime.text= formatDuration(player!!.duration.toLong())
            binding.seekbar.progress=0
            binding.seekbar.max=player!!.duration
            player!!.setOnCompletionListener(this)
            nowPlayingId= musicListPA[songPosition].id
        }catch (e:Exception){return}
    }
    private fun setLayout(){
        fIndex= checkFavourite(musicListPA[songPosition].id)
      Glide.with(this).load(musicListPA[songPosition].imguri).apply(RequestOptions.placeholderOf(R.drawable.smlogo).centerCrop()).into(binding.songimg)
      binding.songtitle.text= musicListPA[songPosition].name
        if (repeat)
        { binding.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.black)) }
        if(min15 || min30)
        { binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.black)) }
        if(isFav)     binding.favbtn.setImageResource(R.drawable.ic_baseline_favorite_24)
        else          binding.favbtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    private fun playMusic(){
        binding.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
        isPlay=true
        player!!.start()
    }
    private fun pauseMusic(){
        binding.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        isPlay=false
        player!!.pause()

    }
    private fun previousNextSong(increment:Boolean){
        if(increment){
            setposition(true)
            setLayout()
            createPlayer()
        }else{
            setposition(false)
            setLayout()
            createPlayer()
        }
    }
     fun setposition(increment: Boolean){
        if(!repeat) {
            if (increment) {
                if (musicListPA.size - 1 == songPosition)
                    songPosition = 0
                else
                    ++songPosition
            } else {
                if (songPosition == 0)
                    songPosition = musicListPA.size - 1
                else
                    --songPosition
            }
        }
    }
    fun seekBarSetup(){
        runnable= Runnable {
            binding.songstarttime.text= formatDuration(player!!.currentPosition.toLong())
            binding.seekbar.progress= player!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,200)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setposition(true)
        createPlayer()
        try {
            setLayout()
        }catch (e:Exception){return}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 || resultCode== RESULT_OK)
            return
    }
    private fun timeSheetDialog(){
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(timesheet)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(this, "Music will stop after 15 min", Toast.LENGTH_SHORT).show()
            binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
            min15=true
            Thread{Thread.sleep((15*60000).toLong())
            if(min15){
                exitProcess(1)
            }}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(this, "Music will stop after 30 min", Toast.LENGTH_SHORT).show()
            binding.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.red))
            min30=true
            Thread{Thread.sleep((30*60000).toLong())
                if(min30){
                    exitProcess(1)
                }}.start()
            dialog.dismiss()
        }
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0 <=1){
            //pause music
            currentSongFragment.binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            pauseMusic()
        }else{
            //play music
            currentSongFragment.binding.playpauseBtnNP.setIconResource(R.drawable.ic_baseline_pause_24)
            playMusic()
        }
    }
    private fun getSong(contentUri: Uri):music{
        var cursor: Cursor?=null
        try{
            val projection= arrayOf(MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION)
            cursor=this.contentResolver.query(contentUri,projection,null,null,null)
            val dataColumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path= dataColumn?.let { cursor.getString(it) }
            val duration= durationColumn?.let { cursor.getLong(it) }
             return music("UNKNOWN",path.toString(),"UNKNOWN","UNKNOWN",duration!!.toLong(),path.toString(),"UNKNOWN")

        }finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicListPA[songPosition].id=="UNKNOWN" && !isPlay)     exitProcess(1)
    }
}