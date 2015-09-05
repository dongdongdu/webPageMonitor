package com.pkucareer.intern;

public class SubscribeInterns extends SubscribeClassBase {

    public SubscribeInterns() {

        this.urlJobBase = "http://scc.pku.edu.cn/home!recruitList.action?category=2&start=0&limit=100";

        this.noPostString = "No new post for interns";
        this.subjectStringformat = "北大实习信息 - %s %s";
        this.propCreateDate = "lastInternCreateDate";
        this.propTitle = "lastInternTitle";
    }
}
