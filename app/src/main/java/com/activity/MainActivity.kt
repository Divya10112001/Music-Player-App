package com.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var previousmenu: MenuItem? = null
    lateinit var toolbar: Toolbar
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicApplication)
        setContentView(R.layout.activity_main)
        requestrunTime()
        setCurrentFragment(HomeFragment())
        toolbar = findViewById(R.id.toolbar)
        settoolbar()
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (previousmenu != null) {
                previousmenu?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousmenu = it
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                }
                R.id.favourites -> {
                    supportActionBar?.title = "My Favourites"
                    setCurrentFragment(FavouriteFragment())
                    bottomNavigationView.menu.findItem(R.id.favourites).isChecked = true
                }
                R.id.playlist -> {
                    supportActionBar?.title = "My Playlist"
                    setCurrentFragment(PlaylistFragment())
                    bottomNavigationView.menu.findItem(R.id.playlist).isChecked = true
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()

        }

    fun settoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Songs"
        supportActionBar?.setHomeButtonEnabled(true)
    }

    fun openHome() {
        supportActionBar?.title = "My Songs"
        setCurrentFragment(HomeFragment())
        bottomNavigationView.menu.findItem(R.id.home).isChecked = true
    }
    private fun requestrunTime(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }else{
                ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }
        }
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.flFragment)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()

        }
    }

}