package com.pkucareer.intern;

public class SubscribeJobs extends SubscribeClassBase {

    public SubscribeJobs() {
        this.noPostString = "No new post for jobs";
        this.subjectStringformat = "北大就业信息 - %s %s";
        this.urlJobBase = "http://jobplatform.pku.edu.cn/portal/listemploy?page=";
        this.propCreateDate = "lastJobCreateDate";
        this.propTitle = "lastJobTitle";
    }
}
