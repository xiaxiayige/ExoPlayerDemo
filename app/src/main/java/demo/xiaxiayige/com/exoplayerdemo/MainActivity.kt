package demo.xiaxiayige.com.exoplayerdemo

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.EventLog
import android.util.Log
import android.widget.SeekBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.video.VideoListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    var speed = 1.0f
    var pitch = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  audio/mpeg
        val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源

        val playHandler=PlayHandler(seekBar = seek,player =player )

        concatenatingMediaSource.addEventListener(playHandler,object:DefaultMediaSourceEventListener(){
            override fun onLoadStarted(windowIndex: Int, mediaPeriodId: MediaSource.MediaPeriodId?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {
                super.onLoadStarted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData)
                seek.max=player.duration.toInt()
                playHandler.sendEmptyMessage(0)

            }
        })

        val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3"))
        val mediaSource2 = ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Let%20It%20Go%20%281%29.mp3"))
//        concatenatingMediaSource.addMediaSource(mediaSource1)
//        concatenatingMediaSource.addMediaSource(mediaSource2) //添加多个媒体数据源  不可以支持动态修改数据源 可以连续播放

        val mergAudio=ConcatenatingMediaSource (mediaSource1,mediaSource2) //连接多个音频，可以支持动态修改 可以连续播放

        MergingMediaSource()

        concatenatingMediaSource.addMediaSource(mergAudio)
        player.prepare(concatenatingMediaSource)

//        player.playWhenReady = false
//        player.playbackParameters = PlaybackParameters(speed,pitch,true)
        b1.setOnClickListener {
//            speed += 0.5f
//            player.playbackParameters = PlaybackParameters(speed,pitch)
            player.playWhenReady=true
        }
        b2.setOnClickListener {
//            pitch += 0.5f
            player.playbackParameters =  PlaybackParameters(speed,pitch)
            player.playWhenReady=false
        }
        player.audioAttributes.usage
    }

    class PlayHandler(seekBar:SeekBar,player:ExoPlayer):Handler(){
        val seekBars=WeakReference<SeekBar>(seekBar)
        val players=WeakReference<ExoPlayer>(player)

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){
            0->{
                seekBars.get()?.max=players.get()?.duration!!.toInt()
                seekBars.get()?.progress=players.get()?.currentPosition!!.toInt()
                sendEmptyMessageDelayed(0,300)
            }
            }
        }
    }
}
