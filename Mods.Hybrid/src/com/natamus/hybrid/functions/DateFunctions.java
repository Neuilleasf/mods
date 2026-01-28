/*
 * Decompiled with CFR 0.152.
 */
package com.natamus.hybrid.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFunctions {
    public static String ymdhisToReadable(String ymdhis) {
        SimpleDateFormat ymdhisformat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat readableformat = new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss");
        try {
            return readableformat.format(ymdhisformat.parse(ymdhis));
        }
        catch (ParseException e) {
            return ymdhis;
        }
    }

    public static String getNowInYmdhis() {
        Date now = new Date();
        return new SimpleDateFormat("yyyyMMddHHmmss").format(now);
    }
}
