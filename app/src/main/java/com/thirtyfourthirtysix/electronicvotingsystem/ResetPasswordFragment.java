package com.thirtyfourthirtysix.electronicvotingsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {
    String EmailID;
    TextInputEditText newPassword,confirmPassword;
    MaterialButton submit;
    public ForgotPassword forgotPassword;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    // helper method to open the database error dialog box
    public void alertFirebaseFailure(DatabaseError error) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        //forgotPassword = (ForgotPassword) getActivity();
        final String userRole = getActivity().getIntent().getStringExtra("Role");
        newPassword = view.findViewById(R.id.new_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().matches(confirmPassword.getText().toString())){
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/"+userRole);
                    dbRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent int1=new Intent(getActivity(),MainActivity.class);
                            String userkey = DatabaseHelper.retrieveKey(snapshot,EmailID);
                            DatabaseHelper.updateUserPassword(userRole,userkey,newPassword.getText().toString());
                            Toast.makeText(getActivity().getApplicationContext(), "Password Reset Successful!", Toast.LENGTH_LONG).show();
                            startActivity(int1);
                            getActivity().finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            alertFirebaseFailure(error);
                            error.toException();
                        }
                    });
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Passwords don't match.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public void updateEmail(String emailID){
        EmailID=emailID;
    }

}
