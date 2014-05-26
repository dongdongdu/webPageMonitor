package com.pkucareer.intern;

import static com.webmoni.util.Utils.RetriveWebContent;
import static com.webmoni.util.Utils.dateStringComparator;
import static com.webmoni.util.Utils.readProperties;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webmoni.util.Utils;

public abstract class SubscribeClassBase {
    protected String urlJobnHost = "http://jobplatform.pku.edu.cn";
    protected String configString = "ddusubsribe.properties";

    protected String urlJobBase = "";
    protected String propCreateDate = "";
    protected String propTitle = "";
    protected String noPostString = "";
    protected String subjectStringformat = "";

    String lastCreateDate = null;
    String lastTitles = null;

    public void ProcessSubscription() {

        if (lastCreateDate == null || lastTitles == null) {
            ReadMostRecentPost();
        }
        String newCreateDate = "";
        String newTitles = "";

        List<CareerObject> sendList = new ArrayList<CareerObject>();

        // 获取前3页的网页内容

        for (int i = 1; i <= 3; i++) {
            String webContent = RetriveWebContent(urlJobBase + i);
            String ul = getUlString(webContent);
            List<String> li = getLiList(ul);
            List<CareerObject> list = getCareerObjectListFromLiList(li);
            Collections.sort(list);

            int count = 0;
            for (int j = list.size() - 1; j >= 0; j--) {
                CareerObject o = list.get(j);
                int a = dateStringComparator.compare(o.getCreateDate(), lastCreateDate);
                if (a == -1) {
                    // 说明这里是旧的post,跳出循环；
                    break;
                }

                if (a == 1) {
                    // 说明这里是最新的post，日期比上次的新
                    sendList.add(o);
                    count++;
                } else {
                    // a==0 说明日期都一样,这里要判断是否同一天有新post出来
                    int idx = lastTitles.indexOf(o.getTitle());
                    // 这里idx==-1 说明是最新的post，上次保存的title里没有
                    if (idx == -1) {
                        sendList.add(o);
                        count++;
                    }
                }
            }

            if (count < list.size()) {
                // 说明当前页面内只有部分被更新，无需继续进入下一页面
                break;
            }

        }

        if (sendList.size() > 0) {
            Collections.sort(sendList);
            newCreateDate = sendList.get(sendList.size() - 1).getCreateDate();
            for (CareerObject careerObject : sendList) {
                if (isSameDateString(newCreateDate, careerObject.getCreateDate())) {
                    newTitles += careerObject.getTitle() + ",";
                }
                notifyNewPost(careerObject);
            }

            Properties properties = Utils.readProperties(configString);
            properties.setProperty(propCreateDate, newCreateDate);

            if (isSameDateString(lastCreateDate, newCreateDate)) {
                properties.setProperty(propTitle, newTitles + lastTitles);
            } else {
                properties.setProperty(propTitle, newTitles);
            }

            Utils.writeProperties(configString, properties);

        } else {
            out.println(noPostString);
            Utils.writeToLog(noPostString);
        }

    }

    /**
     * 
     * @param oldDateString
     * @param newDateString
     * 
     * @return
     */
    private boolean isSameDateString(String dateString1, String dateString2) {
        int a = dateStringComparator.compare(dateString1, dateString2);
        return a == 0 ? true : false;
    }

    private void notifyNewPost(CareerObject internObject) {
        String newpostContent = Utils.RetriveWebContent(internObject.getLinkURL());

        Pattern p = Pattern.compile("<ul class=\"wz1ul\">.*?</ul>");
        Matcher m = p.matcher(newpostContent);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(m.group());
        }

        // 添加post的链接
        String templateString = "<ul><li><a href=\"%s\">%s</a></li></ul>";
        String objLink = internObject.getLinkURL();
        String link = String.format(templateString, internObject.getLinkURL(), objLink, objLink);

        StringBuffer sb2 = new StringBuffer();
        sb2.append(link);

        // 移除所有不必要的 li style
        p = Pattern.compile("style=\".*?\"");
        m = p.matcher(sb.toString());
        sb2.append(m.replaceAll(""));

        String subject = String.format(subjectStringformat, internObject.getTitle(), internObject.getCreateDate());
        out.println(subject);
        Utils.writeToLog(subject);
        Utils.sendEmail(subject, sb2.toString());
    }

    private List<CareerObject> getCareerObjectListFromLiList(List<String> liList) {

        List<CareerObject> list = new ArrayList<CareerObject>();
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
                String link = urlJobnHost + matcher.group(1).trim();
                String title = matcher.group(3).trim();
                String createDate = matcher.group(4).trim();

                CareerObject internObject = new CareerObject(id, title, createDate, link);
                list.add(internObject);
            }
        }
        return list;
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
            Utils.writeToLog("Does not find any matches for li");
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
            Utils.writeToLog("Does not find any matches for ul");
            return null;
        }
        return ul.toString();
    }

    private void ReadMostRecentPost() {
        Properties properties = readProperties(configString);
        this.lastCreateDate = properties.getProperty(propCreateDate);
        this.lastTitles = properties.getProperty(propTitle);
    }
}
