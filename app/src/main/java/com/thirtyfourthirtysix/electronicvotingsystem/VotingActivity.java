package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VotingActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView electionImage;
    TextView electionType, electionLocation, electionDate;
    private List<PartyListData> functionsList = new ArrayList<>();
    private PartyListAdapter adapter;
    SharedPreferences sharedpreferences;
    List<String> partyLogo = new ArrayList<String>();
    List<String> partyName = new ArrayList<String>();
    List<String> partyType = new ArrayList<String>();
    public static final String mypreference = "mypref";
    public static final String UserRole = "roleKey";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";
    String typeOfElection, stateOfElection, cityOfElection, districtOfElection, dateOfElection;
    ArrayList<ElectionVotes> electionVotes;

    public void alertFirebaseFailure(DatabaseError error) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getApplicationContext())
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Role = sharedpreferences.getString(UserRole,"");
        final String Name = sharedpreferences.getString(UserName,"");
        final String Email = sharedpreferences.getString(EMail,"");
        final String UserPermanentAddress = sharedpreferences.getString(PermanentAddress,"");
        final String UserCurrentAddress = sharedpreferences.getString(CurrentAddress,"");
        String UserPhoto = sharedpreferences.getString(Photo,"");
        Intent intent = getIntent();
        ElectionListData electionListData = (ElectionListData) intent.getSerializableExtra("ElectionDetails");
        typeOfElection = electionListData.getElectionType();
        stateOfElection = electionListData.getElectionState();
        cityOfElection = electionListData.getElectionCity();
        districtOfElection = electionListData.getElectionDistrict();
        dateOfElection = electionListData.getElectionDate();
        electionVotes = electionListData.getElectionVotes();

        boolean voted = false;
        Log.d("Size of Election Votes", String.valueOf(electionVotes.size()));
        for (int i = 0; i < electionVotes.size(); i++){
            String name = electionVotes.get(i).getVoterName();
            if (name.matches(Name)){
                voted = true;
            }
        }

        if (voted) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Already Voted!");
            dialog.setMessage("You have already casted your vote in this election.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (Role.matches("Voter")){
                        Intent intent = new Intent(getApplicationContext(),VoterHomePage.class);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                    }else if (Role.matches("Candidate")){
                        Intent intent = new Intent(getApplicationContext(),CandidateHomePage.class);
                        startActivity(intent);
                        finish();
                        dialogInterface.dismiss();
                    }
                }
            });
            dialog.show();
        }

        recyclerView = findViewById(R.id.recycler_functions);
        electionImage = findViewById(R.id.election_image);
        electionType = findViewById(R.id.election_type);
        electionLocation = findViewById(R.id.election_location);
        electionDate = findViewById(R.id.election_date);

        if (typeOfElection.matches("Assembly Election")){
            electionImage.setImageResource(R.drawable.assembly_election);
        } else if (typeOfElection.matches("By Election")){
            electionImage.setImageResource(R.drawable.by_election);
        } else if (typeOfElection.matches("Corporation Election")){
            electionImage.setImageResource(R.drawable.corporation_election);
        } else if (typeOfElection.matches("Parliament Election")){
            electionImage.setImageResource(R.drawable.parliament_election);
        }
        electionType.setText(typeOfElection);
        electionLocation.setText(stateOfElection+","+cityOfElection+","+districtOfElection);
        electionDate.setText(dateOfElection);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Party");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PartyListData> partyListData = DatabaseHelper.fetchPartyDetails(snapshot);
                for (int i=0;i<partyListData.size();i++){
                    partyLogo.add(partyListData.get(i).getLogoLink());
                    partyName.add(partyListData.get(i).getPartyName());
                    partyType.add(partyListData.get(i).getPartyType());
                }
                for (int len=0;len<partyName.size();len++){
                    functionsList.add(new PartyListData(partyLogo.get(len),partyName.get(len),partyType.get(len)));
                }
                adapter = new PartyListAdapter(functionsList,VotingActivity.this, typeOfElection, stateOfElection, cityOfElection, districtOfElection, dateOfElection, Name);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertFirebaseFailure(error);
                error.toException();
            }
        });
    }
}
