package com.mavin.mgaapp.commons.utlis;

import java.security.MessageDigest;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/2.
 */

public final class JavaUtil {


    public static String MD5(String pwd) {
        char md5String[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = pwd.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = md5String[byte0 >>> 4 & 0xf];
                str[k++] = md5String[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatDate2String(Date date, String template) {
        if (date == null) {
            return "";
        }
        return new java.text.SimpleDateFormat(template).format(date);
    }

    /**
     * 判断字符串是否为null, "", "       "
     * @param string
     * @return
     */
    public static boolean isEmptyString(String string) {
        if (string == null) {
            return true;
        }
        return string.trim().length()==0;
    }
}
