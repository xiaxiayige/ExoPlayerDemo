# ExoPlayer的学习和使用（音频）（一） #

## 1.前言 ##

Google GitHub ExoPlayer地址：[https://github.com/google/ExoPlayer](https://github.com/google/ExoPlayer)

相关教程网站：

[https://google.github.io/ExoPlayer/guide.html](https://google.github.io/ExoPlayer/guide.html)

简要说明：

ExoPlayer is an application level media player for Android. It provides an alternative to Android’s MediaPlayer API for playing audio and video both locally and over the Internet. ExoPlayer supports features not currently supported by Android’s MediaPlayer API, including DASH and SmoothStreaming adaptive playbacks. Unlike the MediaPlayer API, ExoPlayer is easy to customize and extend, and can be updated through Play Store application updates.

来自google翻译：

ExoPlayer是Android的应用程序级媒体播放器。 它提供了Android的MediaPlayer API的替代品，用于在本地和互联网上播放音频和视频。 ExoPlayer支持Android MediaPlayer API目前不支持的功能，包括DASH和SmoothStreaming自适应回放。 与MediaPlayer API不同，ExoPlayer易于定制和扩展，并可通过Play Store应用程序更新进行更新。

综上所述，大概就是MediaPlayer不如ExoPlayer，google推荐使用ExoPlayer。

## 2.How To Use

如何能用才是我们关注的重点，看得到效果，才知道是不是适合我们的。

首先我们至少要能让音频能够播放，我们才能做更多想做的事情，对吧，不然捣鼓半天，都不知道音频咋播放的，那真是不高兴。

接下来开始接入步骤，先让音频播放起来，

### 2.1.添加依赖 ###

	根目录的build.gradle文件添加

	repositories {
	    jcenter()
	    google()
	}
	
	app或者module下的build.gradle文件下添加
 
	implementation 'com.google.android.exoplayer:exoplayer:2.X.X'
	
	下面的内容按需添加，符合自己需求的
 
	implementation 'com.google.android.exoplayer:exoplayer-core:2.X.X'
	implementation 'com.google.android.exoplayer:exoplayer-dash:2.X.X' 

	(这里解释一下DASH（Dynamic Adaptive Streaming over HTTP）即自适应流媒体传输，什么意思呢，简单概括来说，就是在服务器端提前存好同一内容的不同码率、不同分辨率的多个分片以及相应的描述文件MPD，客户端在播放时即可以根据自身性能以及网络环境选择最适宜的版本)

	implementation 'com.google.android.exoplayer:exoplayer-ui:2.X.X'
	
	（这里我使用的是  implementation 'com.google.android.exoplayer:exoplayer:2.8.1'
    			    implementation 'com.google.android.exoplayer:exoplayer-core:2.8.1'	）

### 2.2.代码编写 ###

    
	1-> 

	获取player的一个实例，大多数情况可以直接使用 DefaultTrackSelector 
    
    ( DefaultTrackSelector 该类可以对当前播放的音视频进行操作，比如设置音轨，设置约束曲目选择，禁用渲染器)
    
    val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
	
	val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  audio/mpeg
     
	val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源 
    
	2->

	val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
	.createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源
	
	3->

	concatenatingMediaSource.addMediaSource(mediaSource1) //把数据源添加到concatenatingMediaSource里面，相当于添加到一个播放池
	
	4->

	player.playWhenReady = true //设置属性，当准备好以后 自动开始播放

	5->

	 player.prepare(concatenatingMediaSource) //把Player和数据源关联起来  

	6->

	ok,播放的功能就这样结束了，下面看看完整代码

	
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
直接拷贝上面代码 ，是可以直接打卡播放音频的哦，当前Activity没有做其他任何操作，打开以后自动播放。

**注意：如果需要自己控制播放或者暂停可以调用 player.playWhenReady = true 或者 player.playWhenReady = false 可以控制播放的暂停和继续播放**

##3.完成需求 ##
上面已经讲了如何播放一个音频，已经差不多完成了我们一个需求，毕竟至少是要能够播放。

接下来我们完成更多的需求。
	
	1.多个音频连续播放
	2.实现倍速播放-慢速或快速
	3.more and more （后台播放，锁屏播放，通知栏ui显示播放情况）

### 3.1.多个音频连续播放 ###
首先完成来完成第一个需求，第一个需求比较简单，上面已经涉及到了。

	val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源
       concatenatingMediaSource.addMediaSource(mediaSource1)
上面这串代码是把mediaSource1 添加到一个列表中，当然既然提供了Add方法，肯定还能继续Add，内部也是通过一个list来存储添加的数据。


这里需要了解一下 **LoopingMediaSource**  ，**MergingMediaSource**，**ConcatenatingMediaSource**，**ClippingMediaSource** 这4个MediaSource都是继承了CompositeMediaSource这个抽象类，所以我们一个一个来看看都是什么作用
	
####  3.1.1.LoopingMediaSource：  ####

	可以将ConcatenatingMediaSource添加到LoopingMediaSource中 

	 val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源

     val mediaSource2 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Let%20It%20Go%20%281%29.mp3")) //创建一个播放数据源

	concatenatingMediaSource.addMediaSource(mediaSource1)
	concatenatingMediaSource.addMediaSource(mediaSource2) //添加多个MediaSorce

    val loopMediaSouce=LoopingMediaSource(concatenatingMediaSource)// 实现多个音频循环播放
    player.playWhenReady = true
    player.prepare(loopMediaSouce) //调用次方法播放完成以后将不再继续播放

    player.prepare(concatenatingMediaSource)//调用次方法播放完成以后将不再继续播放


#### 3.1.2.ConcatenatingMediaSource: (线程安全，播放期间可以修改播放列表) ####
	
	可以调用add或remove修改播放列表

#### 3.1.3.MergingMediaSource： ####


	类似ConcatenatingMediaSource，合并2个音频，该类应该多用于视频，可以合并视频+字幕等信息 需要在同一个timeline上，如果音频
	播放使用该类，也相当于添加到一个list中,使用player.prepare(MergingMediaSource) 也将循环播放，不能动态修改播放列表


以上三个类型的代码如下：	
	
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

#### 3.1.4.ClippingMediaSource ####

	提供剪切功能，能够裁剪一个一段音频的区间，试一试，代码如下：
	
	
	class AudioPlayerDemo_C : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_)
        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val defaultDataSourceFactory = DefaultDataSourceFactory(this, "audio/mpeg") //  userAgent -> audio/mpeg  不能为空
        val concatenatingMediaSource = ConcatenatingMediaSource() //创建一个媒体连接源
        val mediaSource1 = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse("http://xiaxiayige.u.qiniudn.com/Big%20Big%20World.mp3")) //创建一个播放数据源
        concatenatingMediaSource.addMediaSource(mediaSource1)

        val clippingMediaSource=ClippingMediaSource(mediaSource1,10*1000*1000,20*1000*1000)  // 这里需要注意的是后面的开始时间和结束时间是微秒的单位，这里需要注意	，并且结束时间不能小于开始时间。

        player.playWhenReady = true
        player.prepare(clippingMediaSource)
     }
	}


