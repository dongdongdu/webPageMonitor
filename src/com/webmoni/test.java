package com.webmoni;

import com.pkucareer.intern.SubscribePortalInfo;

public class test {

    public static void main(String[] args) {

        // String aa = "http://scc.pku.edu.cn/home!recruitList.action?category=1&start=0&limit=40";
        //
        String a2 = "https://portal.pku.edu.cn/portal2013/notice/retrAllDeptNotice.do?start=0&limit=10";
        //
        // String bb = Utils.RetriveWebContent(a2);
        //
        // System.out.println(bb);

        SubscribePortalInfo aInfo = new SubscribePortalInfo();
        aInfo.ProcessInfo();
    }
}
