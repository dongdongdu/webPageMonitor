package com.pkucareer.intern;

import static com.webmoni.util.Utils.readProperties;
import static java.lang.System.out;

import java.util.Properties;

import com.webmoni.util.Utils;

public class SubscribeInterns {

    static String urlInternBase = "http://jobplatform.pku.edu.cn/portal/listinternship?page=";
    static String configString = "ddusubsribe.properties";
    static InternObject lastIntern;

    public void ProcessInternSubscription() {

        if (lastIntern == null) {
            ReadMostRecentPost();
        }

        out.println("id is " + lastIntern.getId());
        out.println("title is " + lastIntern.getName());
        out.println("create date is " + lastIntern.getCreateDate());

        String webContent = Utils.RetriveWebContent(urlInternBase + 1);

        out.println(webContent);

        // Utils.sendEmail("hahahahah", webContent);
    }

    private void ReadMostRecentPost() {
        Properties properties = readProperties(configString);

        int lastInternId = Integer.parseInt(properties.getProperty("lastInternId"));
        String lastInternTitle = properties.getProperty("lastInternTitle");
        String lastInternCreateDate = properties.getProperty("lastInternCreateDate");

        lastIntern = new InternObject(lastInternId, lastInternTitle, lastInternCreateDate);
    }
}
