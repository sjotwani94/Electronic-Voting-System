package com.thirtyfourthirtysix.electronicvotingsystem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.MyViewHolder> {
    private final Context context;
    private List<NoticeListData> functionsList;
    ArrayList<LikesOnNotice> likesOnNotices = new ArrayList<LikesOnNotice>();
    int flag = 0;
    String key;
    Activity activity;
    String userName, userRole;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView noticeSender, noticeSubject,noticeDescription;
        ImageView noticeImage;
        Button leaveALike;
        MyViewHolder(View view) {
            super(view);
            noticeSender = view.findViewById(R.id.notice_sender);
            noticeSubject = view.findViewById(R.id.notice_subject);
            noticeDescription = view.findViewById(R.id.notice_desc);
            noticeImage = view.findViewById(R.id.notice_image);
            leaveALike = view.findViewById(R.id.leave_a_like);
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
    public NoticeListAdapter(Context context, List<NoticeListData> functionsList, String userName, String userRole) {
        this.context=context;
        activity = (Activity) context;
        this.functionsList = functionsList;
        this.userName = userName;
        this.userRole = userRole;
    }
    @NonNull
    @Override
    public NoticeListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cardview_notice,parent,false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final NoticeListAdapter.MyViewHolder holder, int position) {
        final NoticeListData listData = functionsList.get(position);
        holder.noticeSender.setText(listData.getNoticeSender());
        holder.noticeSubject.setText(listData.getNoticeSubject());
        holder.noticeDescription.setText(listData.getNoticeDescription());
        Picasso.get().load(listData.getNoticeImage()).into(holder.noticeImage);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/Likes/"+md5(listData.getNoticeDescription()));
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likesOnNotices = DatabaseHelper.getLikesOnNotice(snapshot);
                LikesOnNotice likesOnNotice = new LikesOnNotice(userName,userRole);
                for (int i = 0;i<likesOnNotices.size();i++){
                    String name = likesOnNotices.get(i).getUserName();
                    String role = likesOnNotices.get(i).getUserRole();
                    if (name.matches(userName) && role.matches(userRole)){
                        flag = 1;
                    }
                }
                if (flag==1){
                    key = DatabaseHelper.retrieveLikeKey(snapshot,userName);
                    holder.leaveALike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red_24dp, 0, 0, 0);
                    holder.leaveALike.setText(String.valueOf(likesOnNotices.size())+" Likes");
                }else{
                    holder.leaveALike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_red_24dp, 0, 0, 0);
                    holder.leaveALike.setText(String.valueOf(likesOnNotices.size())+" Likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                alertFirebaseFailure(error);
                error.toException();
            }
        });
        holder.leaveALike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikesOnNotice likesOnNotice = new LikesOnNotice(userName,userRole);
                if (flag==1){
                    DatabaseHelper.deleteLike(key,md5(listData.getNoticeDescription()));
                    flag = 0;
                    holder.leaveALike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_red_24dp, 0, 0, 0);
                    holder.leaveALike.setText(String.valueOf(likesOnNotices.size())+" Likes");
                }else{
                    DatabaseHelper.addLike(likesOnNotice,md5(listData.getNoticeDescription()));
                    flag = 1;
                    holder.leaveALike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_red_24dp, 0, 0, 0);
                    holder.leaveALike.setText(String.valueOf(likesOnNotices.size())+" Likes");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return functionsList.size();
    }
}