Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1,                 //月份 
        "d+": this.getDate(),                    //日 
        "h+": this.getHours(),                   //小时 
        "m+": this.getMinutes(),                 //分 
        "s+": this.getSeconds(),                 //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds()             //毫秒 
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//example New Data("").Format("yyyy-MM-dd");
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    return currentdate;
}
/*
是否是闰年

*/
function IsLeapYear(dateValue) {
    var year = dateValue.getYear();
    return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
}

/*
返回月的第一天 
*/
function BeginOfMonth(dateValue) { 
    return new Date(dateValue.getFullYear(), dateValue.getMonth(), 1);
}

/*
返回月的最后一天 
*/
function EndOfMonth(dateValue) {
    var iMonth = dateValue.getMonth() + 1;
    var iDate;
    switch (iMonth) {
        case 4:
        case 6:
        case 9:
        case 11:
            iDate = 30;
            break;
        case 2:
            if (IsLeapYear(dateValue)) {
                iDate = 29;
            }
            else {
                iDate = 28;
            }
            break;
        default:
            iDate = 31;
            break;
    }
    return new Date(dateValue.getFullYear(),
					dateValue.getMonth(),
					iDate);
} 

/*返回上月*/
function getLastMonth(dateValue) {
    var year = dateValue.getFullYear();       //获取当前日期的年
    var month = dateValue.getMonth() + 1;     //获取当前日期的月
    var day = dateValue.getDate();            //获取当前日期的日
    var days = new Date(year, month, 0);
    days = days.getDate(); //获取当前日期中的月的天数

    var year2 = year;
    var month2 = parseInt(month) - 1;
    if (month == 1) {
        year2 = parseInt(year2) - 1;
        month2 = 12;
    }
    var day2 = day;
    var days2 = new Date(year2, month2, 0);
    days2 = days2.getDate();
    if (day2 > days2) {
        day2 = days2;
    }
    if (month2 < 10) {
        month2 = '0' + month2;
    }

    var t2 = year2 + '-' + month2 + '-' + day2;
    return new Date(Date.parse(t2.replace(/-/g, "/")));
}

/*返回下月*/
function getNextMonth(dateValue) {
    var year = dateValue.getFullYear();       //获取当前日期的年
    var month = dateValue.getMonth() + 1;     //获取当前日期的月
    var day = dateValue.getDate();            //获取当前日期的日
    var days = new Date(year, month, 0);
    days = days.getDate(); //获取当前日期中的月的天数

    var year2 = year;
    var month2 = parseInt(month) + 1;
    if (month == 11) {
        year2 = parseInt(year2) + 1;
        month2 = 0;
    }
    var day2 = day;
    var days2 = new Date(year2, month2, 0);
    days2 = days2.getDate();
    if (day2 > days2) {
        day2 = days2;
    }
    if (month2 < 10) {
        month2 = '0' + month2;
    }

    var t2 = year2 + '-' + month2 + '-' + day2;
    return new Date(Date.parse(t2.replace(/-/g, "/")));
}

/*
返回年的第一天 
*/
function BeginOfYear(dateValue) { 
    return new Date(dateValue.getFullYear(), 0, 1);
}

/*
返回年的最后一天 
*/
function EndOfYear(dateValue) {
    return new Date(dateValue.getFullYear(), 11, 31);
}

/*设定日期格式*/
function GetFormatDate(myDate) {
    if (myDate != null) {
        if (myDate.indexOf("/Date(") > -1)
            return parseInt(myDate.substr(6)) == -62135596800000 ? "" : new Date(parseInt(myDate.substr(6))).Format("yyyy-MM-dd");
        else
            return new Date(myDate).Format("yyyy-MM-dd");
    }
    return "";
} 

/*设定时间格式*/
function GetFormatTime(myDate) {
    if (myDate != null) {
        if (myDate.indexOf("/Date(") > -1)
            return parseInt(myDate.substr(6)) == -2209017600000 ? "" : new Date(parseInt(myDate.substr(6))).Format("hh:mm");
        else
            return new Date(myDate).Format("hh:mm");
    }
    return "";
}


