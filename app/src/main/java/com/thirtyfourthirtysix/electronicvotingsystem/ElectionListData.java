package com.thirtyfourthirtysix.electronicvotingsystem;

import java.io.Serializable;
import java.util.ArrayList;

public class ElectionListData implements Serializable {
    private String electionType;
    private String electionState;
    private String electionCity;
    private String electionDistrict;
    private String electionDate;
    private String electionStatus;
    private ArrayList<ElectionVotes> electionVotes;

    public ElectionListData(String electionType, String electionState, String electionCity, String electionDistrict, String electionDate, String electionStatus, ArrayList<ElectionVotes> electionVotes) {
        this.electionType = electionType;
        this.electionState = electionState;
        this.electionCity = electionCity;
        this.electionDistrict = electionDistrict;
        this.electionDate = electionDate;
        this.electionStatus = electionStatus;
        this.electionVotes = electionVotes;
    }

    public String getElectionType() {
        return electionType;
    }

    public void setElectionType(String electionType) {
        this.electionType = electionType;
    }

    public String getElectionState() {
        return electionState;
    }

    public void setElectionState(String electionState) {
        this.electionState = electionState;
    }

    public String getElectionCity() {
        return electionCity;
    }

    public void setElectionCity(String electionCity) {
        this.electionCity = electionCity;
    }

    public String getElectionDistrict() {
        return electionDistrict;
    }

    public void setElectionDistrict(String electionDistrict) {
        this.electionDistrict = electionDistrict;
    }

    public String getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(String electionDate) {
        this.electionDate = electionDate;
    }

    public String getElectionStatus() {
        return electionStatus;
    }

    public void setElectionStatus(String electionStatus) {
        this.electionStatus = electionStatus;
    }

    public ArrayList<ElectionVotes> getElectionVotes() {
        return electionVotes;
    }

    public void setElectionVotes(ArrayList<ElectionVotes> electionVotes) {
        this.electionVotes = electionVotes;
    }
}
