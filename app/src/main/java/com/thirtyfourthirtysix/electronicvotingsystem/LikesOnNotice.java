package com.thirtyfourthirtysix.electronicvotingsystem;

public class LikesOnNotice {
    private String userName;
    private String userRole;

    public LikesOnNotice(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
