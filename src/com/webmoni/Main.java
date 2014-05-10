package com.webmoni;

import java.text.ParseException;

import com.pkucareer.intern.SubscribeInterns;
import com.pkucareer.intern.SubscribeJobs;

public class Main {

    public static void main(String[] args) throws ParseException {

        SubscribeInterns subscribeInterns = new SubscribeInterns();
        subscribeInterns.ProcessSubscription();

        SubscribeJobs subscribeJobs = new SubscribeJobs();
        subscribeJobs.ProcessSubscription();

    }

}
