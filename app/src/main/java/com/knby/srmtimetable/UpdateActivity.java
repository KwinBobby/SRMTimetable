package com.knby.srmtimetable;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.knby.srmtimetable.classes.TeacherRow;
import com.knby.srmtimetable.classes.TimeTableRow;

public class UpdateActivity extends AppCompatActivity {

    TextView slot;
    EditText teacherName,courseName,roomName;
    FloatingActionButton fab;
    int i;
    char slotName;
    DatabaseReference databaseReference,teachersReference;
    Cursor c;
    FirebaseDatabase mFirebaseDatabase;
    ChildEventListener TeachersEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=firebaseUser.getUid();
        teachersReference = mFirebaseDatabase.getReference(uid+"/Teachers");
        SettingsActivity.iNeedNotifications=false;
        MainActivity.sharedPreferences.edit().putBoolean("iNeedNotifications",false).apply();

        MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable1(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
        MainActivity.sqLiteDatabase.execSQL("BEGIN");
        c=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM timetable1",null);
        if(c.getCount()==0){
            ValueEventListener timeTableListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int number=1;
                    TimeTableRow timeTableRow;
//                    TimeTableRow timeTableRow=dataSnapshot.getValue(TimeTableRow.class);
//                    sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES ("+String.valueOf(timeTableRow.getDayOrder())+",'"+timeTableRow.getHour1()+"','"+timeTableRow.getHour2()+"','"+timeTableRow.getHour3()+"','"+timeTableRow.getHour4()+"','"+timeTableRow.getHour5()+"','"+timeTableRow.getHour6()+"','"+timeTableRow.getHour7()+"','"+timeTableRow.getHour8()+"','"+timeTableRow.getHour9()+"','"+timeTableRow.getHour10()+"')");
                    for(DataSnapshot rowSnapshot : dataSnapshot.getChildren())

                    {
                        Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());

                        timeTableRow=rowSnapshot.getValue(TimeTableRow.class);
                        MainActivity.sqLiteDatabase.execSQL("INSERT INTO timetable1(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES ("+String.valueOf(timeTableRow.getDayOrder())+",'"+timeTableRow.getHour1()+"','"+timeTableRow.getHour2()+"','"+timeTableRow.getHour3()+"','"+timeTableRow.getHour4()+"','"+timeTableRow.getHour5()+"','"+timeTableRow.getHour6()+"','"+timeTableRow.getHour7()+"','"+timeTableRow.getHour8()+"','"+timeTableRow.getHour9()+"','"+timeTableRow.getHour10()+"')");

                    }
                    //  Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            String batchString= String.valueOf(MainActivity.sharedPreferences.getInt("Batch",-1));
            DatabaseReference timeTableReference=mFirebaseDatabase.getReference("TimeTable"+batchString);
            timeTableReference.addValueEventListener(timeTableListener);

        }
        else {

            //you can use this part to update batch 1 timetable if necessary


        }
        MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timetable2(dayOrder INT(1) PRIMARY KEY NOT NULL,Hour1 VARCHAR NOT NULL,Hour2 VARCHAR NOT NULL,Hour3 VARCHAR NOT NULL,Hour4 VARCHAR NOT NULL,Hour5 VARCHAR NOT NULL,Hour6 VARCHAR NOT NULL,Hour7 VARCHAR NOT NULL,Hour8 VARCHAR NOT NULL,Hour9 VARCHAR NOT NULL,Hour10 VARCHAR NOT NULL)");
        c=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM timetable2",null);


