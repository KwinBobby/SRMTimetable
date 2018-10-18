package com.knby.srmtimetable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    Intent intent;
    static ArrayAdapter arrayAdapter;
    ListView listView;
    Cursor chosenTimeTable;
    Cursor chosenSlot;
    static ArrayList arrayList;
    int tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        String slot="";
        int index;

        listView=(ListView)findViewById(R.id.listView);
        intent=getIntent();
       tag=intent.getIntExtra("tag",-1);
        /*if(tag==-1)
            tag=DetailsActivity.ListActivityTag;
        else
        DetailsActivity.ListActivityTag=tag;*/

        if(MainActivity.chosenBatch==1)
           chosenTimeTable=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM timetable1 WHERE dayOrder IN (SELECT dayOrder FROM timetable1  WHERE dayOrder ="+String.valueOf(tag)+" );",null);
        else if(MainActivity.chosenBatch==2)
           chosenTimeTable=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM timetable2 WHERE dayOrder IN (SELECT dayOrder FROM timetable2  WHERE dayOrder ="+String.valueOf(tag)+" );",null);

        arrayList=new ArrayList();
        chosenTimeTable.moveToFirst();



        while (chosenTimeTable!=null)
        {
            for(int i=0;i<10;i++)
            {
                index=chosenTimeTable.getColumnIndex("Hour"+String.valueOf(i+1));
                slot=String.valueOf(chosenTimeTable.getString(index));
                chosenSlot=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE slotName='"+slot+"'",null);

                if(chosenSlot.getCount()==0)
                    arrayList.add(slot);
                else
                {
                    chosenSlot.moveToFirst();
                    arrayList.add(chosenSlot.getString(1));
                }
            }
            if(chosenTimeTable.moveToNext()==false)break;
        }
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!arrayList.get(position).equals("Free")&&!arrayList.get(position).equals("Lunch")&&!arrayList.get(position).equals("LAB")){

                   details(position);

            }
                }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(!arrayList.get(position).equals("Lunch"))
                {
                    new AlertDialog.Builder(ListActivity.this)
                            .setTitle("Options")
                            .setItems(new CharSequence[]{"Add/Edit Homework", "Edit Slot", "Cancel"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(which==0){
                                        intent=new Intent(ListActivity.this,NotesActivity.class);
                                        intent.putExtra("clickedPosition",position);
                                        startActivity(intent);
                                    }
                                    else if(which==1)
                                        new AlertDialog.Builder(ListActivity.this)
                                                .setTitle("Edit")
                                                .setMessage("Are you sure you want to edit?")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        intent=new Intent(getApplicationContext(),EditActivity.class);
                                                        intent.putExtra("clickedPosition",position);
                                                        intent.putExtra("dayOrderClicked",tag);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("No",null)
                                                .show();
                                }
                            }).show();
               }
                return true;
            }
        });




    }
    void details(int position)
    {

        MainActivity.goneToDetails=true;
         LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.details, null);
                 TextView teacherName=(TextView)dialogLayout.findViewById(R.id.teacherName);
                 TextView subjectName=(TextView)dialogLayout.findViewById(R.id.subjectName);
                 TextView slotName=(TextView)dialogLayout.findViewById(R.id.slotName);
                 TextView roomName=(TextView)dialogLayout.findViewById(R.id.roomName);
                 TextView timeName=(TextView)dialogLayout.findViewById(R.id.timeName);
                 TextView teacher=(TextView)dialogLayout.findViewById(R.id.teacher);
               // TextView textView=(TextView)dialogLayout.findViewById(R.id.textView);
              //  textView.setText("Changedy");



        ArrayList time=new ArrayList();
        time.add("8:00 - 8:50");
        time.add("8:50 - 9:40");
        time.add("9:45 - 10:35");
        time.add("10:40 - 11:30");
        time.add("11:35 - 12:25");
        time.add("12:30 - 1:20");
        time.add("1:25 - 2:15");
        time.add("2:20 - 3:10");
        time.add("3:15 - 4:05");
        time.add("4:05 - 4:55");




        int positionClicked=position;
        Cursor teacherDetails=MainActivity.sqLiteDatabase.rawQuery("SELECT * FROM teacherSlots WHERE courseName ='"+ ListActivity.arrayList.get(positionClicked).toString()+"'",null);
        teacherDetails.moveToFirst();


        if(teacherDetails.getString(0).toString().isEmpty())
        {
            teacher.setVisibility(View.GONE);
            teacherName.setVisibility(View.GONE);
        }
        else
            teacherName.setText(teacherDetails.getString(0).toString());

        subjectName.setText(teacherDetails.getString(1).toString());
        slotName.setText(teacherDetails.getString(2));
        roomName.setText(teacherDetails.getString(3));
        timeName.setText(time.get(positionClicked).toString());


                AlertDialog.Builder builder=new AlertDialog.Builder(ListActivity.this);
                builder.setView(dialogLayout);


                builder.setTitle("Details");
                builder.setPositiveButton("OK",null).create()
                        .show();

    }
}
