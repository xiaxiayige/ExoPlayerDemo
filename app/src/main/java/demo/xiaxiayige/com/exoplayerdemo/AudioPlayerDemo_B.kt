package demo.xiaxiayige.com.exoplayerdemo

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

/***
 * 音频的连续播放
 */
class AudioPlayerDemo_B : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_)
        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  userAgent -> audio/mpeg  不能为空
        val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源
        val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源

        val mediaSource2 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Let%20It%20Go%20%281%29.mp3")) //创建一个播放数据源

        concatenatingMediaSource.addMediaSource(mediaSource1)
        concatenatingMediaSource.addMediaSource(mediaSource2)

        val loopMediaSouce = LoopingMediaSource(concatenatingMediaSource)// 实现多个音频循环播放
        val mergingMediaSource = MergingMediaSource(mediaSource1, mediaSource2) //音频合并
        player.playWhenReady = true


//        player.prepare(loopMediaSouce) 讲loop关联player
//        player.prepare(concatenatingMediaSource) //concatenatingMediaSource 关联串联的MediSource
        player.prepare(mergingMediaSource)
    }
}