        if(c.getCount()==0){

            ValueEventListener timeTableListener=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int number=1;
                    TimeTableRow timeTableRow;
//                    TimeTableRow timeTableRow=dataSnapshot.getValue(TimeTableRow.class);
//                    sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES ("+String.valueOf(timeTableRow.getDayOrder())+",'"+timeTableRow.getHour1()+"','"+timeTableRow.getHour2()+"','"+timeTableRow.getHour3()+"','"+timeTableRow.getHour4()+"','"+timeTableRow.getHour5()+"','"+timeTableRow.getHour6()+"','"+timeTableRow.getHour7()+"','"+timeTableRow.getHour8()+"','"+timeTableRow.getHour9()+"','"+timeTableRow.getHour10()+"')");
                    for(DataSnapshot rowSnapshot : dataSnapshot.getChildren())

                    {
                        Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());

                        timeTableRow=rowSnapshot.getValue(TimeTableRow.class);
                        MainActivity.sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES ("+String.valueOf(timeTableRow.getDayOrder())+",'"+timeTableRow.getHour1()+"','"+timeTableRow.getHour2()+"','"+timeTableRow.getHour3()+"','"+timeTableRow.getHour4()+"','"+timeTableRow.getHour5()+"','"+timeTableRow.getHour6()+"','"+timeTableRow.getHour7()+"','"+timeTableRow.getHour8()+"','"+timeTableRow.getHour9()+"','"+timeTableRow.getHour10()+"')");

                    }
                    //  Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            String batchString= String.valueOf(MainActivity.sharedPreferences.getInt("Batch",-1));
            DatabaseReference timeTableReference=mFirebaseDatabase.getReference("TimeTable"+batchString);
            timeTableReference.addListenerForSingleValueEvent(timeTableListener);

//            sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (1,'LAB','LAB','LAB','LAB','LAB','A','A','F','F','D')");
//            sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (2,'B','B','G','G','A','LAB','LAB','LAB','LAB','LAB')");
//            sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (3,'LAB','LAB','LAB','LAB','LAB','C','C','E','F','D')");
//            sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (4,'D','D','B','C','G','LAB','LAB','LAB','LAB','LAB')");
//            sqLiteDatabase.execSQL("INSERT INTO timetable2(dayOrder,Hour1,Hour2,Hour3,Hour4,Hour5,Hour6,Hour7,Hour8,Hour9,Hour10) VALUES (5,'LAB','LAB','LAB','LAB','LAB','E','E','A','B','C')");
        }
        else {

            //you can use this part to update batch 2 timetable if necessary

        }





        slotName='A';
        i=1;
        teacherName=(EditText)findViewById(R.id.teacherEntry);
        courseName=(EditText)findViewById(R.id.subjectEntry);
        roomName=(EditText)findViewById(R.id.roomEntry);
        slot=(TextView)findViewById(R.id.slot);
        fab=(FloatingActionButton)findViewById(R.id.fabnext);
        MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");


        final DatabaseReference userTimeTableReference=FirebaseDatabase.getInstance().getReference(uid).child("TimeTable");
        DatabaseReference TimetableReference;
        if(MainActivity.sharedPreferences.getInt("Batch",-1)==1)
         TimetableReference=FirebaseDatabase.getInstance().getReference("TimeTable1");
        else
            TimetableReference=FirebaseDatabase.getInstance().getReference("TimeTable2");

        TimetableReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTimeTableReference.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         TeachersEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","TeachersEventListener");
                MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS teacherSlots(teacherName VARCHAR,courseName VARCHAR PRIMARY KEY,slotName VARCHAR UNIQUE,roomName VARCHAR NOT NULL)");
                TeacherRow teacherRow = dataSnapshot.getValue(TeacherRow.class);
                try {
                    MainActivity.sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('" + teacherRow.getTeacherName() + "','" + teacherRow.getCourseName() + "','" + teacherRow.getSlotName() + "','" + teacherRow.getRoomName() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+teacherRow.toString());
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        teachersReference.addChildEventListener(TeachersEventListener);


        slot.setText("Slot "+String.valueOf(slotName));
       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!courseName.getText().toString().isEmpty()&&!roomName.getText().toString().isEmpty())
                {
             //       MainActivity.sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('"+teacherName.getText().toString()+"','"+courseName.getText().toString()+"','"+String.valueOf(slotName)+"','"+roomName.getText().toString()+"')");
                    TeacherRow teacherRow=new TeacherRow();
                    teacherRow.setTeacherName(teacherName.getText().toString());
                    teacherRow.setCourseName(courseName.getText().toString());
                    teacherRow.setSlotName(String.valueOf(slotName));
                    teacherRow.setRoomName(roomName.getText().toString());
                    databaseReference.child(uid).child("Teachers").child(String.valueOf(slotName)).setValue(teacherRow);


                    i++;
                if(i==8)
                {
                    UpdateActivity.this.finish();
                }
                else{

                slotName++;
                slot.setText("Slot "+String.valueOf(slotName));
                teacherName.setText("");
                courseName.setText("");
                roomName.setText("");

                }

                if(i==7)
                    fab.setImageResource(R.drawable.ic_check_white_48dp);

            }}
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(i!=8)
        {
            MainActivity.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS teacherSlots");
            MainActivity.sqLiteDatabase.execSQL("DROP TABLE IF EXISTS batch");
            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference.child(uid).child("Teachers").removeValue();
            MainActivity.sqLiteDatabase.execSQL("ROLLBACK");
            UpdateActivity.this.finishAffinity();


        }
        teachersReference.removeEventListener(TeachersEventListener);
    }
}
