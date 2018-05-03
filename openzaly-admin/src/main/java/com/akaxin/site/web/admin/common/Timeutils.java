package com.akaxin.site.web.admin.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timeutils {
    //返回前num天的日期
    public static String getDate(Integer num) {
        if (num != null && num >= 1) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(new Date(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(num)));
        }
        return null;
    }
}
