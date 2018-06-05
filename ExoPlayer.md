# ExoPlayer的学习和使用（音频）（一） #

## 1.前言 ##

Google GitHub EXOPlayer地址：[https://github.com/google/ExoPlayer](https://github.com/google/ExoPlayer)

相关教程网站：

[https://google.github.io/ExoPlayer/guide.html](https://google.github.io/ExoPlayer/guide.html)

简要说明：

ExoPlayer is an application level media player for Android. It provides an alternative to Android’s MediaPlayer API for playing audio and video both locally and over the Internet. ExoPlayer supports features not currently supported by Android’s MediaPlayer API, including DASH and SmoothStreaming adaptive playbacks. Unlike the MediaPlayer API, ExoPlayer is easy to customize and extend, and can be updated through Play Store application updates.

来自google翻译：

ExoPlayer是Android的应用程序级媒体播放器。 它提供了Android的MediaPlayer API的替代品，用于在本地和互联网上播放音频和视频。 ExoPlayer支持Android MediaPlayer API目前不支持的功能，包括DASH和SmoothStreaming自适应回放。 与MediaPlayer API不同，ExoPlayer易于定制和扩展，并可通过Play Store应用程序更新进行更新。

综上所述，大概就是MediaPlayer不如ExoPlayer，google推荐使用ExoPlayer。

## 2.How To Use
如何能用才是我们关注的重点，看得到效果，才知道是不是适合我们的。

接下来开始接入步骤

1.添加依赖

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

2.代码编写
