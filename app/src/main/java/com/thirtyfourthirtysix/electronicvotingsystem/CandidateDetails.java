package com.thirtyfourthirtysix.electronicvotingsystem;

public class CandidateDetails {
    private String CandidateName;
    private String DateOfBirth;
    private String EmailID;
    private long MobileNumber;
    private String PermanentAddress;
    private String CurrentAddress;
    private String Password;
    private String Photo;
    private String IdentityProof;
    private String CandidateID;

    public CandidateDetails(String IdentityProof, String Photo, String candidateName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password) {
        setCandidateID("");
        setIdentityProof(IdentityProof);
        setPhoto(Photo);
        setCandidateName(candidateName);
        setDateOfBirth(dateOfBirth);
        setEmailID(emailID);
        setMobileNumber(mobileNumber);
        setPermanentAddress(permanentAddress);
        setCurrentAddress(currentAddress);
        setPassword(password);
    }

    public String getCandidateID() {
        return CandidateID;
    }

    public void setCandidateID(String candidateID) {
        CandidateID = candidateID;
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

    public String getCandidateName() {
        return CandidateName;
    }

    public void setCandidateName(String candidateName) {
        CandidateName = candidateName;
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
