package com.lht.cloudjob.util.time;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;

import com.lht.cloudjob.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * <p><b>Package</b> com.lht.cloudjob.util.time
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> TimeUtil
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/6/21.
 */
public class TimeUtil {

    public static final int DAY_MILLIS = 1000 * 60 * 60 * 24;

    public static final int HOUR_MILLIS = 1000 * 60 * 60;

    public static final int MINUTE_MILLIS = 1000 * 60;

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd " +
            "HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd",
            Locale.CHINA);

    private TimeUtil() {
        throw new AssertionError();
    }

    /**
     * long time to string0
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }


//    public static long transMillis2Sec(long timeInMillis) {
//        if (timeInMillis > 9999999999l) {
//            return timeInMillis/1000;
//        } else {
//            throw new IllegalArgumentException("you should give a time in millis");
//        }
//    }


//    public static boolean isTimeInMillis(long timestamp) {
//        return timestamp>9999999999l;
//    }
//
//    //不考虑负值，也不去检验
//    public static boolean isTimeInSecond(long timestamp) {
//        return timestamp<=9999999999l;
//    }

    public static int getYear(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        /**
         * because JANUARY is 0
         * @see Calendar#JANUARY
         * */
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getDay(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.DATE);
    }

    public static int getHour(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecond(long timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);
        return calendar.get(Calendar.SECOND);
    }

    public static DateTransformer newDateTransformer(long timestamp) {
        return new DateTransformer(timestamp);
    }

    public static DateTransformer newDateTransformer() {
        return new DateTransformer(getCurrentTimeInLong());
    }

    public static DateTransformer newDateTransformer(int year, int month, int day) {
        return new DateTransformer(year, month, day);
    }

    public static class DateTransformer {
        private GregorianCalendar calendar;

        DateTransformer(long stamp) {
            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(stamp);
        }

        DateTransformer(int year, int month, int day) {
            calendar = new GregorianCalendar();
            calendar.set(year, month, day);
        }

        public int getYear() {
            return calendar.get(Calendar.YEAR);
        }

        public int getMonth() {
            //as we all know :int JANUARY = 0;
            return calendar.get(Calendar.MONTH) + 1;
        }

        public int getDay() {
            return calendar.get(Calendar.DATE);
        }

        public long getMillisInLong() {
            return calendar.getTimeInMillis();
        }

        public String getDefaultFormat() {
            String format = "%d-%d-%d";
            return String.format(format, getYear(), getMonth(), getDay());
        }
    }

    public static long getTodayBeginning() {
        long _c = getCurrentTimeInLong();
        int year = getYear(_c);
        int month = getMonth(_c);
        int day = getDay(_c);
        return newDateTransformer(year, month, day).getMillisInLong();
    }

    public static DatePickerDialog newDatePickerDialog(Context context, DatePickerDialog
            .OnDateSetListener dateSetListener) {
        return newDatePickerDialog(context, getCurrentTimeInLong(), dateSetListener);
    }

    public static DatePickerDialog newDatePickerDialog(Context context, long timeStamp,
                                                       DatePickerDialog.OnDateSetListener
                                                               dateSetListener) {
        TimeUtil.DateTransformer dateTransformer = TimeUtil.newDateTransformer(timeStamp);
        DatePickerDialog dialog;
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            dialog = newDatePickerDialog_SDK21(context, dateSetListener,
                    dateTransformer.getYear(),
                    dateTransformer.getMonth() - 1,
                    dateTransformer.getDay());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int theme = android.R.style.Theme_Material_Light_Dialog_Alert;
            dialog = new DatePickerDialog(context, theme, dateSetListener,
                    dateTransformer.getYear(),
                    dateTransformer.getMonth() - 1,
                    dateTransformer.getDay());
        } else {
            dialog = new DatePickerDialog(context, dateSetListener,
                    dateTransformer.getYear(),
                    dateTransformer.getMonth() - 1,
                    dateTransformer.getDay());
        }
        return dialog;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static DatePickerDialog newDatePickerDialog_SDK21(Context context, DatePickerDialog
            .OnDateSetListener dateSetListener, int year, int month, int day) {
        return new DatePickerDialog(context, R.style.MDatePicker, dateSetListener, year, month,
                day);
    }

}
