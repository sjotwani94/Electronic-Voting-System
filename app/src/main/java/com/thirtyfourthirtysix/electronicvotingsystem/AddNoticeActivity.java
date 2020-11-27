package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;

public class AddNoticeActivity extends AppCompatActivity {
    EditText noticeSubject,noticeDescription;
    ImageView noticeImage;
    ScrollView s1;
    Button submitNotice,savePhoto;
    Uri selectedImage;
    private ProgressDialog progressDialog;
    private String imageDownloadUrl;
    private int status=0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String UserRole = "roleKey";
    public static final String UserName = "nameKey";
    public static final String EMail = "emailKey";
    public static final String PermanentAddress = "permanentAddressKey";
    public static final String CurrentAddress = "currentAddressKey";
    public static final String Photo = "photoKey";
    public static final String Theme = "themeKey";
    public static final String PartyName = "partyKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        noticeSubject=findViewById(R.id.subject_notice);
        noticeImage=findViewById(R.id.image_notice);
        noticeDescription=findViewById(R.id.composed_message);
        s1=findViewById(R.id.scroller);
        savePhoto=findViewById(R.id.save_photo);
        submitNotice=findViewById(R.id.submit);
        progressDialog = new ProgressDialog(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        final String Name = sharedpreferences.getString(UserName,"");
        final String Role = sharedpreferences.getString(UserRole,"");
        final String Party = sharedpreferences.getString(PartyName,"");
        /*if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                s1.setBackgroundResource(R.drawable.navy);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_yellow)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                s1.setBackgroundResource(R.drawable.blackcar);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_black)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#0000FF\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }*/
        noticeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
        savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });
        submitNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeSubject.getText().toString().matches("") || noticeDescription.getText().toString().matches("")
                        || noticeImage.getDrawable() == null){
                    Toast.makeText(AddNoticeActivity.this, "Please Fill Out All Details...", Toast.LENGTH_LONG).show();
                }
                else {
                    String name = Name + " (From "+Party+")";
                    String subject = noticeSubject.getText().toString();
                    String description = noticeDescription.getText().toString();
                    if (status!=1){
                        Toast.makeText(AddNoticeActivity.this, "Please Save Photo", Toast.LENGTH_LONG).show();
                    }else {
                        Log.d("Image Download Link", imageDownloadUrl);
                        NoticeListData noticeDetails = new NoticeListData(name,subject,imageDownloadUrl,description);
                        DatabaseHelper.addNewNotice(noticeDetails);
                        noticeSubject.setText("");
                        noticeDescription.setText("");
                        noticeImage.setImageDrawable(null);
                        Toast.makeText(AddNoticeActivity.this, "Notice Posted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (sharedpreferences.contains(Theme)){
            if (sharedpreferences.getString(Theme,"").matches("Light")){
                s1.setBackgroundResource(R.drawable.navy);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_yellow)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + getSupportActionBar().getTitle() + "</font>")));
            }else if (sharedpreferences.getString(Theme,"").matches("Dark")){
                s1.setBackgroundResource(R.drawable.blackcar);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.simple_black)));
                getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#0000FF\">" + getSupportActionBar().getTitle() + "</font>")));
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                Log.d("Picture Selected", "Picture Path: "+picturePath+"");
                if (ContextCompat.checkSelfPermission(AddNoticeActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddNoticeActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddNoticeActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(AddNoticeActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(AddNoticeActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        ActivityCompat.requestPermissions(AddNoticeActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                }
                noticeImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                cursor.close();
            }
        }
    }

    public String GetFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void UploadImageFileToFirebaseStorage() {
        if (selectedImage != null) {
            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
            StorageReference storageReference2nd = storageReference.child(GetFileName(selectedImage));
            storageReference2nd.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageDownloadUrl = uri.toString();
                                    status++;
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNoticeActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int) p+" % Uploading...");
                        }
                    });
        }
        else {
            Toast.makeText(AddNoticeActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }
}
