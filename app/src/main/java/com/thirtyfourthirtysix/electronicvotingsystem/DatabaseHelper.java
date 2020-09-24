package com.thirtyfourthirtysix.electronicvotingsystem;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public final class DatabaseHelper {

    /*public static final void buildIssuesList(@NonNull DataSnapshot snapshot, ArrayList<VoterDetails> voterDetails) {

        voterDetails.clear();  // clear the old data

        for (DataSnapshot voter : snapshot.getChildren()) {

            // retrieve the key
            String key = voter.getKey();
            String VoterName = voter.child("VoterName").getValue(String.class);
            String DateOfBirth = voter.child("DateOfBirth").getValue(String.class);
            String EmailID = voter.child("EmailID").getValue(String.class);
            long MobileNumber = voter.child("MobileNumber").getValue(Long.class);
            String PermanentAddress = voter.child("PermanentAddress").getValue(String.class);
            String CurrentAddress = voter.child("CurrentAddress").getValue(String.class);
            String Password = voter.child("Password").getValue(String.class);

            // make a new Issue object and add it to the ArrayList
            voterDetails.add(new VoterDetails(key,VoterName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,Password));
        }

    }*/

    public static final void addNewVoter(VoterDetails newVoter) {

        // use the reference to root node /issues
        // Task 5: build the reference and call it rootRef, the path is /issues"
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Voters");
        // to push a new child node and get the reference to it
        DatabaseReference newIssueRef = rootRef.push();
        // set the children nodes of this new reference to our object's properties
        newIssueRef.setValue(newVoter);
    }

    public static final void addNewCandidate(CandidateDetails newCandidate) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Candidates");
        DatabaseReference newIssueRef = rootRef.push();
        newIssueRef.setValue(newCandidate);
    }

    public static final void deleteVoter(String keyToDelete) {

        // get a reference to the issue child node to be deleted
        // Task 6: build the reference and call it issueRef, the path is /issues/<keyToDelete>"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/Voters/" + keyToDelete);
        // remove the child node and its children
        issueRef.removeValue();
    }

    public static final void updateVoter(String issueKey, String newStatus) {

        // get a reference to the child node to be updated and to the "resolved" child node
        // Task 7: build the reference and call it issueRef, the path is /issues/<issueKey>/resolved"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/Voters/" + issueKey + "/resolved");
        // set the value of the node
        issueRef.setValue(newStatus);
    }
}

