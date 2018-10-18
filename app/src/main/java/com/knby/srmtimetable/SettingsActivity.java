package com.knby.srmtimetable;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    static boolean iNeedNotifications;
    static AlarmManager manager;
    static PendingIntent pendingIntent;
    static Switch onOff,homeworkSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
         onOff=(Switch)findViewById(R.id.onOff);
         homeworkSwitch=(Switch)findViewById(R.id.homeworkSwitch);
        if(MainActivity.sharedPreferences.getBoolean("iNeedNotifications",false)==true)
            onOff.setChecked(true);
        else
            onOff.setChecked(false);

        if(MainActivity.sharedPreferences.getBoolean("HomeworkNotifs",false)==true)
            homeworkSwitch.setChecked(true);
        else
            homeworkSwitch.setChecked(false);
       final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        int dayOrder=MainActivity.sharedPreferences.getInt("dayOrder",1);
        String spinnerArray[] ={"1","2","3","4","5"};

        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(dayOrder-1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SettingsActivity.this, String.valueOf(spinner.getSelectedItem()), Toast.LENGTH_SHORT).show();
                MainActivity.sharedPreferences.edit().putInt("dayOrder",Integer.valueOf(String.valueOf(spinner.getSelectedItem()))).apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        homeworkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked==true)
                {
                    MainActivity.sharedPreferences.edit().putBoolean("HomeworkNotifs",true).apply();
                    homeworkSwitch.setChecked(true);
                    TimePickerDialog timePickerDialog;
                    timePickerDialog=new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(SettingsActivity.this, DailyHomework.class);
                            pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);
                            Toast.makeText(SettingsActivity.this,String.valueOf(hourOfDay)+" "+String.valueOf(minute), Toast.LENGTH_SHORT).show();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        }
                    },0,0,true);
                    timePickerDialog.setTitle("Select Time");
                    timePickerDialog.show();
                }
                else
                {
                    MainActivity.sharedPreferences.edit().putBoolean("HomeworkNotifs",false).apply();
                    homeworkSwitch.setChecked(false);
                    manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(SettingsActivity.this, DailyHomework.class);
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);
                    manager.cancel(pendingIntent);
                }

            }
        });





        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true)
                {
                    iNeedNotifications=true;
                    MainActivity.sharedPreferences.edit().putBoolean("iNeedNotifications",iNeedNotifications).apply();
                    onOff.setChecked(true);

                       /* manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(SettingsActivity.this, DailyNotifications.class);
                        pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, 22);
                        calendar.set(Calendar.MINUTE, 10);
                        calendar.set(Calendar.SECOND, 0);
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000*60*10, pendingIntent);*/

                    makeAlarms(0,7,50);
                    makeAlarms(1,8,40);
                    makeAlarms(2,9,30);
                    makeAlarms(3,10,25);
                    makeAlarms(4,11,20);
                    makeAlarms(5,12,15);
                    Log.i("Errore","Hello");
                    makeAlarms(6,13,10);
                    makeAlarms(7,14,05);
                    makeAlarms(8,15,0);
                    makeAlarms(9,15,55);
                    Intent intent = new Intent(SettingsActivity.this, DayOrderCounter.class);
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 99, intent, 0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 58);
                    calendar.set(Calendar.SECOND, 0);
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);



                    // NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                   // noti.cancel(100);
                    MainActivity.sharedPreferences.edit().putInt("Hour",1).apply();

                }
                else
                {
                    iNeedNotifications=false;
                    MainActivity.sharedPreferences.edit().putBoolean("iNeedNotifications",iNeedNotifications).apply();
                    onOff.setChecked(false);
                    Intent intent = new Intent(SettingsActivity.this, DailyNotifications.class);
                    for(int i=0;i<10;i++)
                    {
                        pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, i, intent, 0);
                        manager.cancel(pendingIntent);
                    }
                    intent = new Intent(SettingsActivity.this, DayOrderCounter.class);
                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 99, intent, 0);
                    manager.cancel(pendingIntent);
                    MainActivity.sharedPreferences.edit().putInt("Hour",1).apply();
                }
            }
        });
        Log.i("Errore",String.valueOf(MainActivity.sharedPreferences.getBoolean("iNeedNotifications",false)));

    }

    void makeAlarms(int uniqueId,int hour,int minute)
    {
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(SettingsActivity.this, DailyNotifications.class);
        pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, uniqueId, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }


}
