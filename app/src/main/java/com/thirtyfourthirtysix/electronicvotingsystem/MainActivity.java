package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    TextInputEditText userEmail,userPassword;
    MaterialButton login,forgotPwd,registerVoter,registerCandidate;
    ScrollView scrollView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Email = "emailKey";
    public static final String Theme = "themeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollView);
        userEmail = findViewById(R.id.input_user_email);
        userPassword = findViewById(R.id.input_user_pwd);
        login = findViewById(R.id.login);
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

        registerVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VoterRegistration.class);
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
