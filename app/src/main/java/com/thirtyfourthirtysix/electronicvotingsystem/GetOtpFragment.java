package com.thirtyfourthirtysix.electronicvotingsystem;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetOtpFragment extends Fragment {
    TextInputEditText UserOTP;
    MaterialButton confirmOTP;
    OnNameSetListener onNameSetListener;
    //public ForgotPassword forgotPassword;
    String EmailID;
    int OTP;
    public GetOtpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_otp, container, false);
        //forgotPassword = (ForgotPassword) getActivity();
        final String userRole = getActivity().getIntent().getStringExtra("Role");
        UserOTP = view.findViewById(R.id.get_otp);
        confirmOTP = view.findViewById(R.id.submit);

        confirmOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(UserOTP.getText().toString())==OTP){
                    Snackbar snackbar = Snackbar.make(confirmOTP,"OTP Verified.",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    onNameSetListener.setUserEmailID(EmailID);
                } else {
                    Snackbar snackbar = Snackbar.make(confirmOTP,"Invalid OTP. Please Try Again.",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        try {
            onNameSetListener = (OnNameSetListener) activity;
        }
        catch(Exception e){
        }
    }

    public void updateInfo(String emailID, int otp){
        EmailID=emailID;
        OTP=otp;
    }

    public interface OnNameSetListener {
        public void setUserEmailID(String emailID);
    }
}
