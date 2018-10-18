package com.knby.srmtimetable;

import android.app.SharedElementCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by Kevin on 1/25/2017.
 */

public class DayOrderCounter extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
        int dayOrder=sharedPreferences.getInt("dayOrder",1);
        int day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(day!=Calendar.SUNDAY)
        {
            if(dayOrder==5)
                dayOrder=1;
            else
                dayOrder++;

            sharedPreferences.edit().putInt("dayOrder",dayOrder).apply();
        }


    }
}
