package demo.xiaxiayige.com.exoplayerdemo

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class AudioPlayerDemo_A : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_)
        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  userAgent -> audio/mpeg  不能为空
        val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源
        val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源
        concatenatingMediaSource.addMediaSource(mediaSource1)
        player.playWhenReady = true
        player.prepare(concatenatingMediaSource)
    }
}
