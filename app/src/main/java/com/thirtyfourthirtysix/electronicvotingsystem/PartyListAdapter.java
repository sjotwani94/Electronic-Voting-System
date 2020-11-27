package com.thirtyfourthirtysix.electronicvotingsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PartyListAdapter extends RecyclerView.Adapter<PartyListAdapter.MyViewHolder> {
    private List<PartyListData> functionsList;
    String electionType, electionState, electionCity, electionDistrict, electionDate, voterName;
    Context context;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String UserRole = "roleKey";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";
    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView partyLogo;
        TextView partyName, partyType;
        LinearLayout partyDetails;
        MyViewHolder(View view) {
            super(view);
            partyLogo = view.findViewById(R.id.party_logo);
            partyName = view.findViewById(R.id.name_of_party);
            partyType = view.findViewById(R.id.type_of_party);
            partyDetails = view.findViewById(R.id.party_container);
        }
    }
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
    public void alertFirebaseFailure(DatabaseError error) {

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context)
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }
    public PartyListAdapter(List<PartyListData> functionsList, Context context, String electionType, String electionState, String electionCity, String electionDistrict, String electionDate, String voterName) {
        this.functionsList = functionsList;
        this.context = context;
        this.electionType = electionType;
        this.electionState = electionState;
        this.electionCity = electionCity;
        this.electionDistrict = electionDistrict;
        this.electionDate = electionDate;
        this.voterName = voterName;
    }
    @NonNull
    @Override
    public PartyListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.party_detail,parent,false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PartyListAdapter.MyViewHolder holder, int position) {
        sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Role = sharedpreferences.getString(UserRole,"");
        final PartyListData listData = functionsList.get(position);
        Picasso.get().load(listData.getLogoLink()).into(holder.partyLogo);
        holder.partyName.setText(listData.getPartyName());
        holder.partyType.setText(listData.getPartyType());
        holder.partyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ElectionVotes electionVotes = new ElectionVotes(voterName, md5(listData.getPartyName()));
                final View view = v;
                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
                dialog.setTitle("Place Your Vote?");
                dialog.setMessage("Are you sure you want to give your vote to "+listData.getPartyName()+"? Once vote placed cannot be undone.");
                dialog.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.setNegativeButton("Place Vote", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        boolean status = DatabaseHelper.setVote(electionType,electionState,electionCity,electionDistrict,electionDate,electionVotes);
                        if (status){
                            if (Role.matches("Voter")){
                                Intent intent = new Intent(context,VoterHomePage.class);
                                Toast.makeText(context, "Vote Placed Successfully!", Toast.LENGTH_LONG).show();
                                context.startActivity(intent);
                                ((VotingActivity)context).finish();
                            }else if (Role.matches("Candidate")){
                                Intent intent = new Intent(context,CandidateHomePage.class);
                                Toast.makeText(context, "Vote Placed Successfully!", Toast.LENGTH_LONG).show();
                                context.startActivity(intent);
                                ((VotingActivity)context).finish();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                try {
                    dialog.show();
                }
                catch (WindowManager.BadTokenException e) {
                    Log.d("Error", "Sorry");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return functionsList.size();
    }
}
