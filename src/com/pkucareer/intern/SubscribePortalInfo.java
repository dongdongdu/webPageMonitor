package com.pkucareer.intern;

import static com.webmoni.util.Utils.dateStringComparator;
import static com.webmoni.util.Utils.isSameDateString;
import static com.webmoni.util.Utils.readProperties;
import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.webmoni.util.Utils;

public class SubscribePortalInfo {

    String infoURL = "https://portal.pku.edu.cn/portal2013/jsp/oa/deptNoticeList.jsp";
    String host = "https://portal.pku.edu.cn";
    String elementsPattern = "#item_list p";

    String infoContentPattern = "div.articleContainer";
    String subjectStringformat = "信息门户公告 - %s %s";
    String configString = "ddusubsribe.properties";
    String noPostString = "No new post for portal news!";

    String propCreateDate = "lastPortalInfoDate";
    String propTitle = "lastPortalInfoTitle";

    String lastCreateDate = null;
    String lastTitles = null;

    public void ProcessInfo() {
        String sourceString = getURLSource(infoURL);
        List<CareerObject> list = getCareerObjectsFromSource(sourceString);

        if (lastCreateDate == null || lastTitles == null) {
            ReadMostRecentPost();
        }

        sendInfo(list);
    }

    private void sendInfo(List<CareerObject> list) {
        String newCreateDate = "";
        String newTitles = "";
        List<CareerObject> sendList = new ArrayList<CareerObject>();

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
            } else {
                // a==0 说明日期都一样,这里要判断是否同一天有新post出来
                int idx = lastTitles.indexOf(o.getTitle());
                // 这里idx==-1 说明是最新的post，上次保存的title里没有
                if (idx == -1) {
                    sendList.add(o);
                }
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

    private void notifyNewPost(CareerObject internObject) {
        StringBuffer sb = new StringBuffer();

        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(internObject.getLinkURL()).get();
            Elements elements = doc.select(infoContentPattern);
            sb.append(elements.toString());

            // 添加post的链接
            String templateString = "<ul><li><a href=\"%s\">%s</a></li></ul>";
            String objLink = internObject.getLinkURL();
            String link = String.format(templateString, internObject.getLinkURL(), objLink, objLink);
            sb.append(link);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String subject = String.format(subjectStringformat, internObject.getCreateDate(), internObject.getTitle());
        out.println(subject);
        Utils.writeToLog(subject);
        // Utils.sendEmail(subject, sb.toString());
    }

    private List<CareerObject> getCareerObjectsFromSource(String sourceString) {
        List<CareerObject> list = new ArrayList<CareerObject>();
        Document doc = Jsoup.parse(sourceString);
        Elements elements = doc.select(elementsPattern);
        for (Element element : elements) {
            String department = element.child(0).text(); // department
            String title = element.child(1).text(); // title
            String link = host + element.child(1).attr("href"); // link
            String date = element.childNode(4).toString(); // date
            list.add(new CareerObject(title, date, link, department));
        }
        Collections.sort(list);
        return list;
    }

    private String getURLSource(String url) {
        WebDriver driver = new HtmlUnitDriver(true);
        driver.get(url);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 人事部，组织部，研究生院，学生工作部
        String source = driver.getPageSource();
        return source;
    }

    private void ReadMostRecentPost() {
        Properties properties = readProperties(configString);
        this.lastCreateDate = properties.getProperty(propCreateDate);
        this.lastTitles = properties.getProperty(propTitle);
    }

}
