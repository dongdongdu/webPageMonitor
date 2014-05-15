package com.webmoni;

import java.text.ParseException;

import com.pkucareer.intern.SubscribeInterns;
import com.pkucareer.intern.SubscribeJobs;

public class Main {

    public static void main(String[] args) throws ParseException {

        SubscribeInterns subscribeInterns1 = new SubscribeInterns();
        subscribeInterns1.ProcessSubscription();

        SubscribeJobs subscribeJobs2 = new SubscribeJobs();
        subscribeJobs2.ProcessSubscription();

    }

}
