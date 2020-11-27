package com.thirtyfourthirtysix.electronicvotingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ElectionListAdapter extends RecyclerView.Adapter<ElectionListAdapter.MyViewHolder> {
    private final Context context;
    private List<ElectionListData> functionsList;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    Activity activity;
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
        TextView electionSubject, electionLocation,electionStatus;
        ImageView electionImage;
        Button castVote;
        MyViewHolder(View view) {
            super(view);
            electionSubject = view.findViewById(R.id.election_subject);
            electionLocation = view.findViewById(R.id.election_location);
            electionStatus = view.findViewById(R.id.election_status);
            electionImage = view.findViewById(R.id.election_image);
            castVote = view.findViewById(R.id.cast_vote);
        }
    }
    public ElectionListAdapter(Context context, List<ElectionListData> functionsList) {
        this.context=context;
        activity = (Activity) context;
        this.functionsList = functionsList;
    }
    @NonNull
    @Override
    public ElectionListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cardview_election,parent,false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ElectionListAdapter.MyViewHolder holder, int position) {
        sharedpreferences = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Role = sharedpreferences.getString(UserRole,"");
        final ElectionListData listData = functionsList.get(position);
        holder.electionSubject.setText(listData.getElectionType());
        holder.electionLocation.setText(listData.getElectionState() + ", " +listData.getElectionCity() +", "+listData.getElectionDistrict());
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String nowdate = simpleDateFormat.format(new Date());
        Date now = null, date = null;
        try {
            now = simpleDateFormat.parse(nowdate);
            date = simpleDateFormat.parse(listData.getElectionDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int comparison = date.compareTo(now);
        if (comparison<=0){
            holder.electionStatus.setText("Started at: "+listData.getElectionDate());
            holder.castVote.setBackgroundResource(R.drawable.cardview_button_active);
        } else {
            holder.electionStatus.setText("Starts at: "+listData.getElectionDate());
            holder.castVote.setBackgroundResource(R.drawable.cardview_button_inactive);
            holder.castVote.setClickable(false);
            holder.castVote.setEnabled(false);
        }
        if (listData.getElectionType().matches("Assembly Election")){
            holder.electionImage.setImageResource(R.drawable.assembly_election);
        } else if (listData.getElectionType().matches("By Election")){
            holder.electionImage.setImageResource(R.drawable.by_election);
        } else if (listData.getElectionType().matches("Corporation Election")){
            holder.electionImage.setImageResource(R.drawable.corporation_election);
        } else if (listData.getElectionType().matches("Parliament Election")){
            holder.electionImage.setImageResource(R.drawable.parliament_election);
        }
        if (listData.getElectionStatus().matches("Ended")){
            holder.castVote.setBackgroundResource(R.drawable.cardview_button_inactive);
            holder.castVote.setClickable(false);
            holder.castVote.setText("This Election Is Over");
            holder.castVote.setEnabled(false);
        }
        holder.castVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElectionListData electionListData = new ElectionListData(listData.getElectionType(),listData.getElectionState(),listData.getElectionCity(),listData.getElectionDistrict(),listData.getElectionDate(),listData.getElectionStatus(),listData.getElectionVotes());
                Intent intent = new Intent(context,VotingActivity.class);
                intent.putExtra("ElectionDetails",electionListData);
                Log.d("Size of Election Votes", String.valueOf(listData.getElectionVotes().size()));
                context.startActivity(intent);
                if (Role.matches("Voter")){
                    ((VoterHomePage)context).finish();
                }else if (Role.matches("Candidate")){
                    ((CandidateHomePage)context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return functionsList.size();
    }
}
