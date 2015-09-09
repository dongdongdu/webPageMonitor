package com.pkucareer.intern;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class SubscribePortalInfo {

    String infoURL = "https://portal.pku.edu.cn/portal2013/jsp/oa/deptNoticeList.jsp";
    String host = "https://portal.pku.edu.cn";
    String elementsPattern = "#item_list p";

    public void ProcessInfo() {
        String sourceString = getURLSource(infoURL);
        List<CareerObject> list = getCareerObjectsFromSource(sourceString);

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

}
