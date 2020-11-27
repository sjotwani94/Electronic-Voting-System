package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    ImageView voteLogo,logo;
    TextView title;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String UserRole = "roleKey";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        voteLogo=findViewById(R.id.vote_logo);
        logo=findViewById(R.id.logo);
        title=findViewById(R.id.app_title);
        Animation anim1 = AnimationUtils.loadAnimation(this,R.anim.alpha);
        Animation anim2 = AnimationUtils.loadAnimation(this,R.anim.scale);
        voteLogo.startAnimation(anim1);
        logo.startAnimation(anim1);
        title.startAnimation(anim2);
        final Intent intent = new Intent(this, MainActivity.class);
        final Intent intent1 = new Intent(this,VoterHomePage.class);
        final Intent intent2 = new Intent(this,CandidateHomePage.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(5000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                    if (sharedpreferences.contains(UserRole) && sharedpreferences.contains(UserName)){
                        if (sharedpreferences.getString(UserRole,"").matches("Voter")){
                            startActivity(intent1);
                            finish();
                        }else if (sharedpreferences.getString(UserRole,"").matches("Candidate")){
                            startActivity(intent2);
                            finish();
                        }
                    }else{
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }
}
