package uk.ac.ed.timetable;

import uk.ac.ed.timetable.reminder.AlarmService;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class TimetableReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if ((context != null) && (intent != null))
		{
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				try
				{
					context.startService(new Intent(context, AlarmService.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
