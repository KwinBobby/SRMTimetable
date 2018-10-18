package com.knby.srmtimetable;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kevin on 7/26/2016.
 */
public class DailyNotifications extends BroadcastReceiver {

    Cursor chosenTimeTable,chosenSlot;
    SharedPreferences preferences;
    AlarmManager manager;
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("Timetable", Context.MODE_PRIVATE, null);
        preferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        NotificationManager noti=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
       // ArrayList arrayList,roomList;
        int index;
        int i=preferences.getInt("Hour",-1);
        int chosenBatch=preferences.getInt("Batch",-1);
        String slot="";
        String courseName="";
        String roomNo="";
        int dayOrder=preferences.getInt("dayOrder",1);
       // Log.i("Errore","revving up! part2 "+String.valueOf(chosenBatch));

        if(chosenBatch==1)
            chosenTimeTable=sqLiteDatabase.rawQuery("SELECT * FROM timetable1 WHERE dayOrder="+String.valueOf(dayOrder),null);
        else if(chosenBatch==2)
            chosenTimeTable=sqLiteDatabase.rawQuery("SELECT * FROM timetable2 WHERE dayOrder="+String.valueOf(dayOrder),null);
       // Log.i("Errore",String.valueOf(chosenTimeTable.getCount()));
      //  Log.i("Errore","revving up!");
   //     arrayList=new ArrayList();
       // roomList=new ArrayList();
        //Log.i("Error","1");
        if(chosenTimeTable.getCount()==0)Log.i("Errore","chosenTimeTable is not working");
        chosenTimeTable.moveToFirst();
        //Log.i("Error","2");

        //   Log.i("Error","in list activity ");
        while (chosenTimeTable!=null)
        {

            try{index=chosenTimeTable.getColumnIndex("Hour"+String.valueOf(i));
            //Log.i("Error",String.valueOf(index));
            slot=String.valueOf(chosenTimeTable.getString(index));
            //Log.i("Error",slot);
            //Log.i("Error","SELECT * FROM teacherSlots WHERE slotName='"+slot+"'");
            chosenSlot=sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE slotName='"+slot+"'",null);


            if(chosenSlot.getCount()==0)
            {courseName=slot;
                roomNo=String.valueOf(-1);}
            else
            {
                chosenSlot.moveToFirst();
                courseName=chosenSlot.getString(1);
                roomNo=chosenSlot.getString(3);
            }}
            catch (Exception e){
            Log.i("Errore","Execption found");
        }

            /*
            for(int in=0;in<10;in++)
            {
                index=chosenTimeTable.getColumnIndex("Hour"+String.valueOf(in+1));
                //Log.i("Error",String.valueOf(index));
                slot=String.valueOf(chosenTimeTable.getString(index));
                //Log.i("Error",slot);
                //Log.i("Error","SELECT * FROM teacherSlots WHERE slotName='"+slot+"'");
                chosenSlot=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE slotName='"+slot+"'",null);

                if(chosenSlot.getCount()==0)
                {arrayList.add(slot);
                    roomList.add(-1);}
                else
                {
                    chosenSlot.moveToFirst();
                    arrayList.add(chosenSlot.getString(1));
                    roomList.add(chosenSlot.getString(3));
                }

            }*/
            if(chosenTimeTable.moveToNext()==false)break;
        }
        Log.i("Errore","Hour "+String.valueOf(preferences.getInt("Hour",-1)));


        if(i==11)
        {
            preferences.edit().putInt("Hour",1).apply();
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
           /* for(int uniqueId=0;uniqueId<=10;uniqueId++)
            {
                pendingIntent = PendingIntent.getBroadcast(context, uniqueId, intent, 0);
                manager.cancel(pendingIntent);
            }
*/
            Log.i("Errore","Canceled");
            return;


        }
       if(!courseName.equals("Free")&&!roomNo.equals("-1"))
        { NotificationCompat.BigTextStyle notificationCompat=  new NotificationCompat.BigTextStyle();
        notificationCompat.setBigContentTitle(courseName);
        notificationCompat.bigText(roomNo);
        Notification notificationa= new NotificationCompat.Builder(context)
                .setTicker("Ticker")
                .setContentTitle(courseName)
                .setSmallIcon(R.drawable.ic_assignment_white_48dp)
                .setContentText(roomNo)
                .setAutoCancel(true)
                .setStyle(notificationCompat)
                .build();
       // NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       noti.notify(100,notificationa);}

        preferences.edit().putInt("Hour",i+1).apply();

    }
}
