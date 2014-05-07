package com.webmoni.util;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.webmoni.mail.MailSenderInfo;
import com.webmoni.mail.SimpleMailSender;

public class Utils {

    /**
     * 
     * @param urlString
     *            Get web content based on url string </br> Default encoding is UTF-8
     * @return
     */
    public static String RetriveWebContent(String urlString) {
        return RetriveWebContent(urlString, "UTF-8");
    }

    /**
     * 
     * @param urlString
     *            Get web content based on url
     * @param charset
     *            Specify the web page charset
     * @return
     */
    public static String RetriveWebContent(String urlString, String charset) {
        StringBuffer sb = new StringBuffer();
        URL url;
        String temp;
        try {
            url = new URL(urlString);
            // 读取网页全部内容
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
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

    /**
     * 
     * @param filepath
     *            File location with default UTF-8 encoding
     * @return
     */
    public static Properties readProperties(String filepath) {
        return readProperties(filepath, "UTF-8");
    }

    /**
     * 
     * @param filepath
     *            File location
     * @param charset
     *            Specify the file encoding
     * @return
     */
    public static Properties readProperties(String filepath, String charset) {

        // 读取配置文件
        Properties properties = new Properties();
        File file = new File(filepath);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader aInputStreamReader = new InputStreamReader(fis, charset);
            properties.load(aInputStreamReader);

            fis.close();
            aInputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public static void writeProperties(String filepath, String key, String value) {
        writeProperties(filepath, key, value, "UTF-8");
    }

    public static void writeProperties(String filepath, String key, String value, String charset) {
        Properties properties = readProperties(filepath);
        properties.setProperty(key, value);

        File file = new File(filepath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, charset);
            properties.store(outputStreamWriter, "update");
            fos.close();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String subject, String content) {

        String emailConfigName = "email.properties";
        Properties properties = Utils.readProperties(emailConfigName);

        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(properties.getProperty("emailSmtp"));
        mailInfo.setMailServerPort(properties.getProperty("emailPort"));
        mailInfo.setValidate(true);
        mailInfo.setUserName(properties.getProperty("emailUsername"));
        mailInfo.setPassword(properties.getProperty("emailPassword"));// 您的邮箱密码
        mailInfo.setFromAddress(properties.getProperty("emailFrom"));
        mailInfo.setToAddress(properties.getProperty("emailTo"));
        mailInfo.setSubject(subject);
        mailInfo.setContent(content);

        SimpleMailSender.sendHtmlMail(mailInfo);

    }
}
