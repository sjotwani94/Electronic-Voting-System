package com.thirtyfourthirtysix.electronicvotingsystem;

public class VoterDetails {
    private String VoterName;
    private String DateOfBirth;
    private String EmailID;
    private long MobileNumber;
    private String PermanentAddress;
    private String CurrentAddress;
    private String Password;
    private String Photo;
    private String IdentityProof;
    private String VoterID;

    public VoterDetails(String IdentityProof, String Photo, String voterName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password) {
        setVoterID("");
        setIdentityProof(IdentityProof);
        setPhoto(Photo);
        setVoterName(voterName);
        setDateOfBirth(dateOfBirth);
        setEmailID(emailID);
        setMobileNumber(mobileNumber);
        setPermanentAddress(permanentAddress);
        setCurrentAddress(currentAddress);
        setPassword(password);
    }

    public String getVoterID() {
        return VoterID;
    }

    public void setVoterID(String voterID) {
        VoterID = voterID;
    }

    public String getIdentityProof() {
        return IdentityProof;
    }

    public void setIdentityProof(String identityProof) {
        IdentityProof = identityProof;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public String getVoterName() {
        return VoterName;
    }

    public void setVoterName(String voterName) {
        VoterName = voterName;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public long getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getPermanentAddress() {
        return PermanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        PermanentAddress = permanentAddress;
    }

    public String getCurrentAddress() {
        return CurrentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        CurrentAddress = currentAddress;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
