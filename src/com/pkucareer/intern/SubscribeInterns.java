package com.pkucareer.intern;

public class SubscribeInterns extends SubscribeClassBase {

    public SubscribeInterns() {
        this.noPostString = "No new post for interns";
        this.subjectStringformat = "北大实习信息 - %s %s";
        this.urlJobBase = "http://jobplatform.pku.edu.cn/portal/listinternship?page=";
        this.propCreateDate = "lastInternCreateDate";
        this.propTitle = "lastInternTitle";
    }
}
