/*
 * net/balusc/webapp/FileServlet.java
 *
 * Copyright (C) 2009 BalusC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.xxmd.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServlet;

public class TimeUtil extends HttpServlet {
    public static Long parseTimeStr(String timeStr) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date reference = null;
        try {
            reference = dateFormat.parse("00:00:00");
            Date date = dateFormat.parse(timeStr);
            return date.getTime() - reference.getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTimeStr(Long time, boolean removeZero) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GTM+0"));
        String format = simpleDateFormat.format(time);
        if (removeZero && format.startsWith("00:")) {
            format = format.replaceFirst("00:", "");
        }
        return format;
    }
}