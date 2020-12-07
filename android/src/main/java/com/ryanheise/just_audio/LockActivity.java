package com.ryanheise.just_audio;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ryanheise.just_audio.receiver.ScreenListener;
import com.ryanheise.just_audio.trans.GlideCircleWithBorder;

public class LockActivity extends Activity {
    String name;
    String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.lockactivity);
        initControl();
    }

    private void initControl() {

        name = getIntent().getStringExtra("name");
        img = getIntent().getStringExtra("img");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        ImageView iv_img = (ImageView) findViewById(R.id.iv_img);
        final ImageView iv_play = (ImageView) findViewById(R.id.iv_play);

        if (name != null && name.length() > 0) {
            tv_name.setText(name);
        }

        if (img != null && img.length() > 0) {
            Glide.with(this)
                    .load(img)
                    .apply(RequestOptions.bitmapTransform(new GlideCircleWithBorder(6, Color.parseColor("#66000000"))))

//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_img);
        }

        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AudioPlayer._myBinder != null) {

                    if (AudioPlayer._myBinder.isPlaying()) {
                        AudioPlayer.pause();
                        iv_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_btn_play));
                    } else {

//                        AudioplayerPlugin.mediaPlayer.start();
//                        AudioplayerPlugin.play(url);
                        AudioPlayer.play();
                        iv_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_btn_pause));
                    }

                }
            }
        });

        registLisener(this);
    }

    ScreenListener l;

    private void registLisener(final Context context) {
        l = new ScreenListener(context);
        l.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onUserPresent() {
                Log.e("onUserPresent", "onUserPresent");
                // finish();
            }

            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        l.unregisterListener();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

}
