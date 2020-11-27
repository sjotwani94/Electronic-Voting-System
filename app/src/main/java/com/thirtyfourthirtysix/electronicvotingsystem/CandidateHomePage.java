package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CandidateHomePage extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    ScrollView s1;
    RecyclerView recyclerView;
    LinearLayout nav_user;
    SharedPreferences sharedpreferences;
    List<String> electionType = new ArrayList<String>();
    List<String> electionState = new ArrayList<String>();
    List<String> electionCity = new ArrayList<String>();
    List<String> electionDistrict = new ArrayList<String>();
    List<String> electionDate = new ArrayList<String>();
    List<String> electionStatus = new ArrayList<String>();
    List<ArrayList<ElectionVotes>> electionVotes = new ArrayList<ArrayList<ElectionVotes>>();
    private List<ElectionListData> functionsList = new ArrayList<ElectionListData>();
    private ElectionListAdapter adapter;
    public static final String mypreference = "mypref";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";

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
        setContentView(R.layout.activity_candidate_home_page);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.mytext);
        mTitleTextView.setText("Candidate Handle");
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dl = (DrawerLayout)findViewById(R.id.drawer);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        nv = (NavigationView)findViewById(R.id.navigation_view);
        View hView =  nv.getHeaderView(0);
        nav_user = (LinearLayout) hView.findViewById(R.id.nav_layout);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Name = sharedpreferences.getString(UserName,"");
        final String Email = sharedpreferences.getString(EMail,"");
        final String UserPermanentAddress = sharedpreferences.getString(PermanentAddress,"");
        final String UserCurrentAddress = sharedpreferences.getString(CurrentAddress,"");
        String UserPhoto = sharedpreferences.getString(Photo,"");
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.logout:
                        AlertDialog.Builder dialogx = new AlertDialog.Builder(CandidateHomePage.this);
                        dialogx.setTitle("Logout");
                        dialogx.setMessage("Do You really want to logout from the application?");
                        dialogx.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(CandidateHomePage.this, MainActivity.class);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(intent1);
                                Toast.makeText(CandidateHomePage.this, "Logged Out",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                        dialogx.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onResume();
                            }
                        });
                        dialogx.show();
                        break;
                    case R.id.view_campaigning_notices:
                        Intent intent = new Intent(getApplicationContext(),ViewNoticesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.delete_account:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(CandidateHomePage.this);
                        dialog.setTitle("Delete Account");
                        dialog.setMessage("Do You really want to delete your account from the application?");
                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Candidates");
                                dbRef.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Intent int1=new Intent(CandidateHomePage.this,MainActivity.class);
                                        String userkey = DatabaseHelper.retrieveKey(snapshot,Email);
                                        DatabaseHelper.deleteCandidate(userkey);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.clear();
                                        editor.apply();
                                        Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_LONG).show();
                                        startActivity(int1);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        alertFirebaseFailure(error);
                                        error.toException();
                                    }
                                });
                            }
                        });
                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onResume();
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        View header = nv.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.username);
        ImageView imageView = (ImageView) header.findViewById(R.id.userphoto);
        text.setText(Name+" ("+Email+")");
        Picasso.get().load(UserPhoto).into(imageView);
        s1=findViewById(R.id.scrollview);
        recyclerView=findViewById(R.id.list_elections);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Election");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ElectionListData> electionListData = DatabaseHelper.fetchElections(snapshot,UserPermanentAddress,UserCurrentAddress);
                for (int i=0;i<electionListData.size();i++){
                    electionType.add(electionListData.get(i).getElectionType());
                    electionState.add(electionListData.get(i).getElectionState());
                    electionCity.add(electionListData.get(i).getElectionCity());
                    electionDistrict.add(electionListData.get(i).getElectionDistrict());
                    electionDate.add(electionListData.get(i).getElectionDate());
                    electionStatus.add(electionListData.get(i).getElectionStatus());
                    electionVotes.add(electionListData.get(i).getElectionVotes());
                }
                for (int len=0;len<electionType.size();len++){
                    functionsList.add(new ElectionListData(electionType.get(len),electionState.get(len),electionCity.get(len),electionDistrict.get(len),electionDate.get(len),electionStatus.get(len),electionVotes.get(len)));
                }
                adapter = new ElectionListAdapter(CandidateHomePage.this,functionsList);
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
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Logout");
        dialog.setMessage("Do You really want to logout from the application?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1 = new Intent(CandidateHomePage.this, MainActivity.class);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(intent1);
                Toast.makeText(CandidateHomePage.this, "Logged Out",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onResume();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        /*switch (item.getItemId()){
            case R.id.dark_theme:
                s1.setBackgroundResource(R.drawable.blackcar);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_black)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#0000FF\">" + getSupportActionBar().getTitle() + "</font>")));
                editor.putString(Theme, "Dark");
                editor.commit();
                nav_user.setBackgroundColor(getResources().getColor(R.color.simple_black));
                break;
            case R.id.light_theme:
                s1.setBackgroundResource(R.drawable.navy);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_yellow)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
                editor1.putString(Theme, "Light");
                editor1.commit();
                nav_user.setBackgroundColor(getResources().getColor(R.color.simple_yellow));
                break;
            case R.id.exit:
                System.exit(0);
                break;
        }*/
        return true;
    }
}
