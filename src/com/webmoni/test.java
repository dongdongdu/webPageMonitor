package com.webmoni;

import static java.lang.System.out;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) {

        String aa = "<ul class=\"wz1ul\"><li class=\"heicu\" style=\"width:80%;margin:0 auto;padding-top:30px;padding-bottom:10px;border-bottom:1px #ccc solid;\">【招聘】国资委新闻中心2014年公开招聘应届毕业生公告</li><li style=\"line-height:26px;\">2014-05-15 &nbsp;&nbsp;阅读次数：87次 </li><li class=\"wz1text\" style=\"margin:0 84px 0 84px; font-size:11pt;\"></li></ul>";

        Pattern p = Pattern.compile("sty1le=\".*?\"");
        Matcher m = p.matcher(aa);
        String bb = m.replaceAll("");

        out.println(bb);

    }

}
