package com.pkucareer.intern;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class SubscribeInterns {

    static String urlInternBase = "http://jobplatform.pku.edu.cn/portal/listinternship?page=";
    static String configString = "ddusubsribe.properties";
    static InternObject lastIntern;

    public static void ProcessInternSubscription() {

        if (lastIntern == null) {
            ReadMostRecentPost();
        }

        out.println("id is " + lastIntern.getId());
        out.println("title is " + lastIntern.getName());
        out.println("create date is " + lastIntern.getCreateDate());

        String webContent = RetriveWebContent(urlInternBase + 1);

        out.println(webContent);
    }

    private static String RetriveWebContent(String urlString) {
        StringBuffer sb = new StringBuffer();
        URL url;
        String temp;
        try {
            url = new URL(urlString);
            // 读取网页全部内容
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            in.close();
        } catch (MalformedURLException e) {
            out.println("你输入的URL格式有问题");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private static void ReadMostRecentPost() {

        // 读取配置文件
        Properties properties = new Properties();
        File file = new File(configString);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader aInputStreamReader = new InputStreamReader(fis, "UTF-8");
            properties.load(aInputStreamReader);

            int lastInternId = Integer.parseInt(properties.getProperty("lastInternId"));
            String lastInternTitle = properties.getProperty("lastInternTitle");
            String lastInternCreateDate = properties.getProperty("lastInternCreateDate");

            lastIntern = new InternObject(lastInternId, lastInternTitle, lastInternCreateDate);

            fis.close();
            aInputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
