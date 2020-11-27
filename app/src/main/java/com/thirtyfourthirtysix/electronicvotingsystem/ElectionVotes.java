package com.thirtyfourthirtysix.electronicvotingsystem;

import java.io.Serializable;

public class ElectionVotes implements Serializable {
    private String voterName;
    private String voterParty;

    public ElectionVotes(String voterName, String voterParty) {
        this.voterName = voterName;
        this.voterParty = voterParty;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public String getVoterParty() {
        return voterParty;
    }

    public void setVoterParty(String voterParty) {
        this.voterParty = voterParty;
    }
}
