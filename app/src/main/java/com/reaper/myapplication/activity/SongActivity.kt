package com.reaper.myapplication.activity

import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.reaper.myapplication.MusicApplication
import com.reaper.myapplication.R
import com.reaper.myapplication.databinding.ActivitySongBinding
import com.reaper.myapplication.utils.MySongInfo
import com.reaper.myapplication.utils.OnlineSongsInfo

class SongActivity : AppCompatActivity() {

    private lateinit var favourites:ImageView
    private lateinit var favourites_selected:ImageView
    private lateinit var addToPlaylists:ImageView
    private lateinit var addToPlaylistsSelected:ImageView
    private lateinit var back:ImageView
    private lateinit var play:ImageView
    private lateinit var pause:ImageView
    private lateinit var songName: TextView
    private lateinit var songArtist: TextView
    private lateinit var songImage: ImageView
    private lateinit var previous: ImageView
    private lateinit var next: ImageView
    private lateinit var progressbarSongLoading: ProgressBar
    private lateinit var volumeSeekbar:SeekBar
    private lateinit var binding: ActivitySongBinding
    private lateinit var applic: MusicApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applic = application as MusicApplication
        back= binding.btnBackSong

        previous = binding.previous
        next = binding.next
        songName = binding.txtSongName
        songArtist = binding.txtSingerName
        songImage = binding.imgSongImage

        favourites= binding.favourites
        favourites_selected=binding.favouritesSelected
        favourites_selected.visibility=View.GONE

        addToPlaylists=binding.addtoplaylists
        addToPlaylistsSelected=binding.addtoplaylistsselected
        addToPlaylistsSelected.visibility=View.GONE

        val audioManager:AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeSeekbar=binding.volumeseekbar
        volumeSeekbar.max=maxVolume
        volumeSeekbar.progress = currentVolume

        volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,p1,1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        play=binding.play
        pause= binding.pause
        pause.visibility = View.GONE
        progressbarSongLoading = binding.progressBarSongLoading
        if(intent.getBooleanExtra("isLoaded",false)){
            progressbarSongLoading.visibility = View.GONE
            if(applic.mediaPlayer.isPlaying){
                play.visibility = View.VISIBLE
                pause.visibility = View.INVISIBLE
            }
            else{
                play.visibility = View.INVISIBLE
                pause.visibility = View.VISIBLE
            }
        }

