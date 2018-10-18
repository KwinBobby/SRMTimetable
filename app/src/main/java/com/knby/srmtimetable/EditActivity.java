package com.knby.srmtimetable;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knby.srmtimetable.classes.TeacherRow;

public class EditActivity extends AppCompatActivity {

    Intent intent;
    DatabaseReference databaseReference;
    Cursor c;
    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid=firebaseUser.getUid();

        final TextView editTeacher=(TextView)findViewById(R.id.editTeacher);
        final TextView editSubject=(TextView)findViewById(R.id.editSubject);
        final TextView editSlot=(TextView)findViewById(R.id.editSlot);
        final TextView editRoom=(TextView)findViewById(R.id.editRoom);
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
      //  Button updateButton=(Button)findViewById(R.id.updateButton);
        intent=getIntent();
        final int clickedPosition=intent.getIntExtra("clickedPosition",-1);
        final int dayOrderClicked=intent.getIntExtra("dayOrderClicked",-1);
        Cursor teacherDetails=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE courseName ='"+ ListActivity.arrayList.get(clickedPosition).toString()+"'",null);
       if(teacherDetails.getCount()!=0)
       { teacherDetails.moveToFirst();

        editTeacher.setText(teacherDetails.getString(0).toString());
        editSubject.setText(teacherDetails.getString(1).toString());
        editSlot.setText(teacherDetails.getString(2));
        editRoom.setText(teacherDetails.getString(3));}

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MainActivity.chosenBatch==1)
                {
               //     MainActivity.sqLiteDatabase.execSQL("UPDATE timetable1 SET Hour"+String.valueOf(clickedPosition+1)+" = '"+editSlot.getText().toString() +"' WHERE dayOrder="+String.valueOf(dayOrderClicked));
                    databaseReference.child(uid).child("TimeTable").child("row"+String.valueOf(dayOrderClicked)).child("Hour"+String.valueOf(clickedPosition+1)).setValue(editSlot.getText().toString());

                }
                if(MainActivity.chosenBatch==2)
                {
             //       MainActivity.sqLiteDatabase.execSQL("UPDATE timetable2 SET Hour"+String.valueOf(clickedPosition+1)+" = '"+editSlot.getText().toString() +"' WHERE dayOrder="+String.valueOf(dayOrderClicked));
                    databaseReference.child(uid).child("TimeTable").child("row"+String.valueOf(dayOrderClicked)).child("Hour"+String.valueOf(clickedPosition+1)).setValue(editSlot.getText().toString());

                }

                Cursor checkTeacher=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE slotName = '"+editSlot.getText().toString()+"'",null);
                if(checkTeacher.getCount()==0)
                {
                 //   MainActivity.sqLiteDatabase.execSQL("INSERT INTO teacherSlots(teacherName,courseName,slotName,roomName) VALUES ('"+editTeacher.getText().toString()+"','"+editSubject.getText().toString()+"','"+editSlot.getText().toString()+"','"+editRoom.getText().toString()+"')");
                    TeacherRow teacherRow=new TeacherRow();
                    teacherRow.setTeacherName(editTeacher.getText().toString());
                    teacherRow.setCourseName(editSubject.getText().toString());
                    teacherRow.setSlotName(editSlot.getText().toString());
                    teacherRow.setRoomName(editRoom.getText().toString());
                    databaseReference.child(uid).child("Teachers").child(editSlot.getText().toString()).setValue(teacherRow);

                }
                else {

//                    MainActivity.sqLiteDatabase.execSQL("UPDATE teacherSlots SET teacherName ='"+editTeacher.getText().toString()+"' WHERE slotName = '"+editSlot.getText().toString()+"'");
//                    MainActivity.sqLiteDatabase.execSQL("UPDATE teacherSlots SET courseName ='"+editSubject.getText().toString()+"' WHERE slotName = '"+editSlot.getText().toString()+"'");
//                    MainActivity.sqLiteDatabase.execSQL("UPDATE teacherSlots SET roomName ='"+editRoom.getText().toString()+"' WHERE slotName = '"+editSlot.getText().toString()+"'");
                    TeacherRow teacherRow=new TeacherRow();
                    teacherRow.setTeacherName(editTeacher.getText().toString());
                    teacherRow.setCourseName(editSubject.getText().toString());
                    teacherRow.setSlotName(editSlot.getText().toString());
                    teacherRow.setRoomName(editRoom.getText().toString());
                    databaseReference.child(uid).child("Teachers").child(editSlot.getText().toString()).setValue(teacherRow);

                }
                ListActivity.arrayList.set(clickedPosition,editSubject.getText().toString());
                ListActivity.arrayAdapter.notifyDataSetChanged();
                EditActivity.this.finish();

            }
        });

    }

}
