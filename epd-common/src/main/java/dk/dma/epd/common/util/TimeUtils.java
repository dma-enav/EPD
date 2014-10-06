/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for handling date and time.
 */
public class TimeUtils {

    /**
     * Resets the time part of the date to 0:0:0
     * @param cal the date to reset
     * @return the reset date
     */
    public static Calendar resetTime(Calendar cal) {
        if (cal != null) {
            cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
            cal.set(Calendar.MINUTE, 0);                 // set minute in hour
            cal.set(Calendar.SECOND, 0);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        }
        return cal;
    }

    /**
     * Resets the time part of the date to 0:0:0
     * @param date the date to reset
     * @return the reset date
     */
    public static Date resetTime(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(date);                           // set cal to date
            date = resetTime(cal).getTime();
        }
        return date;
    }

    /**
     * Resets the seconds part of the date to 0
     * @param cal the date to reset
     * @return the reset date
     */
    public static Calendar resetSeconds(Calendar cal) {
        if (cal != null) {
            cal.set(Calendar.SECOND, 0);                 // set second in minute
            cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        }
        return cal;
    }

    /**
     * Resets the seconds part of the date to 0
     * @param date the date to reset
     * @return the reset date
     */
    public static Date resetSeconds(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();       // get calendar instance
            cal.setTime(date);                           // set cal to date
            date = resetSeconds(cal).getTime();
        }
        return date;
    }

    /**
     * Checks if the two Dates is for the same date
     * @param date1 the first date
     * @param date2 the second date
     * @return if the two Dates is for the same date
     */
    public static boolean sameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return resetTime(date1).getTime() == resetTime(date2).getTime();
    }

    /**
     * Checks if the two Dates is for the same date, hour and minute
     * @param date1 the first date
     * @param date2 the second date
     * @return if the two Dates is for the same date, hour and minute
     */
    public static boolean sameDateHourMinute(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return resetSeconds(date1).getTime() == resetSeconds(date2).getTime();
    }
}