        when {
            applic.currentOnlineSongsInfo != null -> {
                songName.text = applic.currentOnlineSongsInfo?.name
                songName.isSelected = true
                songArtist.text = applic.currentOnlineSongsInfo?.artist
                val imageUrl = applic.currentOnlineSongsInfo?.image
                Glide.with(this@SongActivity).load(imageUrl).error(R.drawable.music_image).into(songImage)

                previous.setOnClickListener {
                    play.visibility = View.INVISIBLE
                    pause.visibility = View.INVISIBLE
                    progressbarSongLoading.visibility = View.VISIBLE
                    val currentIndex = applic.onlineSongs.indexOf(applic.currentOnlineSongsInfo)
                    val previousIndex = if(currentIndex==0){
                        applic.onlineSongs.size - 1
                    } else{
                        currentIndex - 1
                    }
                    applic.mediaPlayer.stop()
                    applic.mediaPlayer.reset()
                    applic.mediaPlayer.setDataSource(applic.onlineSongs[previousIndex].url)
                    applic.mediaPlayer.prepareAsync()
                    applic.mediaPlayer.setOnCompletionListener {
                        applic.mainActivity?.onlinePlay?.visibility = View.GONE
                        applic.mainActivity?.onlinePause?.visibility = View.VISIBLE
                        applic.currentOnlineSongsInfo = null
                        applic.musicIsPlaying = false
                        next.callOnClick()
                    }
                    applic.mainActivity?.txtSongName?.text = applic.onlineSongs[previousIndex].name
                    applic.currentMySongInfo = null
                    applic.currentOnlineSongsInfo = applic.onlineSongs[previousIndex]
                    val intent= Intent(this@SongActivity, SongActivity::class.java)
                    intent.putExtra("isLoaded",false)
                    startActivity(intent)
                    finish()
                }
                next.setOnClickListener {
                    play.visibility = View.INVISIBLE
                    pause.visibility = View.INVISIBLE
                    progressbarSongLoading.visibility = View.VISIBLE
                    val currentIndex = applic.onlineSongs.indexOf(applic.currentOnlineSongsInfo)
                    val nextIndex = if(currentIndex==applic.onlineSongs.size - 1){
                        0
                    }
                    else{
                        currentIndex + 1
                    }
                    applic.mediaPlayer.stop()
                    applic.mediaPlayer.reset()
                    applic.mediaPlayer.setDataSource(applic.onlineSongs[nextIndex].url)
                    applic.mediaPlayer.prepareAsync()
                    applic.mediaPlayer.setOnCompletionListener {
                        applic.mainActivity?.onlinePlay?.visibility = View.GONE
                        applic.mainActivity?.onlinePause?.visibility = View.VISIBLE
                        applic.currentOnlineSongsInfo = null
                        applic.musicIsPlaying = false
                        next.callOnClick()
                    }
                    applic.mainActivity?.txtSongName?.text = applic.onlineSongs[nextIndex].name
                    applic.currentMySongInfo = null
                    applic.currentOnlineSongsInfo = applic.onlineSongs[nextIndex]
                    val intent= Intent(this@SongActivity, SongActivity::class.java)
                    intent.putExtra("isLoaded",false)
                    startActivity(intent)
                    finish()
                }

            }
            applic.currentMySongInfo != null -> {
                songName.text = applic.currentMySongInfo?.name
                songName.isSelected = true
                songArtist.text = applic.currentMySongInfo?.artist

                previous.setOnClickListener {
                    play.visibility = View.INVISIBLE
                    pause.visibility = View.INVISIBLE
                    progressbarSongLoading.visibility = View.VISIBLE
                    val currentIndex = applic.mySongs.indexOf(applic.currentMySongInfo)
                    val previousIndex = if(currentIndex==0){
                        applic.mySongs.size - 1
                    } else{
                        currentIndex - 1
                    }
                    applic.mediaPlayer.stop()
                    applic.mediaPlayer.reset()
                    applic.mediaPlayer.setDataSource(this@SongActivity,applic.mySongs[previousIndex].uri!!)
                    applic.mediaPlayer.prepareAsync()
                    applic.mediaPlayer.setOnCompletionListener {
                        applic.mainActivity?.onlinePlay?.visibility = View.GONE
                        applic.mainActivity?.onlinePause?.visibility = View.VISIBLE
                        applic.currentOnlineSongsInfo = null
                        applic.musicIsPlaying = false
                        next.callOnClick()
                    }
                    applic.mainActivity?.txtSongName?.text = applic.mySongs[previousIndex].name
                    applic.currentMySongInfo = null
                    applic.currentMySongInfo = applic.mySongs[previousIndex]
                    val intent= Intent(this@SongActivity, SongActivity::class.java)
                    intent.putExtra("isLoaded",false)
                    startActivity(intent)
                    finish()
                }
                next.setOnClickListener {
                    play.visibility = View.INVISIBLE
                    pause.visibility = View.INVISIBLE
                    progressbarSongLoading.visibility = View.VISIBLE
                    val currentIndex = applic.mySongs.indexOf(applic.currentMySongInfo)
                    val nextIndex = if(currentIndex==applic.mySongs.size - 1){
                        0
                    }
                    else{
                        currentIndex + 1
                    }
                    applic.mediaPlayer.stop()
                    applic.mediaPlayer.reset()
                    applic.mediaPlayer.setDataSource(this@SongActivity,applic.mySongs[nextIndex].uri!!)
                    applic.mediaPlayer.prepareAsync()
                    applic.mediaPlayer.setOnCompletionListener {
                        applic.mainActivity?.onlinePlay?.visibility = View.GONE
                        applic.mainActivity?.onlinePause?.visibility = View.VISIBLE
                        applic.currentOnlineSongsInfo = null
                        applic.musicIsPlaying = false
                        next.callOnClick()
                    }
                    applic.mainActivity?.txtSongName?.text = applic.mySongs[nextIndex].name
                    applic.currentMySongInfo = null
                    applic.currentMySongInfo = applic.mySongs[nextIndex]
                    val intent= Intent(this@SongActivity, SongActivity::class.java)
                    intent.putExtra("isLoaded",false)
                    startActivity(intent)
                    finish()
                }
            }
            else -> {
                Toast.makeText(this@SongActivity,"No Song Playing",Toast.LENGTH_SHORT).show()
                this.finish()
            }
        }

        if(applic.mediaPlayer.isPlaying){
            progressbarSongLoading.visibility = View.GONE
        }

        applic.mediaPlayer.setOnCompletionListener {
            applic.mainActivity?.onlinePlay?.visibility = View.GONE
            applic.mainActivity?.onlinePause?.visibility = View.VISIBLE
            applic.currentOnlineSongsInfo = null
            applic.musicIsPlaying = false
            next.callOnClick()
        }
        applic.mediaPlayer.setOnPreparedListener {
            it.start()
            applic.musicIsPlaying = true
            progressbarSongLoading.visibility = View.GONE
        }

        back.setOnClickListener {
            onBackPressed()
        }

        favourites.setOnClickListener {
            favourites.visibility=View.GONE
            favourites_selected.visibility=View.VISIBLE
            Toast.makeText(this@SongActivity, "Added To Favourites", Toast.LENGTH_SHORT).show()
        }

        favourites_selected.setOnClickListener {
            favourites_selected.visibility=View.GONE
            favourites.visibility=View.VISIBLE
            Toast.makeText(this@SongActivity, "Removed From Favourites", Toast.LENGTH_SHORT).show()
        }

        addToPlaylists.setOnClickListener {
            addToPlaylists.visibility=View.GONE
            addToPlaylistsSelected.visibility=View.VISIBLE
            Toast.makeText(this@SongActivity, "Added To playlist", Toast.LENGTH_SHORT).show()
            val intent=Intent(this@SongActivity,PlaylistSongs::class.java)
            startActivity(intent)
        }

        play.setOnClickListener {
            play.visibility=View.INVISIBLE
            pause.visibility=View.VISIBLE
            applic.mediaPlayer.pause()
            applic.musicIsPlaying = false
        }

        pause.setOnClickListener {
            pause.visibility=View.INVISIBLE
            play.visibility=View.VISIBLE
            applic.mediaPlayer.start()
            applic.musicIsPlaying = true
        }

    }

    override fun onBackPressed() {
        applic.mainActivity?.dragUpButton?.callOnClick()
        super.onBackPressed()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val audioManager:AudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            volumeSeekbar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return super.onKeyDown(keyCode, event);
    }


}
