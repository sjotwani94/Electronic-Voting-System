package com.thirtyfourthirtysix.electronicvotingsystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class VoterRegistration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextInputEditText userName,userDOB,userEmail,userMobile,permanentAddress,currentAddress,permanentPinCode,currentPinCode,userPassword,confirmPassword,documentName;
    AutoCompleteTextView permanentCity,permanentState,currentCity,currentState;
    MaterialButton uploadPhoto,uploadDocument,imgUploader,docUploader,submit;
    ImageView uploadedImage;
    private Calendar mCalendar;
    private int mYear, mMonth, mDay;
    private String mDate;
    private AlertDialog alertDialog = null;
    private ArrayList<VoterDetails> voterDetail = new ArrayList<VoterDetails>();
    private DatabaseReference RootRef;
    private StorageTask uploadTask;
    private Uri fileuri,photouri;
    private String imageDownloadUrl,documentDownloadUrl;
    private ProgressDialog progressDialog,anotherProgressDialog;
    private int status=0;

    // helper method to open the database error dialog box
    public void alertFirebaseFailure(DatabaseError error) {

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("An error occurred while connecting to Firebase!")
                .setMessage(error.toString())
                .setPositiveButton("Dismiss", null)
                .setIcon(android.R.drawable.presence_busy)
                .show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voter_registration);
        userName = findViewById(R.id.input_user_name);
        userDOB = findViewById(R.id.input_dob);
        userEmail = findViewById(R.id.input_user_email);
        userMobile = findViewById(R.id.input_mobile_number);
        permanentAddress = findViewById(R.id.input_permanent_address);
        currentAddress = findViewById(R.id.input_current_address);
        permanentPinCode = findViewById(R.id.input_permanent_pin);
        currentPinCode = findViewById(R.id.input_current_pin);
        userPassword = findViewById(R.id.input_user_pwd);
        confirmPassword = findViewById(R.id.confirm_user_pwd);
        permanentCity = findViewById(R.id.permanent_city_dropdown);
        permanentState = findViewById(R.id.permanent_state_dropdown);
        currentCity = findViewById(R.id.current_city_dropdown);
        currentState = findViewById(R.id.current_state_dropdown);
        uploadPhoto = findViewById(R.id.upload_photo);
        submit = findViewById(R.id.submit);
        uploadedImage = findViewById(R.id.uploaded_img);
        documentName = findViewById(R.id.input_document_name);
        uploadDocument = findViewById(R.id.pick_document);
        imgUploader = findViewById(R.id.img_uploader);
        docUploader = findViewById(R.id.doc_uploader);
        progressDialog = new ProgressDialog(this);
        anotherProgressDialog = new ProgressDialog(this);
        RootRef= FirebaseDatabase.getInstance().getReference();

        try {
            // this will send any local changes we make when we are offline to Firebase when
            // we are online again
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        catch (Exception e) {
            // ignore this because it will throw an exception when the device is rotated
            // and this method is called a second time
        }

        ArrayAdapter<String> states =
                new ArrayAdapter<String>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.india_states));
        permanentState.setAdapter(states);
        currentState.setAdapter(states);

        ArrayAdapter<String> cities =
                new ArrayAdapter<String>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.india_top_cities));
        permanentCity.setAdapter(cities);
        currentCity.setAdapter(cities);

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(VoterRegistration.this);
                dialog.setTitle("Upload Photo");
                dialog.setMessage("How do you want to upload your photo?");
                dialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isPermissionGranted(1)) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1);
                        }
                        onResume();
                    }
                });

                dialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                        onResume();
                    }
                });
                dialog.show();
            }
        });

        uploadDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(VoterRegistration.this);
                dialog.setTitle("Upload Document");
                dialog.setMessage("In which format do you want to upload proof of identity?");
                dialog.setPositiveButton("PDF", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent.createChooser(intent,"Select PDF File"),3);
                        onResume();
                    }
                });

                dialog.setNegativeButton("DOCX", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/msword");
                        startActivityForResult(intent.createChooser(intent,"Select MS Word File"),4);
                        onResume();
                    }
                });
                dialog.show();
            }
        });

        imgUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });

        docUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadDocumentFileToFirebaseStorage();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int1=new Intent(VoterRegistration.this,MainActivity.class);
                LayoutInflater li1=getLayoutInflater();
                View layout=li1.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));
                TextView txt= (TextView) layout.findViewById(R.id.text_toast);
                txt.setText("Registration Successful, You Can Login once Admin verifies you!");
                String VoterName = userName.getText().toString();
                String DateOfBirth = userDOB.getText().toString();
                String EmailID = userEmail.getText().toString().trim();
                long MobileNumber = Long.parseLong(userMobile.getText().toString());
                String PermanentAddress = permanentAddress.getText().toString() + ", " + permanentCity.getText().toString() + ", " + permanentState.getText().toString() + " - " + permanentPinCode.getText().toString();
                String CurrentAddress = currentAddress.getText().toString() + ", " + currentCity.getText().toString() + ", " + currentState.getText().toString() + " - " + currentPinCode.getText().toString();
                String Password = userPassword.getText().toString();
                String ConfirmPassword = confirmPassword.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // here set the pattern as you date in string was containing like date/month/year
                Date dob = null;
                try {
                    dob = sdf.parse(DateOfBirth);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String strDate = sdf.format(new Date());
                Date date = null;
                try {
                    date = sdf.parse(strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long userAge = (date.getTime() - dob.getTime()) / 86400000 / 365;
                Log.d("User Age", String.valueOf(userAge));
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (userAge < 18){
                    Toast.makeText(getApplicationContext(), "You Are Not Eligible to Vote!", Toast.LENGTH_LONG).show();
                }else if (!EmailID.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(), "Invalid E-Mail Address...", Toast.LENGTH_LONG).show();
                }else if (String.valueOf(MobileNumber).length()!=10){
                    Toast.makeText(getApplicationContext(), "Mobile Number Should have 10 Digits...", Toast.LENGTH_LONG).show();
                }else if (VoterName.matches("") || CurrentAddress.matches("") || PermanentAddress.matches("")){
                    Toast.makeText(getApplicationContext(), "Some Field is Empty!", Toast.LENGTH_LONG).show();
                }else if (!Password.matches(ConfirmPassword)){
                    Toast.makeText(getApplicationContext(), "Passwords Don't Match!", Toast.LENGTH_LONG).show();
                }else if (status!=2){
                    Toast.makeText(VoterRegistration.this, "Please Save Photo & Document", Toast.LENGTH_LONG).show();
                }else {
                    Log.d("Image Download Link", imageDownloadUrl);
                    Log.d("Document Download Link", documentDownloadUrl);
                    VoterDetails voterDetails = new VoterDetails(documentDownloadUrl,imageDownloadUrl,VoterName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,md5(Password));
                    DatabaseHelper.addNewVoter(voterDetails);
                    Toast toast=new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    startActivity(int1);
                    finish();
                }
            }
        });
    }

    public boolean isPermissionGranted(int no){
        if(Build.VERSION.SDK_INT>22)
        {
            if(no==1)
            {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        else {
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photouri = data.getData();
                uploadedImage.setImageBitmap(photo);
            } else if (requestCode == 2) {
                photouri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(photouri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bmp = null;
                try {
                    bmp = getBitmapFromUri(photouri);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                uploadedImage.setImageBitmap(bmp);
            } else if (requestCode == 3) {
                fileuri = data.getData();
                documentName.setText(GetFileName(fileuri));
                documentName.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_pdf_svg),null,null,null);
            } else if (requestCode == 4) {
                fileuri = data.getData();
                documentName.setText(GetFileName(fileuri));
                documentName.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_docx_icon),null,null,null);
            }
        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    // Creating Method to get the selected image file Extension from File Path URI.
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

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (photouri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(userName.getText().toString().trim() + "." + GetFileExtension(photouri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(photouri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
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
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(VoterRegistration.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int) p+" % Uploading...");

                        }
                    });
        }
        else {
            Toast.makeText(VoterRegistration.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }

    public void UploadDocumentFileToFirebaseStorage() {
        // Checking whether FilePathUri Is empty or not.
        if (fileuri != null) {

            // Setting progressDialog Title.
            anotherProgressDialog.setTitle("Document is Uploading...");

            // Showing progressDialog.
            anotherProgressDialog.show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(userName.getText().toString().trim() + "." + GetFileExtension(fileuri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Hiding the progressDialog after done uploading.
                            anotherProgressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Document Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    documentDownloadUrl = uri.toString();
                                    status++;
                                }
                            });
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            anotherProgressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(VoterRegistration.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            anotherProgressDialog.setMessage((int) p+" % Uploading...");
                        }
                    });
        }
        else {
            Toast.makeText(VoterRegistration.this, "Please Select A Document", Toast.LENGTH_LONG).show();
        }
    }

    public void setDate(View v){
        Calendar mcurrentDate = Calendar.getInstance();
        int mmYear = mcurrentDate.get(Calendar.YEAR);
        int mmMonth = mcurrentDate.get(Calendar.MONTH);
        int mmDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(VoterRegistration.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear ++;
                mDay = dayOfMonth;
                mMonth = monthOfYear;
                mYear = year;
                mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                userDOB.setText(mDate);
            }
        }, mmYear, mmMonth, mmDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month ++;
        mDay = dayOfMonth;
        mMonth = month;
        mYear = year;
        mDate = dayOfMonth + "/" + month + "/" + year;
        userDOB.setText(mDate);
    }
}