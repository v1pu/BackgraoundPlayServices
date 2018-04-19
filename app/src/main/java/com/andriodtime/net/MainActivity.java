package com.andriodtime.net;

/**
 * Author Md. Nazmul Hasan Masum
 * Background media player using service
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static ImageButton btnPlay, btnStop;
    public static TextView textViewSongTime;
    private Intent playerService;
    public static SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        playerService = new Intent(MainActivity.this, PlayerInService.class);
        startService(playerService);

    }

    private void initView() {
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textViewSongTime = (TextView) findViewById(R.id.textViewSongTime);

        TextView textViewIconCopyright = (TextView) findViewById(R.id.textViewIconCopyright);
        textViewIconCopyright.setText(Html.fromHtml("Icon made by " +
                "<a href=\"http://www.flaticon.com/authors/alessio-atzeni\">Alessio Atzeni</a> " +
                "from " + "<a href=\"http://www.flaticon.com\">www.flaticon.com</a> "));
        textViewIconCopyright.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        if (!PlayerInService.mp.isPlaying()) {
            PlayerInService.mp.stop();
            stopService(playerService);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {
            if (!PlayerInService.mp.isPlaying()) {
                btnPlay.setBackgroundResource(R.drawable.player);

            } else {
                btnPlay.setBackgroundResource(R.drawable.pause);
            }
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage() + e.getStackTrace() + e.getCause());
        }

        super.onResume();

    }


}
