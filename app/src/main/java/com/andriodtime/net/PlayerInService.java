package com.andriodtime.net;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class PlayerInService extends Service implements OnClickListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
	private WeakReference<ImageButton> btnPlay;
	private WeakReference<ImageButton> btnStop;
	public static WeakReference<TextView> textViewSongTime;
	public static WeakReference<SeekBar> songProgressBar;
	static Handler progressBarHandler = new Handler();
	
	public static MediaPlayer mp;
	private boolean isPause = false;  

	@Override
	public void onCreate() {
		mp = new MediaPlayer();
		mp.reset();
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initUI();
		super.onStart(intent, startId);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void initUI() {
		btnPlay = new WeakReference<>(MainActivity.btnPlay);
		btnStop = new WeakReference<>(MainActivity.btnStop);
        textViewSongTime = new WeakReference<>(MainActivity.textViewSongTime);
		songProgressBar = new WeakReference<>(MainActivity.seekBar);
		songProgressBar.get().setOnSeekBarChangeListener(this);
		btnPlay.get().setOnClickListener(this);
		btnStop.get().setOnClickListener(this);
        mp.setOnCompletionListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			if (mp.isPlaying()) {
				mp.pause();
				isPause = true;
                progressBarHandler.removeCallbacks(mUpdateTimeTask);
				btnPlay.get().setBackgroundResource(R.drawable.player);
				return;
			}
			
			if (isPause) {
				mp.start();
				isPause = false;
                updateProgressBar();
				btnPlay.get().setBackgroundResource(R.drawable.pause);
				return;
			}

			if (!mp.isPlaying()) {
				playSong();
			} 
			
			break;
		case R.id.btnStop:
			mp.stop();
            onCompletion(mp);
            textViewSongTime.get().setText("0.00/0.00"); // Displaying time completed playing
			break;

		}
	}

	public void updateProgressBar(){
		try{
			progressBarHandler.postDelayed(mUpdateTimeTask, 100);
		}catch(Exception e){

		}
	}

	static Runnable mUpdateTimeTask = new Runnable() {
		public void run(){
			long totalDuration = 0;
			long currentDuration = 0;

			try {
				totalDuration = mp.getDuration();
				currentDuration = mp.getCurrentPosition();
                textViewSongTime.get().setText(Utility.milliSecondsToTimer(currentDuration) + "/" + Utility.milliSecondsToTimer(totalDuration)); // Displaying time completed playing
				int progress = (int)(Utility.getProgressPercentage(currentDuration, totalDuration));
				songProgressBar.get().setProgress(progress);	/* Running this thread after 100 milliseconds */
                progressBarHandler.postDelayed(this, 100);

			} catch(Exception e){
				e.printStackTrace();
			}

		}
	};

	@Override
	public void onDestroy() {

	}

	// Play song
	public void playSong() {
		Utility.initNotification("Playing (Amar shonar bangla)...", this);
		try {
			mp.reset();
			Uri myUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.bangla);
			mp.setDataSource(this, myUri);
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    try {
                        mp.start();
                        updateProgressBar();
                        btnPlay.get().setBackgroundResource(R.drawable.pause);
                    } catch (Exception e) {
                        Log.i("EXCEPTION", "" + e.getMessage());
                    }
                }
            });

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public void onCompletion(MediaPlayer mp){
        songProgressBar.get().setProgress(0);
        progressBarHandler.removeCallbacks(mUpdateTimeTask); /* Progress Update stop */
        btnPlay.get().setBackgroundResource(R.drawable.player);
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = Utility.progressToTimer(seekBar.getProgress(),totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
	}
}
