package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CandidateRegistration extends AppCompatActivity implements CandidateGeneralFragment.OnNameSetListener{
    private View contentView;
    private View loadingView;
    private int shortAnimationDuration;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_registration);

        contentView = findViewById(R.id.fragment_two);
        loadingView = findViewById(R.id.fragment_one);
        textView = findViewById(R.id.main_text);

        // Initially hide the content view.
        contentView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    private void crossfade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        contentView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        loadingView.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void setGeneralDetails(String IdentityProof, String Photo, String candidateName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password) {
        CandidatePartyFragment f2= (CandidatePartyFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_two);
        f2.updateInfo(IdentityProof, Photo, candidateName, dateOfBirth, emailID, mobileNumber, permanentAddress, currentAddress, password);
        textView.setText("Party Details");
        crossfade();
    }
}
