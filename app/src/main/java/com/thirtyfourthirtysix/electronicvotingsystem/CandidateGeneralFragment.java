package com.thirtyfourthirtysix.electronicvotingsystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CandidateGeneralFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CandidateGeneralFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    TextInputEditText userName,userDOB,userEmail,userMobile,permanentAddress,currentAddress,permanentPinCode,currentPinCode,userPassword,confirmPassword,documentName;
    AutoCompleteTextView permanentCity,permanentState,currentCity,currentState;
    MaterialButton uploadPhoto,uploadDocument,imgUploader,docUploader;
    FloatingActionButton submit;
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
    OnNameSetListener onNameSetListener;

    public CandidateGeneralFragment() {
        // Required empty public constructor
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_one_layout, container, false);
        View view =inflater.inflate(R.layout.fragment_candidate_general,container,false);
        userName = view.findViewById(R.id.input_user_name);
        userDOB = view.findViewById(R.id.input_dob);
        userEmail = view.findViewById(R.id.input_user_email);
        userMobile = view.findViewById(R.id.input_mobile_number);
        permanentAddress = view.findViewById(R.id.input_permanent_address);
        currentAddress = view.findViewById(R.id.input_current_address);
        permanentPinCode = view.findViewById(R.id.input_permanent_pin);
        currentPinCode = view.findViewById(R.id.input_current_pin);
        userPassword = view.findViewById(R.id.input_user_pwd);
        confirmPassword = view.findViewById(R.id.confirm_user_pwd);
        permanentCity = view.findViewById(R.id.permanent_city_dropdown);
        permanentState = view.findViewById(R.id.permanent_state_dropdown);
        currentCity = view.findViewById(R.id.current_city_dropdown);
        currentState = view.findViewById(R.id.current_state_dropdown);
        uploadPhoto = view.findViewById(R.id.upload_photo);
        submit = view.findViewById(R.id.fab);
        uploadedImage = view.findViewById(R.id.uploaded_img);
        documentName = view.findViewById(R.id.input_document_name);
        uploadDocument = view.findViewById(R.id.pick_document);
        imgUploader = view.findViewById(R.id.img_uploader);
        docUploader = view.findViewById(R.id.doc_uploader);
        progressDialog = new ProgressDialog(getActivity());
        anotherProgressDialog = new ProgressDialog(getActivity());
        RootRef= FirebaseDatabase.getInstance().getReference();

        ArrayAdapter<String> states =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.india_states));
        permanentState.setAdapter(states);
        currentState.setAdapter(states);

        ArrayAdapter<String> cities =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.india_top_cities));
        permanentCity.setAdapter(cities);
        currentCity.setAdapter(cities);

        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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
                String CandidateName = userName.getText().toString();
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
                    Toast.makeText(getActivity(), "You Are Not Eligible to Vote!", Toast.LENGTH_LONG).show();
                }else if (!EmailID.matches(emailPattern)){
                    Toast.makeText(getActivity(), "Invalid E-Mail Address...", Toast.LENGTH_LONG).show();
                }else if (String.valueOf(MobileNumber).length()!=10){
                    Toast.makeText(getActivity(), "Mobile Number Should have 10 Digits...", Toast.LENGTH_LONG).show();
                }else if (CandidateName.matches("") || CurrentAddress.matches("") || PermanentAddress.matches("")){
                    Toast.makeText(getActivity(), "Some Field is Empty!", Toast.LENGTH_LONG).show();
                }else if (!Password.matches(ConfirmPassword)){
                    Toast.makeText(getActivity(), "Passwords Don't Match!", Toast.LENGTH_LONG).show();
                }else if (status!=2){
                    Toast.makeText(getActivity(), "Please Save Photo & Document", Toast.LENGTH_LONG).show();
                }else {
                    Log.d("Image Download Link", imageDownloadUrl);
                    Log.d("Document Download Link", documentDownloadUrl);
                    onNameSetListener.setGeneralDetails(documentDownloadUrl,imageDownloadUrl,CandidateName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,Password);
                }
            }
        });
        return view;
    }

    public interface OnNameSetListener {
        public void setGeneralDetails(String IdentityProof, String Photo, String candidateName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password);
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

    public boolean isPermissionGranted(int no){
        if(Build.VERSION.SDK_INT>22)
        {
            if(no==1)
            {
                if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 101);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photouri = data.getData();
                uploadedImage.setImageBitmap(photo);
            } else if (requestCode == 2) {
                photouri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(photouri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bmp = null;
                try {
                    bmp = getBitmapFromUri(photouri);
                } catch (IOException e) {
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
                getActivity().getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
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
                            Toast.makeText(getActivity(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getActivity(), "Document Uploaded Successfully ", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            // anotherProgressDialog.setTitle("Document is Uploading...");
                            double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            anotherProgressDialog.setMessage((int) p+" % Uploading...");
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Please Select A Document", Toast.LENGTH_LONG).show();
        }
    }

    public void setDate(View v){
        Calendar mcurrentDate = Calendar.getInstance();
        int mmYear = mcurrentDate.get(Calendar.YEAR);
        int mmMonth = mcurrentDate.get(Calendar.MONTH);
        int mmDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
