package holder.bychek.com.fingerholder.Utils;

public class TimeUtils {

    public static String secondsToTime(long totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        long seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String pattern = hours + ":" + minutes + ":" + seconds;
        if (totalSeconds < 0) {
            return "00:00:00";
        }
        if (hours < 10 && (minutes >= 10 && seconds >= 10)) {
            pattern = "0" + hours + ":" + minutes + ":" + seconds;
        }
        if (minutes < 10 && (hours >= 10 && seconds >= 10)) {
            pattern = hours + ":0" + minutes + ":" + seconds;
        }
        if (seconds < 10 && (minutes >= 10 && hours >= 10)) {
            pattern = hours + ":" + minutes + ":0" + seconds;
        }
        if (hours < 10 && minutes < 10 && seconds < 10) {
            pattern = "0" + hours + ":0" + minutes + ":0" + seconds;
        }
        if ((hours < 10 && minutes < 10) && seconds >= 10) {
            pattern = "0" + hours + ":0" + minutes + ":" + seconds;
        }
        if ((hours < 10 && minutes >= 10) && seconds <= 10) {
            pattern = "0" + hours + ":" + minutes + ":0" + seconds;
        }
        return pattern;
    }

    public static long timeToSeconds(String time) {
        long hours = 0;
        long minutes;
        long seconds;
        String[] hourMinSec = time.split(":");
        if (hourMinSec.length == 3){
            hours = Long.parseLong(hourMinSec[0]);
            minutes = Long.parseLong(hourMinSec[1]);
            seconds = Long.parseLong(hourMinSec[2]);

        } else {
            minutes = Long.parseLong(hourMinSec[1]);
            seconds = Long.parseLong(hourMinSec[2]);
        }

        return seconds + (60 * minutes) + (3600 * hours);
    }

    public static String sumOfTwoTime(String first, String second) {
        return TimeUtils.secondsToTime(TimeUtils.timeToSeconds(first) + TimeUtils.timeToSeconds(second));
    }
    public static String sumOfTwoTime(long first, long second) {
        return TimeUtils.secondsToTime(first+second);
    }

    public static long calculateHomMuchFiveMinutesInTime(long time){
        return time / 300;
    }

}
