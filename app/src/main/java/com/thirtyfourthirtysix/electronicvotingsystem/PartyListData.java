package com.thirtyfourthirtysix.electronicvotingsystem;

public class PartyListData {
    private String LogoLink;
    private String PartyName;
    private String PartyType;

    public PartyListData(String logoLink, String partyName, String partyType) {
        LogoLink = logoLink;
        PartyName = partyName;
        PartyType = partyType;
    }

    public String getLogoLink() {
        return LogoLink;
    }

    public void setLogoLink(String logoLink) {
        LogoLink = logoLink;
    }

    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String partyName) {
        PartyName = partyName;
    }

    public String getPartyType() {
        return PartyType;
    }

    public void setPartyType(String partyType) {
        PartyType = partyType;
    }
}
