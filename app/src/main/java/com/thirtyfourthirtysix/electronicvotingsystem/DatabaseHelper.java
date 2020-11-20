package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public final class DatabaseHelper {

    public static String md5(String input) {

        String md5 = null;

        if(null == input) return null;

        try {

            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());

            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }

    public static final int fetchLoginDetails(@NonNull DataSnapshot snapshot, String EmailID, String pwd) {
        for (DataSnapshot voter : snapshot.getChildren()) {
            String key = voter.getKey();
            String ID = null;
            if (voter.hasChild("voterID")){
                ID = voter.child("voterID").getValue(String.class);
            }else if (voter.hasChild("candidateID")){
                ID = voter.child("candidateID").getValue(String.class);
            }
            String emailID = voter.child("emailID").getValue(String.class);
            String Password = voter.child("password").getValue(String.class);
            if (emailID.matches(EmailID) && Password.matches(md5(pwd))){
                if (ID.matches("")){
                    return 2;
                } else {
                    return 0;
                }
            }
        }
        return 1;
    }

    public static final String retrieveKey(@NonNull DataSnapshot snapshot, String EmailID) {
        for (DataSnapshot user : snapshot.getChildren()) {
            String key = user.getKey();
            String emailID = user.child("emailID").getValue(String.class);
            if (emailID.matches(EmailID)){
                return key;
            }
        }
        return null;
    }

    public static final boolean verifyEmailID(@NonNull DataSnapshot snapshot, String EmailID) {
        for (DataSnapshot voter : snapshot.getChildren()) {
            String emailID = voter.child("emailID").getValue(String.class);
            if (emailID.matches(EmailID)){
                return true;
            }
        }
        return false;
    }

    public static final ArrayList<String> fetchPartyNames(@NonNull DataSnapshot snapshot){
        ArrayList<String> partyNames = new ArrayList<String>();
        for (DataSnapshot voter : snapshot.getChildren()) {
            partyNames.add(voter.child("PartyName").getValue(String.class));
        }
        return partyNames;
    }

    public static final void addNewVoter(VoterDetails newVoter) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Voters");
        DatabaseReference newIssueRef = rootRef.push();
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

    public static final void updateUserPassword(String userRole, String issueKey, String newPassword) {

        // get a reference to the child node to be updated and to the "resolved" child node
        // Task 7: build the reference and call it issueRef, the path is /issues/<issueKey>/resolved"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/"+userRole+"/" + issueKey + "/password");
        DatabaseReference ref = null;
        if (userRole.matches("Voters")){
            ref = FirebaseDatabase.getInstance().getReference("/Voters/" + issueKey + "/voterID");
        } else if (userRole.matches("Candidates")){
            ref = FirebaseDatabase.getInstance().getReference("/Candidates/" + issueKey + "/candidateID");
        }
        // set the value of the node
        issueRef.setValue(md5(newPassword));
        ref.setValue("");
    }
}

