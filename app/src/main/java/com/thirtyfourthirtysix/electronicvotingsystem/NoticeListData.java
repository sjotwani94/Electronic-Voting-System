package com.thirtyfourthirtysix.electronicvotingsystem;

public class NoticeListData {
    private String noticeSender;
    private String noticeSubject;
    private String noticeImage;
    private String noticeDescription;

    public NoticeListData(String noticeSender, String noticeSubject, String noticeImage, String noticeDescription) {
        this.noticeSender = noticeSender;
        this.noticeSubject = noticeSubject;
        this.noticeImage = noticeImage;
        this.noticeDescription = noticeDescription;
    }

    public String getNoticeSender() {
        return noticeSender;
    }

    public void setNoticeSender(String noticeSender) {
        this.noticeSender = noticeSender;
    }

    public String getNoticeSubject() {
        return noticeSubject;
    }

    public void setNoticeSubject(String noticeSubject) {
        this.noticeSubject = noticeSubject;
    }

    public String getNoticeImage() {
        return noticeImage;
    }

    public void setNoticeImage(String noticeImage) {
        this.noticeImage = noticeImage;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public void setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
    }
}
