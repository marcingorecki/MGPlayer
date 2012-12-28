/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mgorecki.cplayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javafx.util.Duration;

/**
 *
 * @author marcin
 */
public class Formatter {
    public static String formatDuration(Duration duration){
        return formatDuration(duration, "HH:mm:ss");
    }
    
    public static String formatDuration(Duration duration, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); //otherwise date will be formatted in current computer timezone
        return sdf.format(new Date((long)duration.toMillis()));
    }
 
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); //otherwise date will be formatted in current computer timezone
        return sdf.format(date);
    }
}
