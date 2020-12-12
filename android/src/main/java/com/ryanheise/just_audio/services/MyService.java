package com.ryanheise.just_audio.services;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ryanheise.just_audio.AudioPlayer;

import io.flutter.plugin.common.MethodChannel;

public class MyService extends Service {

    public SimpleExoPlayer player;
    private MediaSource mediaSource;

    public void onCreate() {
        super.onCreate();
        //初始化时就创建一个MediaPlayer进行资源链接

        player = new SimpleExoPlayer.Builder(this).build();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        player.release();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        //返回Service对象
        MyService getService() {
            return MyService.this;
        }

        public void setListener(Player.EventListener l) {
            player.addListener(l);
        }


        public void setUrl(String url) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this.getService(), Util.getUserAgent(this.getService(), "just_audio"));
            Uri uri = Uri.parse(url);
            if (uri.getPath().toLowerCase().endsWith(".mpd")) {
                mediaSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            } else if (uri.getPath().toLowerCase().endsWith(".m3u8")) {
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            } else {
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }
            player.prepare(mediaSource);
        }

        public void setClip(final Long start, final Long end) {
            if (start != null || end != null) {
                player.prepare(new ClippingMediaSource(mediaSource,
                        (start != null ? start : 0) * 1000L,
                        (end != null ? end : C.TIME_END_OF_SOURCE) * 1000L));
            } else {
                player.prepare(mediaSource);
            }
        }

        public void setPlayWhenReady(boolean b) {
            player.setPlayWhenReady(b);
        }

        public Long getDuration() {
            return player.getDuration();
        }

        public Long getCurrentPosition() {
            return player.getCurrentPosition();
        }

        public boolean isPlaying() {
            return player.isPlaying();
        }

        public int getPlaybackState() {
            return player.getPlaybackState();
        }

        public void seekTo(final long position) {
            player.seekTo(position);
        }

        public void setVolume(final float volume) {
            player.setVolume(volume);
        }

        public void setPlaybackParameters(@Nullable PlaybackParameters playbackParameters) {
            player.setPlaybackParameters(playbackParameters);
        }

    }
}