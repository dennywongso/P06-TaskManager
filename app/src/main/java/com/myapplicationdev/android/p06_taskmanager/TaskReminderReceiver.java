package com.myapplicationdev.android.p06_taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

public class TaskReminderReceiver extends BroadcastReceiver {

    int notifReqCode = 123;

	@Override
	public void onReceive(Context context, Intent i) {

		int id = i.getIntExtra("id", -1);
		String name = i.getStringExtra("name");
		String desc = i.getStringExtra("desc");

		Intent intent = new Intent(context, AddActivity.class);

		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationCompat.Action action = new
				NotificationCompat.Action.Builder(
				R.mipmap.ic_launcher,
				"Launch task Manager",
				pIntent).build();


		Intent intentreply = new Intent(context,MainActivity.class);
		intentreply.putExtra("key",id);
		PendingIntent pendingIntentReply = PendingIntent.getActivity
				(context, 0, intentreply,
						PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteInput ri = new RemoteInput.Builder("status")
				.setLabel("Status report")
				.setChoices(new String [] {"Completed", "Not yet"})
				.build();


		NotificationCompat.Action action2 = new
				NotificationCompat.Action.Builder(
				R.mipmap.ic_launcher,
				"Reply",
				pendingIntentReply)
				.addRemoteInput(ri)
				.build();
		Intent intentAdd = new Intent(context,AddActivity.class);

		PendingIntent pendingIntentAdd = PendingIntent.getActivity
				(context, 0, intentAdd,
						PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteInput ri1 = new RemoteInput.Builder("status")
				.setLabel("Status report")
				.setChoices(new String [] {"Add", "Not yet"})
				.build();

		NotificationCompat.Action action3 = new
				NotificationCompat.Action.Builder(
				R.mipmap.ic_launcher,
				"Add",
				pendingIntentAdd)
				.addRemoteInput(ri1)
				.build();



		NotificationCompat.WearableExtender extender = new
				NotificationCompat.WearableExtender();
		extender.addAction(action);
		extender.addAction(action2);
		extender.addAction(action3);

//	Intent test = new Intent(context, MainActivity.class);
//		PendingIntent pendingIn = PendingIntent.getActivity
//				(context, 0, test,
//						PendingIntent.FLAG_UPDATE_CURRENT);
		// build notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentTitle("Task");
		builder.setContentText(name +"\n"+desc);
		builder.setSmallIcon(android.R.drawable.ic_dialog_info);
//		builder.setContentIntent(test);
		builder.setAutoCancel(true);
		builder.extend(extender);
		Notification n = builder.build();
		NotificationManagerCompat notificationManagerCompat =
				NotificationManagerCompat.from(context);
		notificationManagerCompat.notify(notifReqCode, n);

	}

}
