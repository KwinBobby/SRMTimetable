package com.knby.srmtimetable;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.knby.srmtimetable.classes.NotesRow;

public class NotesActivity extends AppCompatActivity {

    DatabaseReference databaseReference,notesReference;
    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    ChildEventListener NotesEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
       // Button button=(Button)findViewById(R.id.button);
        FloatingActionButton fabDone=(FloatingActionButton)findViewById(R.id.fabDone);
        final EditText notesText=(EditText)findViewById(R.id.editText);
        //String saveString;

        databaseReference=FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        final String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseAuth = FirebaseAuth.getInstance();
        MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
        Intent intent=getIntent();
        int positionClicked=intent.getIntExtra("clickedPosition",-1);
        final Cursor teacherDetails=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE courseName ='"+ ListActivity.arrayList.get(positionClicked).toString()+"'",null);
        teacherDetails.moveToFirst();
        final Cursor noteDetails=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM notes WHERE slot ='"+teacherDetails.getString(2)+"'",null);
       // Log.i("Error","SELECT * FROM notes WHERE slot ='"+teacherDetails.getString(3)+"'");
        noteDetails.moveToFirst();

        if(noteDetails.getCount()!=0) {
          //  Log.i("Error","Stuck here");
            notesText.setText(noteDetails.getString(1));

        }


         NotesEventListener=new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("Dank","NotesEventListener123");
                //            Log.e("Dank",rowSnapshot.getValue(TimeTableRow.class).getHour2().toString());
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
                try {
                    MainActivity.sqLiteDatabase.execSQL("INSERT INTO notes(slot,note) VALUES ('" + notesRow.getSlot() + "','" + notesRow.getNote() + "')");
                }
                catch (Exception e)
                {
                    Log.e("The error","could not insert "+notesRow.toString());
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                MainActivity. sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");

                MainActivity. sqLiteDatabase.execSQL("UPDATE notes SET note = '"+notesRow.getNote()+"' WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+notesRow.getSlot()+"');");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                NotesRow notesRow=dataSnapshot.getValue(NotesRow.class);
                MainActivity.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes(slot VARCHAR PRIMARY KEY,note VARCHAR)");
                MainActivity.sqLiteDatabase.execSQL("DELETE FROM notes WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+notesRow.getSlot()+"');");

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        notesReference = mFirebaseDatabase.getReference(uid+"/Notes");
        notesReference.addChildEventListener(NotesEventListener);


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String saveString=notesText.getText().toString();
                if(saveString.isEmpty())
                {
                    Log.e("Dank","deletion over here");
//                    MainActivity.sqLiteDatabase.execSQL("DELETE FROM notes WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+teacherDetails.getString(2)+"');");
                    databaseReference.child(uid).child("Notes").child(teacherDetails.getString(2)).removeValue();
                    Log.e("Dank","deletion done here");
                }
                else if(noteDetails.getCount()==0)

                {
//                    MainActivity.sqLiteDatabase.execSQL("INSERT INTO notes(slot,note) VALUES ('"+teacherDetails.getString(2)+"','"+saveString+"')");
                    NotesRow notesRow=new NotesRow();
                    notesRow.setSlot(teacherDetails.getString(2));
                    notesRow.setNote(saveString);
                    databaseReference.child(uid).child("Notes").child(teacherDetails.getString(2)).setValue(notesRow);
                }
                else
                {
                    Log.e("Dank","over here");
//                    MainActivity.sqLiteDatabase.execSQL("UPDATE notes SET note = '"+saveString+"' WHERE slot IN (SELECT slot FROM notes WHERE slot = '"+teacherDetails.getString(2)+"');");
                    Log.e("Dank","done here");
                    NotesRow notesRow=new NotesRow();
                    notesRow.setSlot(teacherDetails.getString(2));
                    notesRow.setNote(saveString);
                    databaseReference.child(uid).child("Notes").child(teacherDetails.getString(2)).setValue(notesRow);
                }



                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                NotesActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        notesReference.removeEventListener(NotesEventListener);
    }
}