现在到这里我们差不多已经知道了，上面的第一需求，怎么让音频连续播放，其中还扩展了其他几个功能 裁剪，合并，循环播放的功能。

### 3.2 音频倍速播放 ###

实现倍速播放通过ExoPlayer的playbackParameters 属性设置

    playbackParameters =   PlaybackParameters(speed,pitch,skipSilence)

	speed ：播放速率
	pitch：声调的变化
	skipSilence：是否跳过音频流中的静音


#### 3.2.1 首先添加2个按钮控制播放速度的加减 ####
	
	 
    <?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <SeekBar
        android:id="@+id/seek"
        android:layout_width="match_parent"
        android:layout_height="20dp" />


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <Button
            android:id="@+id/b1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="-0.1倍速"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <Button
            android:id="@+id/b2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="+0.1倍速"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <Button
            android:id="@+id/b4"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="减音调"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <Button
            android:id="@+id/b5"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="加音调"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
    </LinearLayout>

	</LinearLayout>

如图：
	![](https://i.imgur.com/YR15zAH.png)


前面已经说了通过PlaybackParameters来控制倍速和音调的加减 所以直接上代码了。

		/***
	 	* 倍速播放
 		*/
	class AudioPlayerDemo_D : AppCompatActivity() {
    var speed = 1.0f
    var pitch = 1.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_d)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
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


    }
	}

前面的代码基本一致，没有做什么变动

上图可以看到，顶部有一个进度条，那么，我们也顺便来做下如何获取播放进度吧，并且调整进度。

步骤：

	 1.需要通过监听音频开始播放的时候
	 2.需要不断的去获取当前播放进度，也就是需要一个定时器去获取当前播放时长
	 3.通过SeekBar调整播放进度

    /***
 	* 倍速播放
	 */
	class AudioPlayerDemo_D : AppCompatActivity() {
    var speed = 1.0f
    var pitch = 1.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player_demo_d)

        val player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
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

	//更新进度条
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

	}



# 结尾 #
	
好的，到这里基本上暂时告一段落，从上面，学习了
    
	1.如何使用ExoPlayer进行简单的音频的播放
	2.使用ExoPlayer对多个音频顺序播放或者循环播放，并且使用concatenatingMediaSource可以在过程中操作更新MediaSource-添加或移除，MergingMediaSource不能实现更新操作,
	3.学习了音频的裁剪，可以指定播放某一段音频数据
	4.学习了音频的倍速播放，已经音频的声调修改
	5.使用SeekBar实现对ExoPlayer指定播放，并且获取播放当前位置

最后的最后在Activity销毁的时候别忘记释放掉player实例 

	 override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }