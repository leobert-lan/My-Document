package com.lht.cloudjob.util.identify;

import com.lht.cloudjob.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.identify
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IdentifyCardUtil
 * <p><b>Description</b>: 身份证工具类
 * <a href="http://music.163.com/#/m/song?id=36308916&userid=276041459" target="_blank">专属BGM</a>
 * <p/>
 * Created by leobert on 2016/6/21.
 */
public class IdentifyCardUtil {

    public static final String REG_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";

    public static final String REG_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";

    public static boolean isMainLandIdNo(String id) {
       if (getGeneration(id) == Generation.INVALID)
           return false;
        else
           return true;
    }

    /**
     * 匹配第几代
     * @param id 号码
     * @return
     */
    public static Generation getGeneration(String id) {
        String _id = format(id);
        if (isValidGenerationOneFormat(_id)) {
            if (isG1DateValid(_id)) {
                Generation ret = Generation.ONE;
                ret.setIdNo(_id);
                return ret;
            } else {
                Generation ret = Generation.INVALID;
                ret.setIdNo(_id);
                return ret;
            }
        } else if (isValidGenerationTwoFormat(_id)) {
            if (isG2DateValid(_id) && isG2CheckDigitValid(_id)) {
                Generation ret = Generation.TWO;
                ret.setIdNo(_id);
                return ret;
            } else {
                Generation ret = Generation.INVALID;
                ret.setIdNo(_id);
                return ret;
            }
        } else{
            Generation ret = Generation.INVALID;
            ret.setIdNo(_id);
            return ret;
        }
    }


    private static String format(String id) {
        return StringUtil.nullStrToEmpty(id).trim().toUpperCase();
    }


    /**
     * desc: 是否是结构合法的第一代身份证号
     *
     * @param id
     * @return
     */
    public static boolean isValidGenerationOneFormat(String id) {
        Pattern p = Pattern.compile(REG_15);
        Matcher matcher = p.matcher(id.trim());
        return matcher.matches();
    }

    /**
    * desc: 是否是结构合法的第二代身份证号
    */
    public static boolean isValidGenerationTwoFormat(String id) {
        Pattern p = Pattern.compile(REG_18);
        Matcher matcher = p.matcher(id.trim());
        return matcher.matches();
    }

    private static int[] iaMonthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * desc: 是否闰年
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * desc: 二代身份证日期是否科学合理
     */
    private static boolean isG2DateValid(String id) {
        String date8 = id.substring(6, 14);
        int year, month, day;
        year = Integer.parseInt(date8.substring(0, 4));
        month = Integer.parseInt(date8.substring(4, 6));
        day = Integer.parseInt(date8.substring(6, 8));
        if (year < 1700 || year > 2500)
            return false;
        if (isLeapYear(year))
            iaMonthDays[1] = 29;
        if (month < 1 || month > 12)
            return false;
        if (day < 1 || day > iaMonthDays[month - 1])
            return false;
        return true;
    }

    /**
     * desc: 一代身份证日期是否科学合理
     */
    private static boolean isG1DateValid(String id) {
        String date6 = id.substring(6, 12);
        int year, month;
        year = Integer.parseInt(date6.substring(0, 4));
        month = Integer.parseInt(date6.substring(4, 6));
        if (year < 1700 || year > 2500)
            return false;
        if (month < 1 || month > 12)
            return false;
        return true;
    }

    private static boolean isG2CheckDigitValid(String id) {
        int[] factorArr = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1}; // 系数因子
        String[] parityBit = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"}; // 校验位
        int lngProduct = 0;
        for (int i = 0; i < 17; i++) {
            lngProduct = lngProduct + factorArr[i] * Integer.parseInt(id.substring(i, i + 1));
        }

        String _digit = id.substring(17);
        int checkDigit = lngProduct % 11;
        if (checkDigit == 2)
            return _digit.equals("x") || _digit.equals("X");

        return _digit.equals(parityBit[checkDigit]);
    }


    /**
     * 身份证代
     */
    public enum Generation {
        ONE, TWO, INVALID;

        String idNo;

        public void setIdNo(String id) {
            idNo = StringUtil.nullStrToEmpty(id).trim().toUpperCase();
        }

        public String getNo() {
            return idNo;
        }
    }

}
