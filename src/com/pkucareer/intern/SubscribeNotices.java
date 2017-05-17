package com.pkucareer.intern;



public class   SubscribeNotices extends  SubscribeClassBase {

    public SubscribeNotices(){
        this.urlJobBase = "http://scc.pku.edu.cn/home!newsHome" +
                ".action?category=13&start=0&limit=30";

        this.noPostString = "No new post for notices";
        this.subjectStringformat = "就业通知公告 - %s %s";
        this.propCreateDate = "lastNoticeCreateDate";
        this.propTitle = "lastNoticeTitle";

    }
}
