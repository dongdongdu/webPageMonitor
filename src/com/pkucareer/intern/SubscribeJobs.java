package com.pkucareer.intern;

public class SubscribeJobs extends SubscribeClassBase {

    public SubscribeJobs() {
        this.urlJobBase = "http://scc.pku.edu.cn/home!recruitList.action?category=1&start=0&limit=100";

        this.noPostString = "No new post for jobs";
        this.subjectStringformat = "北大就业信息 - %s %s";
        this.propCreateDate = "lastJobCreateDate";
        this.propTitle = "lastJobTitle";
    }
}
