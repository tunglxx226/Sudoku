package org.example.sudoku;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

public class VideoviewActivity extends Activity implements OnCompletionListener, OnTouchListener
{
	boolean buttonVisibility = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.videoview);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        
        VideoView videoHolder = (VideoView) findViewById(R.id.video);
        
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" 
        + R.raw.intro);
        videoHolder.setVideoURI(video);

        videoHolder.start();
        videoHolder.setOnTouchListener(this);
        videoHolder.setOnCompletionListener(this);
    }

	public void onCompletion(MediaPlayer mp) 
	{
		finish();
	}

	public boolean onTouch(View v, MotionEvent event) 
	{
		if (v.getId() == R.id.video)
		{
			finish();
			buttonVisibility = true;
			return true;
		}
		return false;
	}
}