package com.pkucareer.intern;

import static com.webmoni.util.Utils.RetriveWebContent;
import static com.webmoni.util.Utils.readProperties;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webmoni.util.Utils;

public class SubscribeInterns {

    static String urlInternBase = "http://jobplatform.pku.edu.cn/portal/listinternship?page=";
    static String urlInternHost = "http://jobplatform.pku.edu.cn";
    static String configString = "ddusubsribe.properties";
    static String keyString = "lastInternId";

    private CareerObject lastIntern;

    public void ProcessSubscription() {

        if (lastIntern == null) {
            ReadMostRecentPost();
        }

        int lastInternId = lastIntern.getId();

        TreeMap<Integer, CareerObject> results = new TreeMap<Integer, CareerObject>();

        // 获取前3页的网页内容
        for (int i = 1; i <= 3; i++) {
            String webContent = RetriveWebContent(urlInternBase + i);
            String ul = getUlString(webContent);
            List<String> li = getLiList(ul);
            TreeMap<Integer, CareerObject> map = getTreeMapFromLiList(li);

            if (map.lastKey() <= lastInternId) {
                // map 里最大的id 都比上次id 小说明没有更新，退出循环
                break;
            }

            SortedMap<Integer, CareerObject> newPostsMap = map.subMap(lastInternId, false, map.lastKey(), true);
            if (newPostsMap.size() > 0) {
                results.putAll(newPostsMap);
            }

            if (map.firstKey() <= lastInternId) {
                // 如果map里最小的id比上次id小，说明页面里只有部分更新，退出循环
                break;
            }
        }

        if (results.size() > 0) {
            // 对新post发邮件
            for (CareerObject newpost : results.values()) {
                notifyNewIntern(newpost);
            }

            // 更新最后lastInternId
            if (lastInternId < results.lastKey()) {
                lastInternId = results.lastKey();
                Utils.writeProperties(configString, keyString, lastInternId + "");
            }
        } else {
            out.println("No new post");
        }

    }

    private void notifyNewIntern(CareerObject internObject) {
        String newpostContent = Utils.RetriveWebContent(internObject.getLinkURL());

        Pattern p = Pattern.compile("<ul class=\"wz1ul\">.*?</ul>");
        Matcher m = p.matcher(newpostContent);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(m.group());
        }

        String subject = String.format("北大实习信息 - %s %s", internObject.getName(), internObject.getCreateDate());
        Utils.sendEmail(subject, sb.toString());
        out.println("邮件已发送：" + subject);
    }

    private TreeMap<Integer, CareerObject> getTreeMapFromLiList(List<String> liList) {
        TreeMap<Integer, CareerObject> treeMap = new TreeMap<Integer, CareerObject>();

        for (String string : liList) {

            // 获取li 里的内容
            // group(1) 里拿到链接 /portal/viewinternship/id/10728
            // group(2) 里拿到id 10728
            // group(3) 里拿到标题 学而思2014暑期实习生招聘
            // group(4) 里拿到日期 2014-05-04

            Pattern p = Pattern.compile("<a href=\"(.*?(\\d+?))\".*>(.*?)</a>.*\\[(.*?)\\].*");
            Matcher matcher = p.matcher(string);
            while (matcher.find()) {
                int id = Integer.parseInt(matcher.group(2));
                String link = urlInternHost + matcher.group(1).trim();
                String title = matcher.group(3).trim();
                String createDate = matcher.group(4).trim();

                CareerObject internObject = new CareerObject(id, title, createDate, link);
                treeMap.put(id, internObject);
            }
        }

        return treeMap;
    }

    private List<String> getLiList(String ul) {
        List<String> li = new ArrayList<String>();

        // 获取 每个 li 的list
        Pattern pattern = Pattern.compile("<li.*?</li>");
        Matcher matcher = pattern.matcher(ul);
        while (matcher.find()) {
            li.add(matcher.group());
        }
        if (li.size() == 0) {
            out.println("Does not find any matches for li");
            return null;
        }
        return li;
    }

    private String getUlString(String webContent) {

        // 获取 listul 里的内容
        String regex = "<ul class=\"listul\"[\\s\\S]*?</ul>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(webContent);
        StringBuffer ul = new StringBuffer();
        while (matcher.find()) {
            ul.append(matcher.group());
        }
        if (ul.length() == 0) {
            out.println("Does not find any matches for ul");
            return null;
        }
        return ul.toString();
    }

    private void ReadMostRecentPost() {
        Properties properties = readProperties(configString);

        int lastInternId = Integer.parseInt(properties.getProperty(keyString));
        lastIntern = new CareerObject();
        lastIntern.setId(lastInternId);
    }
}
