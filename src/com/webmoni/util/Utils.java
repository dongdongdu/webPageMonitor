package com.webmoni.util;

import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

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
            Utils.writeToLog("你输入的URL格式有问题");
            e.printStackTrace();
            Utils.writeToLog(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Utils.writeToLog(e.getMessage());
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

    /**
     * 
     * @param filepath
     * @param properties
     *            <p>
     *            Run readProperties to read all of the existing settings before run this method, because it will replace all of
     *            the existing contents.
     *            </p>
     */
    public static void writeProperties(String filepath, Properties properties) {
        File file = new File(filepath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, "UTF-8");
            properties.store(outputStreamWriter, "update");
            fos.close();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mailInfo.setSubject(subject);
        mailInfo.setContent(content);

        String[] tos = properties.getProperty("emailTo").split(";");
        for (String string : tos) {
            mailInfo.setToAddress(string.trim());
            SimpleMailSender.sendHtmlMail(mailInfo);
        }

        try {
            Random rnd = new Random();
            // Get a random number between 1 to 10 times 1000 to the seconds
            int waitTime = (rnd.nextInt(10) + 1) * 1000;
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            out.println("catch exception here");
            Utils.writeToLog("catch exception here");
            e.printStackTrace();
        }
    }

    public static Comparator<String> dateStringComparator = new Comparator<String>() {
        public int compare(String o1, String o2) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date d1 = null;
            java.util.Date d2 = null;
            try {
                d1 = df.parse(o1);
                d2 = df.parse(o2);
            } catch (ParseException e) {
                out.println("Error when parsing dateString, dateString should be in yyyy-MM-dd format, example like 2011-01-01.");
                Utils.writeToLog("Error when parsing dateString, dateString should be in yyyy-MM-dd format, example like 2011-01-01.");
                e.printStackTrace();
            }
            return d1.compareTo(d2);
        }
    };

    public static void writeToLog(String message) {
        String logfile = "log.log";
        File file = new File(logfile);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;

            if (file.length() > 1000000) {
                // If file size > 1M, reset
                fos = new FileOutputStream(file, false);
            } else {
                fos = new FileOutputStream(file, true);
            }

            osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);

            Date date = new Date();
            bw.write(String.format("%s %s", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ").format(date), message));
            bw.newLine();

            bw.close();
            osw.close();
            fos.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param oldDateString
     * @param newDateString
     * 
     * @return
     */
    public static boolean isSameDateString(String dateString1, String dateString2) {
        int a = dateStringComparator.compare(dateString1, dateString2);
        return a == 0 ? true : false;
    }

}
