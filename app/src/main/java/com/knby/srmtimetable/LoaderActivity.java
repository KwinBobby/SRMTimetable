package com.knby.srmtimetable;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knby.srmtimetable.classes.NotesRow;
import com.knby.srmtimetable.classes.TeacherRow;
import com.knby.srmtimetable.classes.TimeTableRow;

public class LoaderActivity extends AppCompatActivity {

    static SharedPreferences sharedPreferences;
    static SQLiteDatabase sqLiteDatabase;
    static int chosenBatch;
    Intent intent;
    static boolean goneToDetails;
    static NotificationManager notificationManager;
    public static final int RC_SIGN_IN = 1;
    Cursor c;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference,timetableReference,notesReference,teachersReference;
    private ChildEventListener mChildEventListener,NotesEventListener,TeachersEventListener,TimeTableEventListener;
    private FirebaseAuth mFirebaseAuth;
    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        count=0;
        ImageView imageView = (ImageView) findViewById(R.id.gif);
        Glide.with(this).load(R.drawable.giphy).into(imageView);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences=this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sqLiteDatabase = this.openOrCreateDatabase("Timetable", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS batch(batchNo INT(1))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable2(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable1(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
        sqLiteDatabase.execSQL("INSERT INTO batch(batchNo) VALUES (1)");
        chosenBatch=1;
        sharedPreferences.edit().putInt("Batch",1).apply();



         NotesEventListener=new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","NotesEventListenerLoader");
                //            Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
                try {
                    sqLiteDatabase.execSQL("INSERT INTO notes(slot,note) VALUES ('" + notesRow.getSlot() + "','" + notesRow.getNote() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+notesRow.toString());
                }




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");

                sqLiteDatabase.execSQL("UPDATE notes SET note = '"+notesRow.getNote()+"' WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+notesRow.getSlot()+"');");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
                MainActivity.sqLiteDatabase.execSQL("DELETE FROM notes WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+notesRow.getSlot()+"');");

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

         TeachersEventListener=new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               count++;
                Log.e("Dank","TeachersEventListenerLoader");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
                TeacherRow teacherRow = dataSnapshot.getValue(TeacherRow.class);
                try {
                    sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('" + teacherRow.getTeacherName() + "','" + teacherRow.getCourseName() + "','" + teacherRow.getSlotName() + "','" + teacherRow.getRoomName() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+teacherRow.toString());
                }
                if(count>=dataSnapshot.getChildrenCount())
                {
                    startActivity(new Intent(LoaderActivity.this,MainActivity.class));
                    LoaderActivity.this.finish();
                }

            }




            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","TeachersEventListener");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
                TeacherRow teacherRow = dataSnapshot.getValue(TeacherRow.class);
                //     sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('"+teacherRow.getTeacherName()+"','"+teacherRow.getCourseName()+"','"+teacherRow.getSlotName()+"','"+teacherRow.getRoomName()+"')");
                sqLiteDatabase.execSQL("UPDATE teacherSlots SET teacherName ='"+teacherRow.getTeacherName()+"' WHERE slotName = '"+teacherRow.getSlotName()+"'");
                sqLiteDatabase.execSQL("UPDATE teacherSlots SET courseName ='"+teacherRow.getCourseName()+"' WHERE slotName = '"+teacherRow.getSlotName()+"'");
                sqLiteDatabase.execSQL("UPDATE teacherSlots SET roomName ='"+teacherRow.getRoomName()+"' WHERE slotName = '"+teacherRow.getSlotName()+"'");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

         TimeTableEventListener=new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable2(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable1(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");

                TimeTableRow timeTableRow = dataSnapshot.getValue(TimeTableRow.class);

                try{
                    if (chosenBatch == 1)
                        MainActivity.sqLiteDatabase.execSQL("INSERT INTO timetable1(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (" + String.valueOf(timeTableRow.getDayOrder()) + ",'" + timeTableRow.getHour1() + "','" + timeTableRow.getHour2() + "','" + timeTableRow.getHour3() + "','" + timeTableRow.getHour4() + "','" + timeTableRow.getHour5() + "','" + timeTableRow.getHour6() + "','" + timeTableRow.getHour7() + "','" + timeTableRow.getHour8() + "','" + timeTableRow.getHour9() + "','" + timeTableRow.getHour10() + "')");
                    else
                        MainActivity.sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (" + String.valueOf(timeTableRow.getDayOrder()) + ",'" + timeTableRow.getHour1() + "','" + timeTableRow.getHour2() + "','" + timeTableRow.getHour3() + "','" + timeTableRow.getHour4() + "','" + timeTableRow.getHour5() + "','" + timeTableRow.getHour6() + "','" + timeTableRow.getHour7() + "','" + timeTableRow.getHour8() + "','" + timeTableRow.getHour9() + "','" + timeTableRow.getHour10() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+timeTableRow.toString());
                }

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable2(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable1(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");

                TimeTableRow timeTableRow=dataSnapshot.getValue(TimeTableRow.class);
                if(chosenBatch==1)
                    MainActivity.sqLiteDatabase.execSQL("UPDATE timetable1 SET Hour1 = '"+timeTableRow.getHour1() +"',Hour2 = '"+timeTableRow.getHour2() +"',Hour3 = '"+timeTableRow.getHour3() +"',Hour4 = '"+timeTableRow.getHour4() +"',Hour5 = '"+timeTableRow.getHour5() +"',Hour6 = '"+timeTableRow.getHour6() +"',Hour7 = '"+timeTableRow.getHour7() +"',Hour8 = '"+timeTableRow.getHour8() +"',Hour9 = '"+timeTableRow.getHour9() +"',Hour10 = '"+timeTableRow.getHour10()+"' WHERE dayOrder="+String.valueOf(timeTableRow.getDayOrder()));
                else
                    MainActivity.sqLiteDatabase.execSQL("UPDATE timetable2 SET Hour1 = '"+timeTableRow.getHour1() +"',Hour2 = '"+timeTableRow.getHour2() +"',Hour3 = '"+timeTableRow.getHour3() +"',Hour4 = '"+timeTableRow.getHour4() +"',Hour5 = '"+timeTableRow.getHour5() +"',Hour6 = '"+timeTableRow.getHour6() +"',Hour7 = '"+timeTableRow.getHour7() +"',Hour8 = '"+timeTableRow.getHour8() +"',Hour9 = '"+timeTableRow.getHour9() +"',Hour10 = '"+timeTableRow.getHour10()+"' WHERE dayOrder="+String.valueOf(timeTableRow.getDayOrder()));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

             timetableReference = mFirebaseDatabase.getReference(uid+"/TimeTable");
             notesReference = mFirebaseDatabase.getReference(uid+"/Notes");
             teachersReference = mFirebaseDatabase.getReference(uid+"/Teachers");
            timetableReference.addChildEventListener(TimeTableEventListener);
            notesReference.addChildEventListener(NotesEventListener);
            teachersReference.addChildEventListener(TeachersEventListener);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        timetableReference.removeEventListener(TimeTableEventListener);
        teachersReference.removeEventListener(TeachersEventListener);
        notesReference.removeEventListener(NotesEventListener);
    }
}
