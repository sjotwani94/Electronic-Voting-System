package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

public class ForgotPassword extends AppCompatActivity implements GetEmailFragment.OnNameSetListener, GetOtpFragment.OnNameSetListener{
    private View emailFragment,otpFragment,resetFragment;
    TextView mainText;
    public String role;
    private int shortAnimationDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailFragment = findViewById(R.id.fragment_one);
        otpFragment = findViewById(R.id.fragment_two);
        resetFragment = findViewById(R.id.fragment_three);
        mainText = findViewById(R.id.main_text);
        role = getIntent().getStringExtra("Role");
        Log.d("User Role", role);
        otpFragment.setVisibility(View.GONE);
        resetFragment.setVisibility(View.GONE);
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void animateToOTP() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        otpFragment.setAlpha(0f);
        otpFragment.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        otpFragment.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        emailFragment.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        emailFragment.setVisibility(View.GONE);
                    }
                });
    }

    private void animateToResetPassword() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        resetFragment.setAlpha(0f);
        resetFragment.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        resetFragment.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        otpFragment.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        otpFragment.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void setGeneralDetails(String emailID, int otp) {
        GetOtpFragment f2= (GetOtpFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_two);
        f2.updateInfo(emailID,otp);
        mainText.setText("Enter OTP");
        animateToOTP();
    }

    @Override
    public void setUserEmailID(String emailID) {
        ResetPasswordFragment f3 = (ResetPasswordFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_three);
        f3.updateEmail(emailID);
        mainText.setText("Reset Password");
        animateToResetPassword();
    }
}
