package com.pkucareer.intern;

import static com.webmoni.util.Utils.dateStringComparator;
import static com.webmoni.util.Utils.isSameDateString;
import static com.webmoni.util.Utils.readProperties;
import static java.lang.System.out;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.text.SimpleDateFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.webmoni.util.Utils;

public abstract class SubscribeClassBase {
    protected String urlJobnHost = "http://scc.pku.edu.cn/";
    protected String configString = "ddusubsribe.properties";

    protected String elementsPattern = "table#articleList tbody tr";


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

        List<CareerObject> list = getCareerObjectListFromURL(urlJobBase);
        Collections.sort(list);

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

            Elements elements = doc.select("div.articleContainer");
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
        Utils.sendEmail(subject, sb.toString());
    }

    private List<CareerObject> getCareerObjectListFromURL(String urlJobBase) {
        List<CareerObject> list = new ArrayList<CareerObject>();
        Document doc;

        try {
            doc = Jsoup.connect(urlJobBase).get();
            Elements elements = doc.select(elementsPattern);
            for (Element element : elements) {
                String title = element.child(1).text();
                String date = element.child(2).text().split(" ")[0];

                String link = "";

                for (Element element2 : element.child(1).children()) {
                    String tmp = element2.attr("abs:href");
                    if (tmp.length() > 0 && tmp.indexOf("html") > -1) {
                        link = tmp;
                        break;
                    }
                }

                list.add(new CareerObject(title, date, link));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }

    private void ReadMostRecentPost() {
        Properties properties = readProperties(configString);
        this.lastCreateDate = properties.getProperty(propCreateDate);
        this.lastTitles = properties.getProperty(propTitle);
    }
}
