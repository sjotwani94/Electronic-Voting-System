package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    TextInputEditText userEmail,userPassword;
    MaterialButton loginVoter,loginCandidate,forgotPwd,registerVoter,registerCandidate;
    ScrollView scrollView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Email = "emailKey";
    public static final String Theme = "themeKey";

    // helper method to open the database error dialog box
    public void alertFirebaseFailure(DatabaseError error) {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollView);
        userEmail = findViewById(R.id.input_user_email);
        userPassword = findViewById(R.id.input_user_pwd);
        loginVoter = findViewById(R.id.login_as_voter);
        loginCandidate = findViewById(R.id.login_as_candidate);
        forgotPwd = findViewById(R.id.forgot_pwd);
        registerVoter = findViewById(R.id.register_voter);
        registerCandidate = findViewById(R.id.register_candidate);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                scrollView.setBackgroundResource(R.drawable.light_green);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_green)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                scrollView.setBackgroundResource(R.drawable.wavy_black);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#00C700\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }

        loginVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Voters");
                final String userEmailID = userEmail.getText().toString();
                final String userPwd = userPassword.getText().toString();
                dbRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int result = DatabaseHelper.fetchLoginDetails(snapshot,userEmailID,userPwd);
                        if (result==0){
                            Snackbar snackbar = Snackbar.make(loginVoter,"Successfully Logged in as Voter",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else if (result==1){
                            Snackbar snackbar = Snackbar.make(loginVoter,"Invalid Credentials for Voter",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else if (result==2){
                            Snackbar snackbar = Snackbar.make(loginVoter,"Voter Not Verified",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        alertFirebaseFailure(error);
                        error.toException();
                    }
                });
            }
        });

        loginCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Candidates");
                final String userEmailID = userEmail.getText().toString();
                final String userPwd = userPassword.getText().toString();
                dbRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int result = DatabaseHelper.fetchLoginDetails(snapshot,userEmailID,userPwd);
                        if (result==0){
                            Snackbar snackbar = Snackbar.make(loginCandidate,"Successfully Logged in as Candidate",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else if (result==1){
                            Snackbar snackbar = Snackbar.make(loginCandidate,"Invalid Credentials for Candidate",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }else if (result==2){
                            Snackbar snackbar = Snackbar.make(loginCandidate,"Candidate Not Verified",Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        alertFirebaseFailure(error);
                        error.toException();
                    }
                });
            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Role");
                dialog.setMessage("What is your role in Electronic Voting System?");
                dialog.setPositiveButton("Voter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this,ForgotPassword.class);
                        intent.putExtra("Role","Voters");
                        startActivity(intent);
                        onResume();
                    }
                });

                dialog.setNegativeButton("Candidate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this,ForgotPassword.class);
                        intent.putExtra("Role","Candidates");
                        startActivity(intent);
                        onResume();
                    }
                });
                dialog.show();
            }
        });

        registerVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VoterRegistration.class);
                startActivity(intent);
            }
        });

        registerCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CandidateRegistration.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                scrollView.setBackgroundResource(R.drawable.light_green);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_green)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                scrollView.setBackgroundResource(R.drawable.wavy_black);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#00C700\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_items1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dark_theme:
                scrollView.setBackgroundResource(R.drawable.wavy_black);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#00C700\">" + getSupportActionBar().getTitle() + "</font>")));
                editor.putString(Theme, "Dark");
                editor.commit();
                break;
            case R.id.light_theme:
                scrollView.setBackgroundResource(R.drawable.light_green);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_green)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
                editor1.putString(Theme, "Light");
                editor1.commit();
                break;
            case R.id.exit:
                System.exit(0);
                break;
        }
        return true;
    }
}
