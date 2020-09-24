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
    private String EducationQualification;
    private String PoliticalExperience;
    private String JobBusiness;
    private String MoneyAssets;
    private String PropertyDetails;
    private String PartyName;

    public CandidateDetails(String candidateName, String dateOfBirth, String emailID, long mobileNumber, String permanentAddress, String currentAddress, String password, String photo, String identityProof, String educationQualification, String politicalExperience, String jobBusiness, String moneyAssets, String propertyDetails, String partyName) {
        setCandidateID("");
        setCandidateName(candidateName);
        setDateOfBirth(dateOfBirth);
        setEmailID(emailID);
        setMobileNumber(mobileNumber);
        setPermanentAddress(permanentAddress);
        setCurrentAddress(currentAddress);
        setPassword(password);
        setPhoto(photo);
        setIdentityProof(identityProof);
        setEducationQualification(educationQualification);
        setPoliticalExperience(politicalExperience);
        setJobBusiness(jobBusiness);
        setMoneyAssets(moneyAssets);
        setPropertyDetails(propertyDetails);
        setPartyName(partyName);
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

    public String getEducationQualification() {
        return EducationQualification;
    }

    public void setEducationQualification(String educationQualification) {
        EducationQualification = educationQualification;
    }

    public String getPoliticalExperience() {
        return PoliticalExperience;
    }

    public void setPoliticalExperience(String politicalExperience) {
        PoliticalExperience = politicalExperience;
    }

    public String getJobBusiness() {
        return JobBusiness;
    }

    public void setJobBusiness(String jobBusiness) {
        JobBusiness = jobBusiness;
    }

    public String getMoneyAssets() {
        return MoneyAssets;
    }

    public void setMoneyAssets(String moneyAssets) {
        MoneyAssets = moneyAssets;
    }

    public String getPropertyDetails() {
        return PropertyDetails;
    }

    public void setPropertyDetails(String propertyDetails) {
        PropertyDetails = propertyDetails;
    }

    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String partyName) {
        PartyName = partyName;
    }
}