//日期加上天数得到新的日期  
//dateTemp 需要参加计算的日期，days要添加的天数，返回新的日期，日期格式：YYYY-MM-DD  
function getNewDay(dateTemp, days) {      
    var millSeconds = Math.abs(dateTemp) + (days * 24 * 60 * 60 * 1000);  
    var rDate = new Date(millSeconds);   
    return rDate;
}

/*返回指定日期几个月后的日期*/
function getNewMonth(dateValue, addMonth, isMinus) {
    var year = dateValue.getFullYear();       //获取当前日期的年
    var month = dateValue.getMonth() + 1;     //获取当前日期的月
    var day = dateValue.getDate();            //获取当前日期的日
    var days = new Date(year, month, 0);
    days = days.getDate(); //获取当前日期中的月的天数

    var year2 = year;
    var month2 = parseInt(month) + parseInt(addMonth);
    var monthCount = Math.ceil(month2 / 24);
    if (month2 > 11) {
        year2 = parseInt(year2) + monthCount;
        month2 = month2 - 12 * monthCount;
    }
    var day2 = day;
    var days2 = new Date(year2, month2, 0);
    days2 = days2.getDate();
    if (day2 > days2) {
        day2 = days2;
    } 

    var newDate = new Date(year2, month2-1, day2);
    if (isMinus == true) { 
        var newDate2 = Math.abs(newDate) - (24 * 60 * 60 * 1000);
        return new Date(newDate2).Format("yyyy-MM-dd");
    }
    else
        return newDate;
}


/*获取两个时间之间的小时差*/
function GetDateDiff(startTime, endTime) {
    startTime = startTime.replace(/-/g, "/");
    endTime = endTime.replace(/-/g, "/");
    var date3 = new Date(endTime).getTime() - new Date(startTime).getTime()  //时间差的毫秒数

    //计算出相差天数
    var days = Math.floor(date3 / (24 * 3600 * 1000))

    //计算出小时数

    var leave1 = date3 % (24 * 3600 * 1000)    //计算天数后剩余的毫秒数
    var hours = Math.floor(leave1 / (3600 * 1000))
    //计算相差分钟数
    var leave2 = leave1 % (3600 * 1000)        //计算小时数后剩余的毫秒数
    var minutes = Math.floor(leave2 / (60 * 1000))
    return parseFloat(hours + minutes / 60).toFixed(1);
}

/*计算两个日期之间的天数差*/
function DateDiff(startTime, endTime) {
    startTime = startTime.replace(/-/g, "/");
    endTime = endTime.replace(/-/g, "/");
    var date3 = new Date(endTime).getTime() - new Date(startTime).getTime()  //时间差的毫秒数

    //计算出相差天数
    var days = Math.floor(date3 / (24 * 3600 * 1000));
    return days;
}
/*将后台返回的/Date(1448954018000)/格式转换为指定的格式*/
var TODateFormat = function (obj, fmt) {
    if (obj) {
        var value = obj;
        fmt = fmt ? fmt : 'yyyy-MM-dd';
        var reg = /\/Date\(([-]?\d+)\)\//gi;
        if (reg.test(value)) {
            var msec = value.toString().replace(reg, "$1");
            value = new Date(parseInt(msec)).Format(fmt);
        }
        return value.replace("T", " ");
    }
    else
        return "";
}

//格式化时间
function GetTime(JSONDateString) {
    var d = new Date(parseInt(JSONDateString.replace("/Date(", "").replace(")/", ""), 10));
    return d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds();
}
function addDate(date, days) {
    var d = new Date(date);
    var d_s = d.getTime();
    var nt = new Date();
    nt.setTime(d_s + 1000 * 60 * 60 * 24);
    return nt;
}

function convertDate2String(date) {
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var day = date.getDate();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    var val = year + "-" + month + "-" + day;

    return val;
}