package com.knby.srmtimetable;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Kevin on 11/30/2016.
 */

public class DailyHomework extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager;
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("Timetable", Context.MODE_PRIVATE, null);
        Cursor noteNotifications=sqLiteDatabase.rawQuery("SELECT a.courseName,b.note FROM teacherSlots a LEFT JOIN notes b ",null);
        Log.i("Error",noteNotifications.toString());
        if(noteNotifications.getCount()!=0){
            noteNotifications.moveToFirst();
            int i=0;
            while(noteNotifications!=null)
            {



                    Log.i("Error",noteNotifications.toString());
                    Cursor teacherDetails=sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE slotName ='"+ noteNotifications.getString(0)+"'",null);
                    Log.i("Error","SELECT * FROM teacherSlots WHERE slotName ='"+ noteNotifications.getString(0)+"'");
                    //SELECT a.teacherName,a.courseName,b.note FROM teacherSlots a LEFT JOIN notes b ON a.slotName=b.slot
                    teacherDetails.moveToFirst();
                    NotificationCompat.BigTextStyle notificationCompat=  new NotificationCompat.BigTextStyle();
                    notificationCompat.setBigContentTitle(noteNotifications.getString(0));
                    notificationCompat.bigText(noteNotifications.getString(1));
                    Notification notification= new NotificationCompat.Builder(context)
                            .setTicker("Ticker")
                            .setContentTitle(noteNotifications.getString(0))
                            .setSmallIcon(R.drawable.ic_assignment_white_48dp)
                            .setContentText(noteNotifications.getString(1))
                            .setAutoCancel(true)
                            .setStyle(notificationCompat)
                            .build();
                    notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(i,notification);
                    Log.i("Error", noteNotifications.getString(1));
                    i++;





                if(noteNotifications.moveToNext()==false)break;
            }}


    }
}
