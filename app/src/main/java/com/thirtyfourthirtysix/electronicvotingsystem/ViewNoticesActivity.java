package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewNoticesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    List<String> noticeSender = new ArrayList<String>();
    List<String> noticeSubject = new ArrayList<String>();
    List<String> noticeImage = new ArrayList<String>();
    List<String> noticeDescription = new ArrayList<String>();
    private List<NoticeListData> functionsList = new ArrayList<NoticeListData>();
    private NoticeListAdapter adapter;
    RelativeLayout s1;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String UserRole = "roleKey";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";
    public static final String PartyName = "partyKey";

    public void alertFirebaseFailure(DatabaseError error) {

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getApplicationContext())
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notices);
        s1=findViewById(R.id.scroller);
        recyclerView=findViewById(R.id.list_notices);
        floatingActionButton=findViewById(R.id.fab);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        /*if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                s1.setBackgroundResource(R.drawable.navy);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_yellow)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                s1.setBackgroundResource(R.drawable.blackcar);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_black)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#0000FF\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }*/
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Name = sharedpreferences.getString(UserName,"");
        final String Role = sharedpreferences.getString(UserRole,"");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Notices");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<NoticeListData> noticeDetails = DatabaseHelper.getNoticeDetails(snapshot);
                for (int i=0;i<noticeDetails.size();i++){
                    noticeSender.add(noticeDetails.get(i).getNoticeSender());
                    noticeSubject.add(noticeDetails.get(i).getNoticeSubject());
                    noticeImage.add(noticeDetails.get(i).getNoticeImage());
                    noticeDescription.add(noticeDetails.get(i).getNoticeDescription());
                }
                for (int len=0;len<noticeSender.size();len++){
                    functionsList.add(new NoticeListData(noticeSender.get(len),noticeSubject.get(len),noticeImage.get(len),noticeDescription.get(len)));
                }
                adapter = new NoticeListAdapter(ViewNoticesActivity.this,functionsList,Name,Role);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertFirebaseFailure(error);
                error.toException();
            }
        });

        if (Role.matches("Voter")){
            floatingActionButton.setVisibility(View.GONE);
        }
        else {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),AddNoticeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                s1.setBackgroundResource(R.drawable.navy);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_yellow)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                s1.setBackgroundResource(R.drawable.blackcar);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_black)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#0000FF\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }
    }*/
}
