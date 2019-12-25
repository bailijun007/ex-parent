package com.hp.sh.expv3.pc.extension.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间工具类
 *
 * @author BaiLiJun  on 2019/12/25
 */
public class DateUtil {
    /**
     * ----时间格式化类
     */
    public static final String DATE_FORMAT_NORMAL = "yyyy-MM-dd";

    /**
     * @param start_date //开始日期
     * @param end_date   //结束日期
     * @return 几天
     * @throws Exception
     */
    public static int getDayLength(String start_date, String end_date) throws Exception {
        Date fromDate = getStrToDate(start_date, DATE_FORMAT_NORMAL); //开始日期
        Date toDate = getStrToDate(end_date, DATE_FORMAT_NORMAL); //结束日期
        long from = fromDate.getTime();
        long to = toDate.getTime();
        //一天等于多少毫秒：24*3600*1000
        int day = (int) ((to - from) / (24 * 60 * 60 * 1000));
        return day;
    }

    public static Date getStrToDate(String date, String fomtter) throws Exception {
        DateFormat df = new SimpleDateFormat(fomtter);
        return df.parse(date);
    }

    /**
     * 方法名：getCurrTime
     * 描述：     获取当前时间，格式为：yyyy-MM-dd HH:mm:ss
     * 参数：    @return
     *
     * @return: String
     */
    public static String getCurrTime() {
        SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ss.format(new Date());
    }

    /**
     * 方法名：getCurrTime
     * 描述：     获取当前时间，格式为：yyyy-MM-dd HH:mm:ss
     * 参数：    @return
     *
     * @return: String
     */
    public static String getCurrTimes(long current) {
        SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return ss.format(current);
    }

    /**
     * 方法名：findFormatDate
     * 描述：     获取当前时间，格式为：yyyy-MM-dd
     * 参数：    @return
     *
     * @return: String
     */
    public static String findFormatDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }


    /**
     * 方法名：findFormatDates
     * 描述：     获取当前时间，格式为：yyyyMMdd
     * 参数：    @return
     *
     * @return: String
     */
    public static String findFormatDates() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 方法名：formatDateInNumber
     * 描述：     获取当前时间，格式为：yyyyMMdd
     * 参数：    @return
     *
     * @return: String
     */
    public static String formatDateInNumber(Date date) {
        SimpleDateFormat ss = new SimpleDateFormat("yyyyMMdd");
        return ss.format(date);
    }

    /**
     * 方法名：findLatelyDate
     * 描述：    获取最近的时间   格式：yyyy-MM-dd
     * 参数：    @param number 时间偏移量  +1 当前时间加1天      -1 当前时间减一天
     * 参数：    @return
     *
     * @return: String
     */
    @SuppressWarnings("static-access")
    public static String findLatelyDate(int number) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, number);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        String time = formatter.format(date);
        return time;
    }

    /**
     * 方法名：findFormatDateHour
     * 描述：    获取当前时间，格式为：HH:mm:ss
     * 参数：    @return
     *
     * @return: String
     */
    public static String findFormatDateHours() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * 方法名：findFormatDateHour
     * 描述：    获取当前时间，格式为：HH-mm-ss
     * 参数：    @return
     *
     * @return: String
     */
    public static String findFormatDateHour() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
    }

    /**
     * 方法名：DateFormatOrderNum
     * 描述：    生成订单号格式 yyyyMMddHHmmss
     * 参数：    @return
     *
     * @return: String
     */
    public static String DateFormatOrderNum() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();//取时间
        return formatter.format(date);
    }

    /**
     * 方法名：getLast12Months
     * 描述：    获取最近12个月的月份
     * 参数：     @param index 月份偏移量  +1 当前月份加1月      -1 当前月份减一月份
     * 参数：    @return
     *
     * @return: String
     */
    public static String getLast12Months(int index) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, index);
        Date m = c.getTime();
        return sdf.format(m);
    }


    /**
     * 格式化日期，格式为：yyyy-MM-dd
     *
     * @return
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 格式化日期，格式为：yyyyMMdd
     *
     * @return
     */
    public static String formatDateInNumber(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 格式化时间，格式为：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String formatDateTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static void main(String[] args) {
        String a = getLast12Months(0);
        System.out.println(a);
    }

}
