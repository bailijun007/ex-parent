package com.hp.sh.expv3.pc.extension.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/9
 */
public final class CommonDateUtils {


    /**
     * 计算2个字符串之间的月份差
     *
     * @param str
     * @param str2
     * @return 所有月份
     */
    public static List<Integer> getTimeDifference(String str, String str2) {
        String[] split = str.split("-");
        String[] split2 = str2.split("-");

        String s = split[0] + split[1];
        String s2 = split2[0] + split2[1];

        int p1 = Integer.parseInt(s);
        int p2 = Integer.parseInt(s2);

        List<Integer> list = new ArrayList<>();
        for (int i = p1; i <= p2; i++) {
            Boolean b = isData("yyyyMM",i + "");
            if (b) {
                list.add(i);
            }
        }

        return list;
    }


    /**
     * 字符串转时间戳
     *
     * @param str
     * @return
     */
    public static Long stringToTimestamp(String str) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startLdt = LocalDate.parse(str, dtf);
        Long begin = startLdt.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        return begin;
    }


    /**
     * 时间戳转String
     *
     * @param timestamp
     * @return
     */
    public static String timestampToString(Long timestamp) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate beginDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return dtf.format(beginDate);
    }

    /**
     * LocalDate 转时间戳
     *
     * @param localDate
     * @return
     */
    public static Long localDateToTimestamp(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime 转时间戳
     *
     * @param localDateTime
     * @return
     */
    public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    public static String getDefaultDateTime(String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        //如果开始时间，结束时间没有值则给默认今天时间
        if (org.springframework.util.StringUtils.isEmpty(startTime)) {
            startTime = formatter.format(dateTime);
        }
        return startTime;
    }


    /**
     * 判断是否是日期
     *
     * @param pattern 指定的格式
     * @param s 需格式化的字符串
     * @return
     */
    public static Boolean isData(String pattern, String s) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        try {
            Date date = formatter.parse(s);  //抛出转换异常
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // String -->LocalDate
        public static LocalDate stringToLocalDate(CharSequence text) {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(text, pattern);
            return localDate;
        }

        public static String[] getStartAndEndTime(String startTime, String endTime) {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (org.apache.commons.lang3.StringUtils.isEmpty(startTime)) {
                startTime = getDefaultDateTime(startTime);
                LocalDate startDate = stringToLocalDate(startTime);
                startTime = startDate.format(pattern);
                LocalDate endDate = startDate.plusDays(1);
                endTime = endDate.format(pattern);
            }
        String[] startAndEndTime = {startTime, endTime};
        return startAndEndTime;
    }

}
