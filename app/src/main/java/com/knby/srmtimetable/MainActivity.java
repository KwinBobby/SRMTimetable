package com.knby.srmtimetable;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knby.srmtimetable.classes.NotesRow;
import com.knby.srmtimetable.classes.TeacherRow;
import com.knby.srmtimetable.classes.TimeTableRow;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    static SharedPreferences sharedPreferences;
    static SQLiteDatabase sqLiteDatabase;
    static int chosenBatch;
    Intent intent;
    static boolean goneToDetails;
    static NotificationManager notificationManager;
    public static final int RC_SIGN_IN = 1;
    Cursor c;



    public void display(View v)
    {
       // Log.i("Error","not working");
        int i= Integer.valueOf(v.getTag().toString());
        intent=new Intent(getApplicationContext(), com.knby.srmtimetable.ListActivity.class);
        intent.putExtra("tag",i);
        startActivity(intent);

    }

    private String mUsername;
    public static final String ANONYMOUS = "anonymous";


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        sharedPreferences=this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        boolean windowShown =sharedPreferences.getBoolean("windowShown",false);
        final Intent i=new Intent(getApplicationContext(),UpdateActivity.class);
        goneToDetails=false;

        //sharedPreferences.edit().putBoolean("iNeedNotifications",false).apply();


        sqLiteDatabase = this.openOrCreateDatabase("Timetable", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS batch(batchNo INT(1))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable2(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable1(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");

        if(sharedPreferences.getInt("Hour",-1)==-1)
        sharedPreferences.edit().putInt("Hour",1).apply();


         c = sqLiteDatabase.rawQuery("SELECT * FROM batch", null);

        if (c.getCount() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Batch")
                    .setMessage("Which batch are you?")
                    .setNegativeButton("ONE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqLiteDatabase.execSQL("INSERT INTO batch(batchNo) VALUES (1)");
                            chosenBatch=1;
                            sharedPreferences.edit().putInt("Batch",1).apply();
                            startActivity(i);


                        }
                    })
                    .setPositiveButton("TWO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqLiteDatabase.execSQL("INSERT INTO batch(batchNo) VALUES (2)");
                            chosenBatch=2;
                            sharedPreferences.edit().putInt("Batch",2).apply();
                            startActivity(i);
                        }
                    })
                    .show();
        } else{
            c.moveToFirst();
            chosenBatch=c.getInt(0);
            //  Log.i("Errore",Integer.toString(c.getInt(0)));
        }

        //Log.i("Error",Integer.toString(chosenBatch));

        if(windowShown==false){

            new AlertDialog.Builder(this)
                    .setTitle("Instructions to follow")
                    .setMessage("1.Choose your batch\n2.Enter the subject details for slots A to G\n3.The lab slots can be added later\n4.You can edit any period by long pressing it\n5.If any period is free,edit it and set the course name and slot to be Free\n6.If you have more than one subject in the same slot,give slots to the other subjects with a number\nEg:D,then D1,then D2,then so on")                               //instructions to be updated
                    .setPositiveButton("OK",null)
                    .show();
            sharedPreferences.edit().putBoolean("windowShown",true).apply();

        }


       // Log.i("Error","after startactivity");



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


        ChildEventListener NotesEventListener=new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","NotesEventListener123");
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

        ChildEventListener TeachersEventListener=new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","TeachersEventListener");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
                TeacherRow teacherRow = dataSnapshot.getValue(TeacherRow.class);
                try {
                    sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('" + teacherRow.getTeacherName() + "','" + teacherRow.getCourseName() + "','" + teacherRow.getSlotName() + "','" + teacherRow.getRoomName() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+teacherRow.toString());
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

        ChildEventListener TimeTableEventListener=new ChildEventListener() {

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

            DatabaseReference timetableReference = mFirebaseDatabase.getReference(uid+"/TimeTable");
            DatabaseReference notesReference = mFirebaseDatabase.getReference(uid+"/Notes");
            DatabaseReference teachersReference = mFirebaseDatabase.getReference(uid+"/Teachers");
            timetableReference.addChildEventListener(TimeTableEventListener);
            notesReference.addChildEventListener(NotesEventListener);
            teachersReference.addChildEventListener(TeachersEventListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                mFirebaseDatabase.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            startActivity(new Intent(MainActivity.this,LoaderActivity.class));
                            MainActivity.this.finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();


            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
      //  mMessageAdapter.clear();
        detachDatabaseReadListener();
    }




    private void onSignedInInitialize(String username) {
        mUsername = username;
    //    attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
      //  mMessageAdapter.clear();
        detachDatabaseReadListener();
    }



//    private void attachDatabaseReadListener() {
//        if (mChildEventListener == null) {
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
//                    mMessageAdapter.add(friendlyMessage);
//                }
//
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
//                public void onChildRemoved(DataSnapshot dataSnapshot) {}
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
//                public void onCancelled(DatabaseError databaseError) {}
//            };
//            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
//        }
//    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.help)
        {
            //Toast.makeText(getApplicationContext(),"Boo",Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle("Instructions to follow")
                    .setMessage("1.Choose your batch\n2.Enter the subject details for slots A to G\n3.The lab slots can be added later\n4.You can edit any period by long pressing it\n5.If any period is free,edit it and set the course name and slot to be Free\n6.If you have more than one subject in the same slot,give slots to the other subjects with a number\nEg:D,then D1,then D2,then so on\n7.While adding labs ,give them a unique slot name")                               //instructions to be updated
                    .setPositiveButton("OK",null)
                    .show();

            return true;
        }

        else if(id==R.id.setting)
        {
            intent =new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.removeNotes)
        {
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference.child(uid).child("Notes").removeValue();

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS notes");
        }
        else if(id==R.id.logs)
        {

          try {
              c = sqLiteDatabase.rawQuery("SELECT count(*) FROM teacherSlots", null);
              c.moveToFirst();
              Log.e("Huh?",c.toString());
              Toast.makeText(this, "No. of teachers: " + c.getInt(0), Toast.LENGTH_SHORT).show();
              c = sqLiteDatabase.rawQuery("SELECT count(*) FROM notes", null);
              c.moveToFirst();
              Toast.makeText(this, "No. of notes: " + c.getInt(0), Toast.LENGTH_SHORT).show();
          }catch (Exception e)
          {

          }

        }
        else if(id==R.id.logout)
        {
            MainActivity.this.deleteDatabase("Timetable");
            AuthUI.getInstance().signOut(this);
            return true;

        }
        else if(id==R.id.checkForUpdates)
        {

            Cursor noteNotifications=sqLiteDatabase.rawQuery("SELECT courseName,note FROM teacherSlots INNER JOIN notes ON teacherSlots.slotName = notes.slot",null);
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
                    Notification notification= new NotificationCompat.Builder(MainActivity.this)
                            .setTicker("Ticker")
                            .setContentTitle(noteNotifications.getString(0))
                            .setSmallIcon(R.drawable.ic_assignment_white_48dp)
                            .setContentText(noteNotifications.getString(1))
                            .setAutoCancel(true)
                            .setStyle(notificationCompat)
                            .build();
                    notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(i,notification);
                    Log.i("Error", noteNotifications.getString(1));
                    i++;

                    if(noteNotifications.moveToNext()==false)break;
            }}
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS teacherSlots");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS notes");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS timetable1");
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS timetable2");
////        Log.e("Dank","im dying stop");
//    }

}
