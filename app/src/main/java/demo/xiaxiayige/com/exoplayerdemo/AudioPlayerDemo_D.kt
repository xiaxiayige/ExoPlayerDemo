package demo.xiaxiayige.com.exoplayerdemo

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.SeekBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_audio_player_demo_d.*
import java.lang.ref.WeakReference

/***
 * 倍速播放
 */
class AudioPlayerDemo_D : AppCompatActivity() {
    var speed = 1.0f
    var pitch = 1.0f
    lateinit var player: SimpleExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_d)

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  userAgent -> audio/mpeg  不能为空
        val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源
        val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源
        concatenatingMediaSource.addMediaSource(mediaSource1)
        player.playWhenReady = true
        player.prepare(concatenatingMediaSource)


        b1.setOnClickListener {
            speed -= 0.1f
            if (speed <= 0) speed = 0.1f
            player.playbackParameters = PlaybackParameters(speed, pitch)
        }

        b2.setOnClickListener {
            speed += 0.1f
            player.playbackParameters = PlaybackParameters(speed, pitch)
        }


        b4.setOnClickListener {
            pitch -= 0.1f
            player.playbackParameters = PlaybackParameters(speed, pitch)
        }

        b5.setOnClickListener {
            pitch += 0.1f
            player.playbackParameters = PlaybackParameters(speed, pitch)
        }

        val playHandler = PlayHandler(seek, player)
        concatenatingMediaSource.addEventListener(playHandler, object : DefaultMediaSourceEventListener() {
            override fun onLoadStarted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                super.onLoadStarted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                seek.max = player.duration.toInt()
                playHandler.sendEmptyMessage(0)
            }
        })

        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player.seekTo(seekBar?.progress!!.toLong())
            }

        })


    }


    class PlayHandler(seekBar: SeekBar, player: ExoPlayer) : Handler() {
        val seekBars = WeakReference<SeekBar>(seekBar)
        val players = WeakReference<ExoPlayer>(player)

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    seekBars.get()?.max = players.get()?.duration!!.toInt()
                    seekBars.get()?.progress = players.get()?.currentPosition!!.toInt()
                    sendEmptyMessageDelayed(0, 300)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

}
