package uk.ac.ed.timetable.reminder;

import android.content.Context;
import android.media.MediaPlayer;

public class AlarmSound
{
	// fields
	private Context context = null;
	MediaPlayer mMediaPlayer = null;

	// methods
	public AlarmSound(Context context)
	{
		this.context = context;
	}

	public void play()
	{
		mMediaPlayer = MediaPlayer.create(context, uk.ac.ed.timetable.R.raw.alarm);
		mMediaPlayer.start();
	}
	
	public void finalize()
	{
		mMediaPlayer.release();
	}

}
