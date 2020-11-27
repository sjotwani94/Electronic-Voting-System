package com.thirtyfourthirtysix.electronicvotingsystem;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public final class DatabaseHelper {

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

    public static final int fetchLoginDetails(@NonNull DataSnapshot snapshot, String EmailID, String pwd) {
        for (DataSnapshot voter : snapshot.getChildren()) {
            String key = voter.getKey();
            String ID = null;
            if (voter.hasChild("voterID")){
                ID = voter.child("voterID").getValue(String.class);
            }else if (voter.hasChild("candidateID")){
                ID = voter.child("candidateID").getValue(String.class);
            }
            String emailID = voter.child("emailID").getValue(String.class);
            String Password = voter.child("password").getValue(String.class);
            if (emailID.matches(EmailID) && Password.matches(md5(pwd))){
                if (ID.matches("")){
                    return 2;
                } else {
                    return 0;
                }
            }
        }
        return 1;
    }

    public static final ArrayList<ElectionListData> fetchElections(@NonNull DataSnapshot snapshot, String PermanentAddress, String CurrentAddress){
        ArrayList<ElectionListData> electionListData = new ArrayList<ElectionListData>();
        for (DataSnapshot election : snapshot.getChildren()) {
            String key = election.getKey();
            if (key.matches("Assembly Election")){
                for (DataSnapshot state : election.getChildren()) {
                    String statename = state.getKey();
                    if (PermanentAddress.contains(statename) || CurrentAddress.contains(statename)){
                        for (DataSnapshot date : state.getChildren()) {
                            String electiondate = date.getKey();
                            String electionstatus = date.child("status").getValue(String.class);
                            ArrayList<ElectionVotes> electionVotes = new ArrayList<ElectionVotes>();
                            if (date.child("votes").hasChildren()){
                                for (DataSnapshot votes : date.child("votes").getChildren()){
                                    ElectionVotes electionVotes1 = new ElectionVotes(votes.child("voterName").getValue(String.class),votes.child("voterParty").getValue(String.class));
                                    electionVotes.add(electionVotes1);
                                }
                            }
                            ElectionListData electionListData1 = new ElectionListData(key,statename,null,null,electiondate,electionstatus,electionVotes);
                            electionListData.add(electionListData1);
                        }
                    }
                }
            }
            else if (key.matches("By Election")){
                for (DataSnapshot state : election.getChildren()) {
                    String statename = state.getKey();
                    if (PermanentAddress.contains(statename) || CurrentAddress.contains(statename)){
                        for (DataSnapshot city : state.getChildren()) {
                            String cityname = city.getKey();
                            if (PermanentAddress.contains(cityname) || CurrentAddress.contains(cityname)){
                                for (DataSnapshot district : city.getChildren()){
                                    String districtname = district.getKey();
                                    for (DataSnapshot date : district.getChildren()){
                                        String electiondate = date.getKey();
                                        String electionstatus = date.child("status").getValue(String.class);
                                        ArrayList<ElectionVotes> electionVotes = new ArrayList<ElectionVotes>();
                                        if (date.child("votes").hasChildren()){
                                            for (DataSnapshot votes : date.child("votes").getChildren()){
                                                ElectionVotes electionVotes1 = new ElectionVotes(votes.child("voterName").getValue(String.class),votes.child("voterParty").getValue(String.class));
                                                electionVotes.add(electionVotes1);
                                            }
                                        }
                                        ElectionListData electionListData1 = new ElectionListData(key,statename,cityname,districtname,electiondate,electionstatus,electionVotes);
                                        electionListData.add(electionListData1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (key.matches("Corporation Election")){
                for (DataSnapshot state : election.getChildren()) {
                    String statename = state.getKey();
                    if (PermanentAddress.contains(statename) || CurrentAddress.contains(statename)){
                        for (DataSnapshot city : state.getChildren()) {
                            String cityname = city.getKey();
                            if (PermanentAddress.contains(cityname) || CurrentAddress.contains(cityname)){
                                for (DataSnapshot date : city.getChildren()){
                                    String electiondate = date.getKey();
                                    String electionstatus = date.child("status").getValue(String.class);
                                    ArrayList<ElectionVotes> electionVotes = new ArrayList<ElectionVotes>();
                                    if (date.child("votes").hasChildren()){
                                        for (DataSnapshot votes : date.child("votes").getChildren()){
                                            ElectionVotes electionVotes1 = new ElectionVotes(votes.child("voterName").getValue(String.class),votes.child("voterParty").getValue(String.class));
                                            electionVotes.add(electionVotes1);
                                        }
                                    }
                                    ElectionListData electionListData1 = new ElectionListData(key,statename,cityname,null,electiondate,electionstatus,electionVotes);
                                    electionListData.add(electionListData1);
                                }
                            }
                        }
                    }
                }
            }
            else if (key.matches("Parliament Election")){
                for (DataSnapshot date : election.getChildren()){
                    String electiondate = date.getKey();
                    String electionstatus = date.child("status").getValue(String.class);
                    ArrayList<ElectionVotes> electionVotes = new ArrayList<ElectionVotes>();
                    if (date.child("votes").hasChildren()){
                        for (DataSnapshot votes : date.child("votes").getChildren()){
                            ElectionVotes electionVotes1 = new ElectionVotes(votes.child("voterName").getValue(String.class),votes.child("voterParty").getValue(String.class));
                            electionVotes.add(electionVotes1);
                        }
                    }
                    ElectionListData electionListData1 = new ElectionListData(key,"All Over India",null,null,electiondate,electionstatus,electionVotes);
                    electionListData.add(electionListData1);
                }
            }
        }
        return electionListData;
    }

    public static final boolean setVote(String electionType, String electionState, String electionCity, String electionDistrict, String electionDate, ElectionVotes electionVotes){
        if (electionType.matches("Assembly Election")){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Election/"+electionType+"/"+electionState+"/"+electionDate+"/votes");
            DatabaseReference newIssueRef = rootRef.push();
            newIssueRef.setValue(electionVotes);
        }else if (electionType.matches("By Election")){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Election/"+electionType+"/"+electionState+"/"+electionCity+"/"+electionDistrict+"/"+electionDate+"/votes");
            DatabaseReference newIssueRef = rootRef.push();
            newIssueRef.setValue(electionVotes);
        }else if (electionType.matches("Corporation Election")){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Election/"+electionType+"/"+electionState+"/"+electionCity+"/"+electionDate+"/votes");
            DatabaseReference newIssueRef = rootRef.push();
            newIssueRef.setValue(electionVotes);
        }else if (electionType.matches("Parliament Election")){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Election/"+electionType+"/"+electionDate+"/votes");
            DatabaseReference newIssueRef = rootRef.push();
            newIssueRef.setValue(electionVotes);
        }
        return true;
    }

    public static final String retrieveKey(@NonNull DataSnapshot snapshot, String EmailID) {
        for (DataSnapshot user : snapshot.getChildren()) {
            String key = user.getKey();
            String emailID = user.child("emailID").getValue(String.class);
            if (emailID.matches(EmailID)){
                return key;
            }
        }
        return null;
    }

    public static final boolean verifyEmailID(@NonNull DataSnapshot snapshot, String EmailID) {
        for (DataSnapshot voter : snapshot.getChildren()) {
            String emailID = voter.child("emailID").getValue(String.class);
            if (emailID.matches(EmailID)){
                return true;
            }
        }
        return false;
    }

    public static final ArrayList<String> fetchPartyNames(@NonNull DataSnapshot snapshot){
        ArrayList<String> partyNames = new ArrayList<String>();
        for (DataSnapshot voter : snapshot.getChildren()) {
            partyNames.add(voter.child("PartyName").getValue(String.class));
        }
        return partyNames;
    }

    public static final ArrayList<PartyListData> fetchPartyDetails(@NonNull DataSnapshot snapshot){
        ArrayList<PartyListData> partyListData = new ArrayList<PartyListData>();
        for (DataSnapshot party : snapshot.getChildren()) {
            String partyLogo = party.child("LogoLink").getValue(String.class);
            String partyName = party.child("PartyName").getValue(String.class);
            String partyType = party.child("PartyType").getValue(String.class);
            partyListData.add(new PartyListData(partyLogo,partyName,partyType));
        }
        return partyListData;
    }

    public static final void addNewVoter(VoterDetails newVoter) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Voters");
        DatabaseReference newIssueRef = rootRef.push();
        newIssueRef.setValue(newVoter);
    }

    public static final void addNewCandidate(CandidateDetails newCandidate) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Candidates");
        DatabaseReference newIssueRef = rootRef.push();
        newIssueRef.setValue(newCandidate);
    }

    public static final void deleteVoter(String keyToDelete) {

        // get a reference to the issue child node to be deleted
        // Task 6: build the reference and call it issueRef, the path is /issues/<keyToDelete>"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/Voters/" + keyToDelete);
        // remove the child node and its children
        issueRef.removeValue();
    }

    public static final void deleteCandidate(String keyToDelete) {

        // get a reference to the issue child node to be deleted
        // Task 6: build the reference and call it issueRef, the path is /issues/<keyToDelete>"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/Candidates/" + keyToDelete);
        // remove the child node and its children
        issueRef.removeValue();
    }

    public static final ArrayList<VoterDetails> getVoterDetailsEmail(@NonNull DataSnapshot snapshot, String EMail) {
        ArrayList<VoterDetails> voterDetails = new ArrayList<VoterDetails>();
        for (DataSnapshot user : snapshot.getChildren()) {
            String VoterName = user.child("voterName").getValue(String.class);
            String DateOfBirth = user.child("dateOfBirth").getValue(String.class);
            String EmailID = user.child("emailID").getValue(String.class);
            long MobileNumber = user.child("mobileNumber").getValue(Long.class);
            String PermanentAddress = user.child("permanentAddress").getValue(String.class);
            String CurrentAddress = user.child("currentAddress").getValue(String.class);
            String Password = user.child("password").getValue(String.class);
            String Photo = user.child("photo").getValue(String.class);
            String IdentityProof = user.child("identityProof").getValue(String.class);
            if (EmailID.matches(EMail)){
                voterDetails.add(new VoterDetails(IdentityProof,Photo,VoterName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,Password));
            }
        }
        return voterDetails;
    }

    public static final ArrayList<CandidateDetails> getCandidateDetailsEmail(@NonNull DataSnapshot snapshot, String EMail) {
        ArrayList<CandidateDetails> candidateDetails = new ArrayList<CandidateDetails>();
        for (DataSnapshot user : snapshot.getChildren()) {
            String VoterName = user.child("candidateName").getValue(String.class);
            String DateOfBirth = user.child("dateOfBirth").getValue(String.class);
            String EmailID = user.child("emailID").getValue(String.class);
            long MobileNumber = user.child("mobileNumber").getValue(Long.class);
            String PermanentAddress = user.child("permanentAddress").getValue(String.class);
            String CurrentAddress = user.child("currentAddress").getValue(String.class);
            String Password = user.child("password").getValue(String.class);
            String Photo = user.child("photo").getValue(String.class);
            String IdentityProof = user.child("identityProof").getValue(String.class);
            String EducationQualification = user.child("educationQualification").getValue(String.class);
            String PoliticalExperience = user.child("politicalExperience").getValue(String.class);
            String JobBusiness = user.child("jobBusiness").getValue(String.class);
            String MoneyAssets = user.child("moneyAssets").getValue(String.class);
            String PropertyDetails = user.child("propertyDetails").getValue(String.class);
            String PartyName = user.child("partyName").getValue(String.class);
            if (EmailID.matches(EMail)){
                candidateDetails.add(new CandidateDetails(VoterName,DateOfBirth,EmailID,MobileNumber,PermanentAddress,CurrentAddress,Password,IdentityProof,Photo,EducationQualification,PoliticalExperience,JobBusiness,MoneyAssets,PropertyDetails,PartyName));
            }
        }
        return candidateDetails;
    }

    public static final void addNewNotice(NoticeListData newNotice) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Notices");
        DatabaseReference newIssueRef = rootRef.push();
        newIssueRef.setValue(newNotice);
    }

    public static final ArrayList<NoticeListData> getNoticeDetails(@NonNull DataSnapshot snapshot) {
        ArrayList<NoticeListData> noticeDetails = new ArrayList<NoticeListData>();
        for (DataSnapshot user : snapshot.getChildren()) {
            String NoticeSender = user.child("noticeSender").getValue(String.class);
            String NoticeSubject = user.child("noticeSubject").getValue(String.class);
            String NoticeImage = user.child("noticeImage").getValue(String.class);
            String NoticeDescription = user.child("noticeDescription").getValue(String.class);
            noticeDetails.add(new NoticeListData(NoticeSender,NoticeSubject,NoticeImage,NoticeDescription));
        }
        return noticeDetails;
    }

    public static final void addLike(LikesOnNotice likesOnNotice, String noticeDescription){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("/Likes/"+noticeDescription);
        DatabaseReference newIssueRef = rootRef.push();
        newIssueRef.setValue(likesOnNotice);
    }

    public static final String retrieveLikeKey(@NonNull DataSnapshot snapshot, String UserName) {
        for (DataSnapshot user : snapshot.getChildren()) {
            String key = user.getKey();
            String userName = user.child("userName").getValue(String.class);
            if (userName.matches(UserName)){
                return key;
            }
        }
        return null;
    }

    public static final void deleteLike(String keyToDelete, String noticeDescription) {
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/Likes/"+noticeDescription+"/"+keyToDelete);
        issueRef.removeValue();
    }

    public static final ArrayList<LikesOnNotice> getLikesOnNotice(@NonNull DataSnapshot snapshot) {
        ArrayList<LikesOnNotice> likesOnNotices = new ArrayList<LikesOnNotice>();
        for (DataSnapshot user : snapshot.getChildren()) {
            String UserName = user.child("userName").getValue(String.class);
            String UserRole = user.child("userRole").getValue(String.class);
            likesOnNotices.add(new LikesOnNotice(UserName,UserRole));
        }
        return likesOnNotices;
    }

    public static final void updateUserPassword(String userRole, String issueKey, String newPassword) {

        // get a reference to the child node to be updated and to the "resolved" child node
        // Task 7: build the reference and call it issueRef, the path is /issues/<issueKey>/resolved"
        DatabaseReference issueRef = FirebaseDatabase.getInstance().getReference("/"+userRole+"/" + issueKey + "/password");
        DatabaseReference ref = null;
        if (userRole.matches("Voters")){
            ref = FirebaseDatabase.getInstance().getReference("/Voters/" + issueKey + "/voterID");
        } else if (userRole.matches("Candidates")){
            ref = FirebaseDatabase.getInstance().getReference("/Candidates/" + issueKey + "/candidateID");
        }
        // set the value of the node
        issueRef.setValue(md5(newPassword));
        ref.setValue("");
    }
}

