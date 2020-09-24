package com.thirtyfourthirtysix.electronicvotingsystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CandidatePartyFragment extends Fragment {
    TextInputEditText politicalExperience,jobBusiness,moneyAssets,property;
    AutoCompleteTextView educationQualification,partyName;
    MaterialButton submit;
    String CandidateName,DateOfBirth,EmailID,PermanentAddress,CurrentAddress,Password,identityProof,CandidatePhoto;
    long MobileNumber;

    public CandidatePartyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_two_layout, container, false);
        View view=inflater.inflate(R.layout.fragment_candidate_party,container,false);
        politicalExperience = view.findViewById(R.id.political_experience);
        jobBusiness = view.findViewById(R.id.job_business);
        moneyAssets = view.findViewById(R.id.money_assets);
        property = view.findViewById(R.id.property_details);
        educationQualification = view.findViewById(R.id.education_qualification);
        partyName = view.findViewById(R.id.party_name);
        submit = view.findViewById(R.id.submit_all);
        ArrayAdapter<String> eduqual =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.education_qualification));
        educationQualification.setAdapter(eduqual);

        ArrayAdapter<String> partyname =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.dropdown_menu_popup_item,
                        getResources().getStringArray(R.array.parties));
        partyName.setAdapter(partyname);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent int1=new Intent(getActivity(),MainActivity.class);
                LayoutInflater li1=getLayoutInflater();
                View layout=li1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout));
                TextView txt= (TextView) layout.findViewById(R.id.text_toast);
                txt.setText("Registration Successful, You Can Login once Admin verifies you!");
                String eduQualification = educationQualification.getText().toString();
                String politicalExp = politicalExperience.getText().toString();
                String jobOrBusiness = jobBusiness.getText().toString();
                String moneyAndAssets = moneyAssets.getText().toString();
                String propertyDetails = property.getText().toString();
                String party = partyName.getText().toString();

                if (eduQualification.matches("") || politicalExp.matches("") || jobOrBusiness.matches("") || moneyAndAssets.matches("") || propertyDetails.matches("") || party.matches("")){
                    Toast.makeText(getActivity(), "Some Field is Empty!", Toast.LENGTH_LONG).show();
                }else {
                    CandidateDetails candidateDetails = new CandidateDetails(CandidateName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,Password,CandidatePhoto,identityProof,eduQualification,politicalExp,jobOrBusiness,moneyAndAssets,propertyDetails,party);
                    DatabaseHelper.addNewCandidate(candidateDetails);
                    Toast toast=new Toast(getActivity().getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                    startActivity(int1);
                    getActivity().finish();
                }
            }
        });
        return view;
    }
    public void updateInfo(String IdentityProof, String Photo, String candidateName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password){
        identityProof=IdentityProof;
        CandidatePhoto=Photo;
        CandidateName=candidateName;
        DateOfBirth=dateOfBirth;
        EmailID=emailID;
        MobileNumber=mobileNumber;
        PermanentAddress=permanentAddress;
        CurrentAddress=currentAddress;
        Password=password;
    }
}
