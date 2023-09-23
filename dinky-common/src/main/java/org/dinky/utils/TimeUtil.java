/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The TimeUtil class provides utility methods for working with date and time operations.
 */
public class TimeUtil {

    /**
     * Returns the current date and time as a formatted string in the "yyyy-MM-dd HH:mm:ss" format.
     *
     * @return The current date and time as a formatted string.
     */
    public static String nowStr() {
        return nowStr("yyyy-MM-dd HH:mm:ss");
    }

    public static Long nowTimestamp() {
        return localDateTimeToLong(LocalDateTime.now());
    }

    /**
     * Returns the current date and time as a formatted string based on the provided format.
     *
     * @param formate The desired date and time format.
     * @return The current date and time as a formatted string.
     */
    public static String nowStr(String formate) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formate);
        return now.format(formatter);
    }

    /**
     * Converts a Long timestamp to a String in the "yyyy-MM-dd HH:mm:ss" format.
     *
     * @param time The Long timestamp to convert.
     * @return A formatted String representation of the timestamp.
     */
    public static String convertTimeToString(Long time) {
        return convertTimeToString(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String convertTimeToString(LocalDateTime time) {
        return convertTimeToString(
                time.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Converts a Long timestamp to a String based on the provided format.
     *
     * @param time    The Long timestamp to convert.
     * @param formate The desired date and time format.
     * @return A formatted String representation of the timestamp.
     */
    public static String convertTimeToString(Long time, String formate) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern(formate);
        return dateTimeFormater.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    /**
     * Converts a date and time String to a Long timestamp.
     *
     * @param time The date and time String in "yyyy-MM-dd HH:mm:ss" format.
     * @return A Long timestamp.
     */
    public static Long convertTimeToLong(String time) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(time, dateTimeFormater);
        return LocalDateTime.from(parse)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public static Long localDateTimeToLong(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Converts a LocalDateTime object to a formatted String in "yyyy-MM-dd HH:mm:ss" format.
     *
     * @param localDateTime The LocalDateTime object to convert.
     * @return A formatted String representation of the LocalDateTime.
     */
    public static String convertDateToString(LocalDateTime localDateTime) {
        return convertDateToString(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Converts a LocalDateTime object to a formatted String based on the provided format.
     *
     * @param localDateTime The LocalDateTime object to convert.
     * @param formate       The desired date and time format.
     * @return A formatted String representation of the LocalDateTime.
     */
    public static String convertDateToString(LocalDateTime localDateTime, String formate) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern(formate);
        return dateTimeFormater.format(localDateTime);
    }

    /**
     * Converts a date and time String to a LocalDateTime object.
     *
     * @param time The date and time String in "yyyy-MM-dd HH:mm:ss" format.
     * @return A LocalDateTime object representing the parsed date and time.
     */
    public static LocalDateTime convertStringToDate(String time) {
        return convertStringToDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Converts a date and time String to a LocalDateTime object.
     *
     * @return A LocalDateTime object representing the parsed date and time.
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Converts a date and time String to a LocalDateTime object based on the provided format.
     *
     * @param time    The date and time String to convert.
     * @param formate The desired date and time format.
     * @return A LocalDateTime object representing the parsed date and time.
     */
    public static LocalDateTime convertStringToDate(String time, String formate) {
        DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern(formate);
        return LocalDateTime.parse(time, dateTimeFormater);
    }
}
